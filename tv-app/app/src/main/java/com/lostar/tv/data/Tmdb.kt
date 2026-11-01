package com.lostar.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/** Poster, backdrop, rating and Italian plot from The Movie Database (themoviedb.org). */
@Serializable
data class TmdbInfo(
    val found: Boolean = true,
    val id: Int = 0,
    val title: String? = null,
    val poster: String? = null,
    val backdrop: String? = null,
    val rating: Double? = null,
    val votes: Int = 0,
    val overview: String? = null,
    val year: String? = null,
) {
    val ratingLabel: String? get() = rating?.takeIf { it > 0 && votes >= 5 }?.let { "%.1f".format(java.util.Locale.ITALY, it) }
}

class TmdbClient(
    private val http: OkHttpClient,
    private val cacheFile: File?,
    private val apiKey: () -> String?,
) {
    enum class Kind { MOVIE, TV }

    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = MapSerializer(String.serializer(), TmdbInfo.serializer())
    private val cache = ConcurrentHashMap<String, TmdbInfo>()
    private val limiter = Semaphore(4)
    @Volatile private var dirty = 0

    init {
        val f = cacheFile
        if (f != null && f.exists()) runCatching { cache.putAll(json.decodeFromString(serializer, f.readText())) }
    }

    val enabled: Boolean get() = !apiKey().isNullOrBlank()

    private fun cacheKey(kind: Kind, q: TitleCleaner.Query) = "$kind|${TitleCleaner.normalize(q.text)}|${q.year.orEmpty()}"

    /** Instant answer from the cache (no network), for first composition. */
    fun peek(kind: Kind, rawTitle: String, yearHint: String? = null): TmdbInfo? {
        val q = TitleCleaner.forSearch(rawTitle).let { if (yearHint != null) it.copy(year = yearHint) else it }
        return cache[cacheKey(kind, q)]?.takeIf { it.found }
    }

    suspend fun lookup(kind: Kind, rawTitle: String, yearHint: String? = null): TmdbInfo? {
        val key = apiKey()?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val q = TitleCleaner.forSearch(rawTitle).let { if (yearHint != null && it.year == null) it.copy(year = yearHint) else it }
        if (q.text.length < 2) return null
        val ck = cacheKey(kind, q)
        cache[ck]?.let { return it.takeIf { i -> i.found } }
        val info = limiter.withPermit {
            runCatching {
                search(key, kind, q.text, q.year) ?: if (q.year != null) search(key, kind, q.text, null) else null
            }
        }.getOrElse { return null } // network problem: don't remember a miss
        val stored = info ?: TmdbInfo(found = false)
        cache[ck] = stored
        if (++dirty % 20 == 0) save()
        return stored.takeIf { it.found }
    }

    private suspend fun search(key: String, kind: Kind, query: String, year: String?): TmdbInfo? = withContext(Dispatchers.IO) {
        val path = if (kind == Kind.MOVIE) "movie" else "tv"
        val bearer = key.startsWith("eyJ")
        val url = "https://api.themoviedb.org/3/search/$path".toHttpUrl().newBuilder()
            .addQueryParameter("query", query)
            .addQueryParameter("language", "it-IT")
            .addQueryParameter("include_adult", "false")
            .apply {
                if (year != null) addQueryParameter(if (kind == Kind.MOVIE) "year" else "first_air_date_year", year)
                if (!bearer) addQueryParameter("api_key", key)
            }
            .build()
        val request = Request.Builder().url(url).apply { if (bearer) header("Authorization", "Bearer $key") }.build()
        val body = http.newCall(request).execute().use { r ->
            if (r.code == 401) throw IllegalStateException("Chiave TMDB non valida")
            if (!r.isSuccessful) throw java.io.IOException("TMDB ${r.code}")
            r.body?.string().orEmpty()
        }
        parseSearch(body, query)
    }

    fun save() {
        val f = cacheFile ?: return
        runCatching {
            val tmp = File(f.parentFile, f.name + ".tmp")
            tmp.writeText(json.encodeToString(serializer, HashMap(cache)))
            tmp.renameTo(f)
        }
    }

    companion object {
        private const val IMG = "https://image.tmdb.org/t/p/"

        /** Picks the result whose title matches best, falling back to the most popular one. */
        internal fun parseSearch(body: String, query: String): TmdbInfo? {
            val root = runCatching { Json.parseToJsonElement(body) }.getOrNull() as? JsonObject ?: return null
            val results = (root["results"] as? JsonArray)?.filterIsInstance<JsonObject>().orEmpty()
            if (results.isEmpty()) return null
            val nq = TitleCleaner.normalize(query)
            fun str(o: JsonObject, k: String) = (o[k] as? JsonPrimitive)?.takeIf { it.isString }?.content?.takeIf { it.isNotBlank() }
            fun num(o: JsonObject, k: String) = (o[k] as? JsonPrimitive)?.content?.toDoubleOrNull()
            fun titles(o: JsonObject) = listOfNotNull(str(o, "title"), str(o, "name"), str(o, "original_title"), str(o, "original_name"))
            val best = results.take(8).firstOrNull { o -> titles(o).any { TitleCleaner.normalize(it) == nq } }
                ?: results.first()
            val date = str(best, "release_date") ?: str(best, "first_air_date")
            return TmdbInfo(
                id = num(best, "id")?.toInt() ?: 0,
                title = str(best, "title") ?: str(best, "name"),
                poster = str(best, "poster_path")?.let { IMG + "w342" + it },
                backdrop = str(best, "backdrop_path")?.let { IMG + "w1280" + it },
                rating = num(best, "vote_average"),
                votes = num(best, "vote_count")?.toInt() ?: 0,
                overview = str(best, "overview"),
                year = date?.take(4),
            )
        }
    }
}
