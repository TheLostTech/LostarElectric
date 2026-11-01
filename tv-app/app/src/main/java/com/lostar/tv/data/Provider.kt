package com.lostar.tv.data

interface Provider {
    suspend fun loadCatalog(): Catalog
    suspend fun movieDetail(movie: Movie): MovieDetail
    suspend fun seriesDetail(series: Series, catalog: Catalog): SeriesDetail
    suspend fun shortEpg(channel: Channel): List<EpgProgram>
    suspend fun account(): AccountInfo?
}
