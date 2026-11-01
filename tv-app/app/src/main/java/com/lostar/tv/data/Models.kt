package com.lostar.tv.data

import kotlinx.serialization.Serializable

@Serializable
enum class Section { LIVE, MOVIES, SERIES }

@Serializable
data class Category(
    val id: String,
    val name: String,
    val section: Section,
)

@Serializable
data class Channel(
    val id: String,
    val name: String,
    val logo: String? = null,
    val categoryId: String,
    val url: String,
    val epgId: String? = null,
    val number: Int = 0,
)

@Serializable
data class Movie(
    val id: String,
    val name: String,
    val poster: String? = null,
    val categoryId: String,
    val url: String,
    val rating: Double? = null,
    val year: String? = null,
    val added: Long = 0,
)

@Serializable
data class Series(
    val id: String,
    val name: String,
    val cover: String? = null,
    val categoryId: String,
    val plot: String? = null,
    val genre: String? = null,
    val rating: Double? = null,
    val year: String? = null,
    val backdrop: String? = null,
    val added: Long = 0,
)

@Serializable
data class Episode(
    val id: String,
    val title: String,
    val season: Int,
    val episode: Int,
    val url: String,
    val plot: String? = null,
    val image: String? = null,
    val durationSecs: Int? = null,
)

data class MovieDetail(
    val plot: String? = null,
    val backdrop: String? = null,
    val genre: String? = null,
    val durationSecs: Int? = null,
    val director: String? = null,
    val cast: String? = null,
    val year: String? = null,
    val rating: Double? = null,
)

data class SeriesDetail(
    val plot: String? = null,
    val backdrop: String? = null,
    val genre: String? = null,
    val cast: String? = null,
    val seasons: Map<Int, List<Episode>> = emptyMap(),
)

data class EpgProgram(
    val title: String,
    val description: String?,
    val startMs: Long,
    val endMs: Long,
) {
    fun progressAt(nowMs: Long): Float =
        if (endMs <= startMs) 0f else ((nowMs - startMs).toFloat() / (endMs - startMs)).coerceIn(0f, 1f)
}

/** Account info returned by Xtream panels (expiry, connections). */
data class AccountInfo(
    val status: String?,
    val expiresAtMs: Long?,
    val maxConnections: Int?,
    val activeConnections: Int?,
)

/** Whole provider catalog. Series episodes from M3U lists live in [m3uEpisodes]; Xtream loads them on demand. */
@Serializable
data class Catalog(
    val liveCategories: List<Category> = emptyList(),
    val channels: List<Channel> = emptyList(),
    val movieCategories: List<Category> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val seriesCategories: List<Category> = emptyList(),
    val series: List<Series> = emptyList(),
    val m3uEpisodes: Map<String, List<Episode>> = emptyMap(),
    val epgUrl: String? = null,
) {
    val channelsByCategory: Map<String, List<Channel>> by lazy { channels.groupBy { it.categoryId } }

    val moviesByCategory: Map<String, List<Movie>> by lazy { movies.groupBy { it.categoryId } }

    val seriesByCategory: Map<String, List<Series>> by lazy { series.groupBy { it.categoryId } }

    val channelById: Map<String, Channel> by lazy { channels.associateBy { it.id } }

    val movieById: Map<String, Movie> by lazy { movies.associateBy { it.id } }

    val seriesById: Map<String, Series> by lazy { series.associateBy { it.id } }

    /** Categories that actually contain something, in provider order. */
    fun nonEmptyLive() = liveCategories.filter { channelsByCategory[it.id].orEmpty().isNotEmpty() }
    fun nonEmptyMovies() = movieCategories.filter { moviesByCategory[it.id].orEmpty().isNotEmpty() }
    fun nonEmptySeries() = seriesCategories.filter { seriesByCategory[it.id].orEmpty().isNotEmpty() }

    val isEmpty: Boolean get() = channels.isEmpty() && movies.isEmpty() && series.isEmpty()
}
