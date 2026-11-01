package com.lostar.tv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lostar.tv.data.TmdbClient
import com.lostar.tv.data.TmdbInfo
import kotlinx.coroutines.delay

/** TMDB data for a title: instant from cache, otherwise fetched shortly after the card appears. */
@Composable
fun rememberTmdb(kind: TmdbClient.Kind, title: String, year: String? = null, delayMs: Long = 250): TmdbInfo? {
    val app = LocalApp.current
    var info by remember(kind, title) { mutableStateOf(app.tmdb.peek(kind, title, year)) }
    LaunchedEffect(kind, title) {
        if (info == null && app.tmdb.enabled) {
            delay(delayMs)
            info = app.tmdb.lookup(kind, title, year)
        }
    }
    return info
}
