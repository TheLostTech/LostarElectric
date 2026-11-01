package com.lostar.tv.data

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.io.IOException

class XtreamProvider(private val http: OkHttpClient, private val source: PlaylistSource.Xtream) : Provider {

    private val base = PlaylistSource.normalizeServer(source.server)
    private var liveExtension = "ts"

    private fun api(action: String?, vararg params: Pair<String, String>): String {
        val builder = "$base/player_api.php".toHttpUrl().newBuilder()
            .addQueryParameter("username", source.username)
            .addQueryParameter("password", source.password)
        if (action != null) builder.addQueryParameter("action", action)
        params.forEach { (k, v) -> builder.addQueryParameter(k, v) }
        return builder.build().toString()
    }

    override suspend fun account(): AccountInfo {
        val body = http.getString(api(null))
        val login = XtreamParsers.login(body) ?: throw IOException("Risposta del server non valida")
        if (!login.authenticated) throw IOException("Utente o password non validi")
        liveExtension = if ("m3u8" in login.formats) "m3u8" else "ts"
        return login.account
    }

    override suspend fun loadCatalog(): Catalog = coroutineScope {
        account()
        val liveCats = async { XtreamParsers.categories(http.getString(api("get_live_categories")), Section.LIVE) }
        val vodCats = async { XtreamParsers.categories(http.getString(api("get_vod_categories")), Section.MOVIES) }
        val seriesCats = async { XtreamParsers.categories(http.getString(api("get_series_categories")), Section.SERIES) }
        val live = async { XtreamParsers.liveStreams(http.getString(api("get_live_streams")), base, source, liveExtension) }
        val vod = async { XtreamParsers.vodStreams(http.getString(api("get_vod_streams")), base, source) }
        val series = async { XtreamParsers.series(http.getString(api("get_series"))) }
        XtreamParsers.withFallbackCategories(
            Catalog(
                liveCategories = liveCats.await(),
                channels = live.await(),
                movieCategories = vodCats.await(),
                movies = vod.await(),
                seriesCategories = seriesCats.await(),
                series = series.await(),
            )
        )
    }

    override suspend fun movieDetail(movie: Movie): MovieDetail =
        XtreamParsers.movieDetail(http.getString(api("get_vod_info", "vod_id" to movie.id)))

    override suspend fun seriesDetail(series: Series, catalog: Catalog): SeriesDetail =
        XtreamParsers.seriesDetail(http.getString(api("get_series_info", "series_id" to series.id)), base, source)

    override fun xmltvUrl(catalog: Catalog): String =
        "$base/xmltv.php".toHttpUrl().newBuilder()
            .addQueryParameter("username", source.username)
            .addQueryParameter("password", source.password)
            .build().toString()

    override suspend fun shortEpg(channel: Channel): List<EpgProgram> =
        XtreamParsers.shortEpg(http.getString(api("get_short_epg", "stream_id" to channel.id, "limit" to "6")))
}
