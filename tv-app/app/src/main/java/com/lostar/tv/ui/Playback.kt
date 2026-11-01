package com.lostar.tv.ui

import com.lostar.tv.data.Episode
import com.lostar.tv.data.Movie
import com.lostar.tv.data.Section
import com.lostar.tv.data.Series
import com.lostar.tv.data.UserStore
import com.lostar.tv.data.WatchEntry
import com.lostar.tv.ui.player.VodItem

fun Movie.toVodItem() = VodItem(
    key = UserStore.movieKey(id),
    section = Section.MOVIES,
    title = Format.cleanTitle(name),
    subtitle = year,
    url = url,
    image = poster,
)

fun Episode.toVodItem(series: Series) = VodItem(
    key = UserStore.episodeKey(id),
    section = Section.SERIES,
    title = Format.cleanTitle(series.name),
    subtitle = "S$season · E$episode" + title.takeIf { it.isNotBlank() && !it.contains(series.name, ignoreCase = true) }?.let { " · $it" }.orEmpty(),
    url = url,
    image = image ?: series.backdrop ?: series.cover,
    seriesId = series.id,
)

fun WatchEntry.toVodItem() = VodItem(
    key = key, section = section, title = title, subtitle = subtitle, url = url, image = image, seriesId = seriesId,
)
