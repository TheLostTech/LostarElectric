package com.lostar.tv.desktop.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lostar.tv.data.Movie
import com.lostar.tv.data.Series
import com.lostar.tv.data.TmdbClient
import com.lostar.tv.data.TmdbInfo
import com.lostar.tv.desktop.AppState
import com.lostar.tv.ui.Format
import kotlinx.coroutines.delay

@Composable
fun rememberTmdb(state: AppState, kind: TmdbClient.Kind, title: String, year: String? = null, delayMs: Long = 200): TmdbInfo? {
    var info by remember(kind, title) { mutableStateOf(state.tmdb.peek(kind, title, year)) }
    LaunchedEffect(kind, title) {
        if (info == null && state.tmdb.enabled) {
            delay(delayMs)
            info = state.tmdb.lookup(kind, title, year)
        }
    }
    return info
}

/** Poster for a movie with TMDB artwork and rating. */
@Composable
fun MoviePoster(state: AppState, m: Movie, onClick: () -> Unit, width: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(160f)) {
    val t = rememberTmdb(state, TmdbClient.Kind.MOVIE, Format.cleanTitle(m.name), m.year)
    Poster(Format.cleanTitle(m.name), t?.poster ?: m.poster, onClick = onClick, width = width, rating = t?.ratingLabel ?: Format.rating(m.rating)?.removePrefix("★ "))
}

@Composable
fun SeriesPoster(state: AppState, s: Series, onClick: () -> Unit, width: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp(160f)) {
    val t = rememberTmdb(state, TmdbClient.Kind.TV, Format.cleanTitle(s.name), s.year)
    Poster(Format.cleanTitle(s.name), t?.poster ?: s.cover, onClick = onClick, width = width, rating = t?.ratingLabel ?: Format.rating(s.rating)?.removePrefix("★ "))
}

