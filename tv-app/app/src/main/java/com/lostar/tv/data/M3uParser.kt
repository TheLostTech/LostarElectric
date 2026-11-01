package com.lostar.tv.data

/**
 * Streaming parser for `#EXTM3U` playlists (including the `m3u_plus` flavour used by paid providers).
 * Entries are split into live channels, movies and series using the URL layout and the title.
 */
object M3uParser {
    private val attrRegex = Regex("""([A-Za-z0-9_-]+)="([^"]*)"""")
    private val episodeRegex = Regex("""^(.*?)[\s._\-|]*[Ss](\d{1,2})[\s._\-]*[Ee](\d{1,3})\b.*$""")
    private val movieExtensions = setOf("mp4", "mkv", "avi", "mov", "m4v", "wmv", "flv", "webm")

    private const val NO_GROUP = "Senza categoria"

    fun parse(lines: Sequence<String>): Catalog {
        val liveCats = LinkedHashMap<String, Category>()
        val movieCats = LinkedHashMap<String, Category>()
        val seriesCats = LinkedHashMap<String, Category>()
        val channels = ArrayList<Channel>()
        val movies = ArrayList<Movie>()
        val seriesMap = LinkedHashMap<String, Series>()
        val episodes = HashMap<String, MutableList<Episode>>()
        var epgUrl: String? = null

        var pendingInfo: String? = null
        var pendingGroup: String? = null
        var index = 0

        for (raw in lines) {
            val line = raw.trim().removePrefix("﻿")
            if (line.isEmpty()) continue
            when {
                line.startsWith("#EXTM3U") -> {
                    val attrs = attributes(line)
                    epgUrl = attrs["url-tvg"] ?: attrs["x-tvg-url"]
                }
                line.startsWith("#EXTINF") -> {
                    pendingInfo = line
                    pendingGroup = null
                }
                line.startsWith("#EXTGRP:") -> pendingGroup = line.removePrefix("#EXTGRP:").trim()
                line.startsWith("#") -> Unit
                else -> {
                    val info = pendingInfo ?: continue
                    pendingInfo = null
                    val attrs = attributes(info)
                    val title = displayName(info).ifBlank { attrs["tvg-name"].orEmpty() }.ifBlank { "Canale ${index + 1}" }
                    val group = (attrs["group-title"] ?: pendingGroup)?.trim().takeUnless { it.isNullOrEmpty() } ?: NO_GROUP
                    val logo = attrs["tvg-logo"]?.takeIf { it.isNotBlank() }
                    val url = line
                    val id = "m$index"
                    index++

                    val episodeMatch = episodeRegex.matchEntire(title)
                    when (classify(url, episodeMatch != null)) {
                        Section.LIVE -> {
                            val cat = liveCats.getOrPut(group) { Category("live:$group", group, Section.LIVE) }
                            channels += Channel(
                                id = id, name = title, logo = logo, categoryId = cat.id, url = url,
                                epgId = attrs["tvg-id"]?.takeIf { it.isNotBlank() },
                                number = attrs["tvg-chno"]?.toIntOrNull() ?: channels.size + 1,
                            )
                        }
                        Section.MOVIES -> {
                            val cat = movieCats.getOrPut(group) { Category("movie:$group", group, Section.MOVIES) }
                            movies += Movie(id = id, name = title, poster = logo, categoryId = cat.id, url = url, added = index.toLong())
                        }
                        Section.SERIES -> {
                            val cat = seriesCats.getOrPut(group) { Category("series:$group", group, Section.SERIES) }
                            val showName = episodeMatch?.groupValues?.get(1)?.trim()?.trimEnd('-', '|', '.', ':')?.trim()
                                .takeUnless { it.isNullOrEmpty() } ?: title
                            val season = episodeMatch?.groupValues?.get(2)?.toIntOrNull() ?: 1
                            val number = episodeMatch?.groupValues?.get(3)?.toIntOrNull() ?: (episodes[showName]?.size ?: 0) + 1
                            val seriesId = "s:" + showName.lowercase()
                            seriesMap.getOrPut(seriesId) {
                                Series(id = seriesId, name = showName, cover = logo, categoryId = cat.id, added = index.toLong())
                            }
                            episodes.getOrPut(seriesId) { mutableListOf() } += Episode(
                                id = id, title = title, season = season, episode = number, url = url, image = logo,
                            )
                        }
                    }
                }
            }
        }

        return Catalog(
            liveCategories = liveCats.values.toList(),
            channels = channels,
            movieCategories = movieCats.values.toList(),
            movies = movies,
            seriesCategories = seriesCats.values.toList(),
            series = seriesMap.values.toList(),
            m3uEpisodes = episodes.mapValues { (_, list) -> list.sortedWith(compareBy({ it.season }, { it.episode })) },
            epgUrl = epgUrl,
        )
    }

    internal fun classify(url: String, looksLikeEpisode: Boolean): Section {
        val path = url.substringBefore('?').lowercase()
        if ("/series/" in path) return Section.SERIES
        if ("/movie/" in path || "/movies/" in path) return Section.MOVIES
        val ext = path.substringAfterLast('/').substringAfterLast('.', "")
        if (ext in movieExtensions) return if (looksLikeEpisode) Section.SERIES else Section.MOVIES
        return Section.LIVE
    }

    internal fun attributes(line: String): Map<String, String> =
        attrRegex.findAll(line).associate { it.groupValues[1].lowercase() to it.groupValues[2] }

    /** Title is whatever follows the first comma that is not inside a quoted attribute. */
    internal fun displayName(info: String): String {
        var inQuotes = false
        for (i in info.indices) {
            when (info[i]) {
                '"' -> inQuotes = !inQuotes
                ',' -> if (!inQuotes) return info.substring(i + 1).trim()
            }
        }
        return ""
    }
}
