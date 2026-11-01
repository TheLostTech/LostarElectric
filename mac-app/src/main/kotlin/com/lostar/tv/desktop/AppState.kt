package com.lostar.tv.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.data.Episode
import com.lostar.tv.data.Movie
import com.lostar.tv.data.Section
import com.lostar.tv.data.Series
import com.lostar.tv.data.StreamHealth
import com.lostar.tv.desktop.video.LiveSession
import com.lostar.tv.desktop.video.VideoPlayer
import com.lostar.tv.ui.Format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

enum class Page(val label: String) { HOME("Home"), LIVE("TV in diretta"), MOVIES("Film"), SERIES("Serie"), FAVORITES("Preferiti"), SETTINGS("Impostazioni") }

sealed interface Detail {
    data class OfMovie(val id: String) : Detail
    data class OfSeries(val id: String) : Detail
}

data class VodItem(
    val key: String,
    val section: Section,
    val title: String,
    val subtitle: String?,
    val url: String,
    val image: String?,
    val seriesId: String? = null,
)

data class VodRequest(val queue: List<VodItem>, val index: Int, val startMs: Long = 0)

fun Movie.toVodItem() = VodItem(DesktopStore.movieKey(id), Section.MOVIES, Format.cleanTitle(name), year, url, poster)

fun Episode.toVodItem(series: Series) = VodItem(
    key = DesktopStore.episodeKey(id),
    section = Section.SERIES,
    title = Format.cleanTitle(series.name),
    subtitle = "S$season · E$episode" + title.takeIf { it.isNotBlank() && !it.contains(series.name, true) }?.let { " · $it" }.orEmpty(),
    url = url,
    image = image ?: series.backdrop ?: series.cover,
    seriesId = series.id,
)

fun WatchEntry.toVodItem() = VodItem(key, section, title, subtitle, url, image, seriesId)

class AppState {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val dataDir = File(System.getProperty("user.home"), "Library/Application Support/LostTV").apply { mkdirs() }

    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val req = chain.request()
            chain.proceed(if (req.header("User-Agent") == null) req.newBuilder().header("User-Agent", USER_AGENT).build() else req)
        }
        .build()

    val store = DesktopStore(dataDir, scope)
    val catalog = CatalogRepository(http, File(dataDir, "cache").apply { mkdirs() }, CoroutineScope(SupervisorJob() + Dispatchers.Default))
    val health = StreamHealth(File(dataDir, "stream_health.json"))

    val livePlayer: VideoPlayer by lazy { VideoPlayer(scope, USER_AGENT).also { it.setVolume(store.data.value.volume) } }
    val live: LiveSession by lazy { LiveSession(scope, livePlayer, health) { store.pushRecentChannel(it.id) } }

    var page by mutableStateOf(Page.HOME)
    var query by mutableStateOf("")
    var detail by mutableStateOf<Detail?>(null)
    var vod by mutableStateOf<VodRequest?>(null)
    var liveFullscreen by mutableStateOf(false)

    var window: WindowState? = null

    /** Screens that want keyboard shortcuts install a handler here. */
    var keyHandler: ((KeyEvent) -> Boolean)? = null

    fun open(p: Page) {
        page = p
        query = ""
        detail = null
    }

    fun playVod(request: VodRequest) {
        if (live.phase != LiveSession.Phase.IDLE) live.stop() // one stream at a time (connection limits)
        vod = request
    }

    fun toggleWindowFullscreen() {
        val w = window ?: return
        w.placement = if (w.placement == WindowPlacement.Fullscreen) WindowPlacement.Floating else WindowPlacement.Fullscreen
    }

    fun shutdown() {
        runCatching { if (live.phase != LiveSession.Phase.IDLE) live.stop() }
        health.saveIfNeeded()
    }

    companion object {
        const val USER_AGENT = "VLC/3.0.20 LibVLC/3.0.20"
    }
}
