package com.lostar.tv.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class CatalogRepository(
    private val http: OkHttpClient,
    private val cacheDir: File,
    private val scope: CoroutineScope,
) {
    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Ready(val catalog: Catalog, val refreshing: Boolean = false) : State
        data class Error(val message: String) : State
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _account = MutableStateFlow<AccountInfo?>(null)
    val account: StateFlow<AccountInfo?> = _account.asStateFlow()

    private var source: PlaylistSource? = null
    private var provider: Provider? = null
    private var loadJob: Job? = null

    private val epgCache = ConcurrentHashMap<String, Pair<Long, List<EpgProgram>>>()
    private val epgVariant = ConcurrentHashMap<String, String>()
    private val epgLimiter = Semaphore(4)
    private val movieDetailCache = ConcurrentHashMap<String, MovieDetail>()
    private val seriesDetailCache = ConcurrentHashMap<String, SeriesDetail>()

    val currentCatalog: Catalog? get() = (_state.value as? State.Ready)?.catalog

    fun providerFor(source: PlaylistSource): Provider = when (source) {
        is PlaylistSource.Xtream -> XtreamProvider(http, source)
        is PlaylistSource.M3u -> M3uProvider(http, source)
    }

    /** Called whenever the saved source changes. Shows the cached catalog at once, then refreshes it. */
    fun activate(newSource: PlaylistSource) {
        if (newSource == source && _state.value !is State.Error && _state.value !is State.Idle) return
        source = newSource
        provider = providerFor(newSource)
        epgCache.clear(); epgVariant.clear(); movieDetailCache.clear(); seriesDetailCache.clear()
        loadJob?.cancel()
        loadJob = scope.launch {
            val cached = readCache(newSource)
            cached?.first?.let { warm(it) }
            if (cached != null) {
                _state.value = State.Ready(cached.first, refreshing = true)
                if (System.currentTimeMillis() - cached.second < CACHE_FRESH_MS) {
                    _state.value = State.Ready(cached.first, refreshing = false)
                    refreshAccount()
                    return@launch
                }
            } else {
                _state.value = State.Loading
            }
            fetch(newSource, hadCache = cached != null)
        }
    }

    fun reload() {
        val s = source ?: return
        loadJob?.cancel()
        loadJob = scope.launch {
            val current = currentCatalog
            _state.value = if (current != null) State.Ready(current, refreshing = true) else State.Loading
            fetch(s, hadCache = current != null)
        }
    }

    fun deactivate() {
        loadJob?.cancel()
        source?.let { cacheFile(it).delete() }
        source = null
        provider = null
        _account.value = null
        _state.value = State.Idle
    }

    private suspend fun fetch(s: PlaylistSource, hadCache: Boolean) {
        val p = provider ?: return
        try {
            val catalog = p.loadCatalog()
            if (catalog.isEmpty) throw IllegalStateException("La lista non contiene canali o contenuti")
            warm(catalog)
            _state.value = State.Ready(catalog)
            writeCache(s, catalog)
            refreshAccount()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            val current = currentCatalog
            _state.value = if (hadCache && current != null) State.Ready(current) else State.Error(friendly(e))
        }
    }

    /** Builds the lazy indexes (duplicate merging, category maps) off the main thread. */
    private suspend fun warm(c: Catalog) = withContext(Dispatchers.Default) {
        c.live
        c.moviesByCategory
        c.seriesByCategory
        c.movieById
        c.seriesById
    }

    private fun refreshAccount() {
        val p = provider ?: return
        scope.launch { _account.value = runCatching { p.account() }.getOrNull() }
    }

    /** Used by the setup screen to validate credentials before saving them. */
    suspend fun test(source: PlaylistSource): Result<Unit> = runCatching {
        when (source) {
            is PlaylistSource.Xtream -> XtreamProvider(http, source).account()
            is PlaylistSource.M3u -> http.getLines(source.url) { lines ->
                val head = lines.take(5).joinToString("\n")
                if (!head.contains("#EXTM3U") && !head.contains("#EXTINF")) throw IllegalStateException("Il link non è una lista M3U")
            }
        }
        Unit
    }.recoverCatching { throw IllegalStateException(friendly(it)) }

    suspend fun movieDetail(movie: Movie): MovieDetail =
        movieDetailCache[movie.id] ?: runCatching { provider?.movieDetail(movie) }.getOrNull()
            ?.also { movieDetailCache[movie.id] = it } ?: MovieDetail()

    suspend fun seriesDetail(series: Series): SeriesDetail {
        seriesDetailCache[series.id]?.let { return it }
        val catalog = currentCatalog ?: return SeriesDetail()
        val detail = provider?.seriesDetail(series, catalog) ?: SeriesDetail()
        if (detail.seasons.isNotEmpty()) seriesDetailCache[series.id] = detail
        return detail
    }

    suspend fun epg(channel: Channel): List<EpgProgram> {
        val now = System.currentTimeMillis()
        epgCache[channel.id]?.let { (at, list) ->
            if (now - at < EPG_TTL_MS && list.any { it.endMs > now }) return list.filter { it.endMs > now }
        }
        val list = epgLimiter.withPermit { runCatching { provider?.shortEpg(channel) }.getOrNull().orEmpty() }
        epgCache[channel.id] = now to list
        return list.filter { it.endMs > now }
    }

    /** Guide for a merged channel: uses the first link that has one (links with an EPG id first). */
    suspend fun epg(channel: LiveChannel): List<EpgProgram> {
        epgVariant[channel.id]?.let { id -> channel.variants.firstOrNull { it.id == id }?.let { v -> return epg(v) } }
        val candidates = channel.variants.sortedByDescending { !it.epgId.isNullOrBlank() }.take(3)
        for (v in candidates) {
            val list = epg(v)
            if (list.isNotEmpty()) {
                epgVariant[channel.id] = v.id
                return list
            }
        }
        return emptyList()
    }

    suspend fun nowPlaying(channel: LiveChannel): EpgProgram? {
        val now = System.currentTimeMillis()
        return epg(channel).firstOrNull { it.startMs <= now && it.endMs > now }
    }

    private fun cacheFile(s: PlaylistSource) = File(cacheDir, "catalog-${s.hashCode().toUInt()}.json")

    private suspend fun readCache(s: PlaylistSource): Pair<Catalog, Long>? = withContext(Dispatchers.IO) {
        val f = cacheFile(s)
        if (!f.exists()) return@withContext null
        runCatching { json.decodeFromString(Catalog.serializer(), f.readText()) to f.lastModified() }.getOrNull()
    }

    private suspend fun writeCache(s: PlaylistSource, c: Catalog) = withContext(Dispatchers.IO) {
        runCatching {
            cacheDir.listFiles { f -> f.name.startsWith("catalog-") }?.forEach { it.delete() }
            val tmp = File(cacheDir, "catalog.tmp")
            tmp.writeText(json.encodeToString(Catalog.serializer(), c))
            tmp.renameTo(cacheFile(s))
        }
    }

    private fun friendly(e: Throwable): String = when (e) {
        is HttpException -> when (e.code) {
            401, 403 -> "Accesso negato dal server (credenziali errate o abbonamento scaduto)"
            404 -> "Indirizzo non trovato: controlla il link o il server"
            else -> "Il server ha risposto con errore ${e.code}"
        }
        is java.net.UnknownHostException -> "Server non raggiungibile: controlla l'indirizzo e la connessione"
        is java.net.SocketTimeoutException -> "Il server non risponde (timeout)"
        is java.net.ConnectException -> "Connessione rifiutata dal server"
        is IllegalArgumentException -> "Indirizzo non valido"
        else -> e.message ?: "Errore sconosciuto"
    }

    companion object {
        private const val CACHE_FRESH_MS = 6 * 60 * 60 * 1000L
        private const val EPG_TTL_MS = 10 * 60 * 1000L
    }
}
