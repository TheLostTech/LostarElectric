package com.lostar.tv.data

import okhttp3.OkHttpClient

class M3uProvider(private val http: OkHttpClient, private val source: PlaylistSource.M3u) : Provider {
    override suspend fun loadCatalog(): Catalog = http.getLines(source.url) { M3uParser.parse(it) }

    override suspend fun movieDetail(movie: Movie) = MovieDetail()

    override suspend fun seriesDetail(series: Series, catalog: Catalog): SeriesDetail =
        SeriesDetail(seasons = catalog.m3uEpisodes[series.id].orEmpty().groupBy { it.season }.toSortedMap())

    override suspend fun shortEpg(channel: Channel): List<EpgProgram> = emptyList()

    override suspend fun account(): AccountInfo? = null
}
