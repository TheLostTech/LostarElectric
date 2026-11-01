package com.lostar.tv.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Xtream Codes panels are loosely typed (numbers as strings, `[]` instead of `{}` when empty, nulls…),
 * so everything is read through [JsonElement] with tolerant accessors instead of strict data classes.
 */
internal object XtreamParsers {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val yearRegex = Regex("""\((\d{4})\)""")
    private val anyYearRegex = Regex("""\b(19\d{2}|20\d{2})\b""")

    data class Login(val authenticated: Boolean, val formats: List<String>, val account: AccountInfo)

    fun login(body: String): Login? {
        val root = parse(body) as? JsonObject ?: return null
        val user = root.obj("user_info") ?: return null
        val auth = user.str("auth")
        return Login(
            authenticated = auth == "1" || auth.equals("true", ignoreCase = true),
            formats = user.arr("allowed_output_formats")?.mapNotNull { (it as? JsonPrimitive)?.content } ?: emptyList(),
            account = AccountInfo(
                status = user.str("status"),
                expiresAtMs = user.long("exp_date")?.let { it * 1000 },
                maxConnections = user.int("max_connections"),
                activeConnections = user.int("active_cons"),
            ),
        )
    }

    fun categories(body: String, section: Section): List<Category> {
        val prefix = prefix(section)
        return items(body).mapNotNull { o ->
            val id = o.str("category_id") ?: return@mapNotNull null
            Category("$prefix$id", o.str("category_name")?.trim().orEmpty().ifEmpty { "Categoria $id" }, section)
        }
    }

    fun liveStreams(body: String, base: String, src: PlaylistSource.Xtream, ext: String): List<Channel> =
        items(body).mapIndexedNotNull { i, o ->
            val id = o.str("stream_id") ?: return@mapIndexedNotNull null
            Channel(
                id = id,
                name = o.str("name")?.trim().orEmpty(),
                logo = o.str("stream_icon").nullIfBlank(),
                categoryId = prefix(Section.LIVE) + (o.categoryId() ?: "0"),
                url = "$base/live/${src.username}/${src.password}/$id.$ext",
                epgId = o.str("epg_channel_id").nullIfBlank(),
                number = o.int("num") ?: (i + 1),
            )
        }

    fun vodStreams(body: String, base: String, src: PlaylistSource.Xtream): List<Movie> =
        items(body).mapNotNull { o ->
            val id = o.str("stream_id") ?: return@mapNotNull null
            val name = o.str("name")?.trim().orEmpty()
            val ext = o.str("container_extension").nullIfBlank() ?: "mp4"
            Movie(
                id = id,
                name = name,
                poster = o.str("stream_icon").nullIfBlank(),
                categoryId = prefix(Section.MOVIES) + (o.categoryId() ?: "0"),
                url = "$base/movie/${src.username}/${src.password}/$id.$ext",
                rating = o.double("rating")?.takeIf { it > 0 },
                year = o.str("year").nullIfBlank() ?: yearFrom(name, o.str("releasedate")),
                added = o.long("added") ?: 0,
            )
        }

    fun series(body: String): List<Series> =
        items(body).mapNotNull { o ->
            val id = o.str("series_id") ?: return@mapNotNull null
            val name = o.str("name")?.trim().orEmpty()
            Series(
                id = id,
                name = name,
                cover = o.str("cover").nullIfBlank(),
                categoryId = prefix(Section.SERIES) + (o.categoryId() ?: "0"),
                plot = o.str("plot").nullIfBlank(),
                genre = o.str("genre").nullIfBlank(),
                rating = o.double("rating")?.takeIf { it > 0 },
                year = o.str("year").nullIfBlank() ?: yearFrom(name, o.str("releaseDate") ?: o.str("release_date")),
                backdrop = o.firstString("backdrop_path"),
                added = o.long("last_modified") ?: 0,
            )
        }

    fun movieDetail(body: String): MovieDetail {
        val root = parse(body) as? JsonObject ?: return MovieDetail()
        val info = root.obj("info") ?: return MovieDetail()
        return MovieDetail(
            plot = (info.str("plot") ?: info.str("description")).nullIfBlank(),
            backdrop = info.firstString("backdrop_path") ?: info.str("cover_big").nullIfBlank(),
            genre = info.str("genre").nullIfBlank(),
            durationSecs = info.int("duration_secs"),
            director = info.str("director").nullIfBlank(),
            cast = (info.str("cast") ?: info.str("actors")).nullIfBlank(),
            year = yearFrom("", info.str("releasedate") ?: info.str("release_date")),
            rating = info.double("rating")?.takeIf { it > 0 },
        )
    }

    fun seriesDetail(body: String, base: String, src: PlaylistSource.Xtream): SeriesDetail {
        val root = parse(body) as? JsonObject ?: return SeriesDetail()
        val info = root.obj("info")
        val episodesElement = root["episodes"]
        val rawEpisodes: List<JsonObject> = when (episodesElement) {
            is JsonObject -> episodesElement.values.flatMap { (it as? JsonArray).orEmpty().filterIsInstance<JsonObject>() }
            is JsonArray -> episodesElement.flatMap { el ->
                when (el) {
                    is JsonArray -> el.filterIsInstance<JsonObject>()
                    is JsonObject -> listOf(el)
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
        val episodes = rawEpisodes.mapNotNull { e ->
            val id = e.str("id") ?: return@mapNotNull null
            val epInfo = e.obj("info")
            val ext = e.str("container_extension").nullIfBlank() ?: "mp4"
            Episode(
                id = id,
                title = e.str("title")?.trim().orEmpty(),
                season = e.int("season") ?: 1,
                episode = e.int("episode_num") ?: 0,
                url = "$base/series/${src.username}/${src.password}/$id.$ext",
                plot = epInfo?.str("plot").nullIfBlank(),
                image = epInfo?.str("movie_image").nullIfBlank(),
                durationSecs = epInfo?.int("duration_secs"),
            )
        }
        return SeriesDetail(
            plot = info?.str("plot").nullIfBlank(),
            backdrop = info?.firstString("backdrop_path"),
            genre = info?.str("genre").nullIfBlank(),
            cast = info?.str("cast").nullIfBlank(),
            seasons = episodes.groupBy { it.season }.toSortedMap().mapValues { (_, l) -> l.sortedBy { it.episode } },
        )
    }

    fun shortEpg(body: String): List<EpgProgram> {
        val root = parse(body) as? JsonObject ?: return emptyList()
        return root.arr("epg_listings").orEmpty().filterIsInstance<JsonObject>().mapNotNull { o ->
            val start = o.long("start_timestamp") ?: return@mapNotNull null
            val end = o.long("stop_timestamp") ?: o.long("end_timestamp") ?: return@mapNotNull null
            EpgProgram(
                title = Base64Lite.decodeToStringOrNull(o.str("title"))?.trim().orEmpty().ifEmpty { o.str("title").orEmpty() },
                description = Base64Lite.decodeToStringOrNull(o.str("description"))?.trim().nullIfBlank(),
                startMs = start * 1000,
                endMs = end * 1000,
            )
        }.sortedBy { it.startMs }
    }

    /** Adds a category for items whose category id is not declared by the panel. */
    fun withFallbackCategories(c: Catalog): Catalog {
        fun fix(cats: List<Category>, used: Set<String>, section: Section): List<Category> {
            val known = cats.mapTo(HashSet()) { it.id }
            val missing = used.filter { it !in known }.map { Category(it, "Altro", section) }
            return cats + missing
        }
        return c.copy(
            liveCategories = fix(c.liveCategories, c.channels.mapTo(LinkedHashSet()) { it.categoryId }, Section.LIVE),
            movieCategories = fix(c.movieCategories, c.movies.mapTo(LinkedHashSet()) { it.categoryId }, Section.MOVIES),
            seriesCategories = fix(c.seriesCategories, c.series.mapTo(LinkedHashSet()) { it.categoryId }, Section.SERIES),
        )
    }

    private fun prefix(section: Section) = when (section) {
        Section.LIVE -> "live:"
        Section.MOVIES -> "movie:"
        Section.SERIES -> "series:"
    }

    private fun yearFrom(name: String, date: String?): String? =
        yearRegex.find(name)?.groupValues?.get(1)
            ?: date?.let { anyYearRegex.find(it)?.groupValues?.get(1) }

    private fun parse(body: String): JsonElement? = try {
        json.parseToJsonElement(body)
    } catch (_: Exception) {
        null
    }

    private fun items(body: String): List<JsonObject> = when (val root = parse(body)) {
        is JsonArray -> root.filterIsInstance<JsonObject>()
        is JsonObject -> root.values.filterIsInstance<JsonObject>()
        else -> emptyList()
    }

    private fun JsonObject.categoryId(): String? =
        str("category_id") ?: arr("category_ids")?.firstOrNull()?.let { (it as? JsonPrimitive)?.content }

    private fun JsonObject.obj(key: String): JsonObject? = this[key] as? JsonObject
    private fun JsonObject.arr(key: String): JsonArray? = this[key] as? JsonArray

    fun JsonObject.str(key: String): String? {
        val v = this[key] ?: return null
        if (v is JsonNull) return null
        return (v as? JsonPrimitive)?.content
    }

    private fun JsonObject.long(key: String): Long? = str(key)?.trim()?.let { it.toLongOrNull() ?: it.toDoubleOrNull()?.toLong() }
    private fun JsonObject.int(key: String): Int? = long(key)?.toInt()
    private fun JsonObject.double(key: String): Double? = str(key)?.trim()?.replace(',', '.')?.toDoubleOrNull()

    private fun JsonObject.firstString(key: String): String? = when (val v = this[key]) {
        is JsonArray -> v.firstNotNullOfOrNull { (it as? JsonPrimitive)?.content.nullIfBlank() }
        is JsonPrimitive -> v.content.nullIfBlank()
        else -> null
    }

    private fun String?.nullIfBlank(): String? = this?.takeIf { it.isNotBlank() && it != "null" }
}
