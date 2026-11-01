package com.lostar.tv.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lostar.tv.data.UserStore
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.LandscapeCard
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.PosterCard
import com.lostar.tv.ui.components.Shelf
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette

@Composable
fun FavoritesScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val channels = remember(favorites, catalog) { catalog.live.channels.filter { UserStore.isLiveFavorite(it, favorites) } }
    val movies = remember(favorites, catalog) { catalog.movies.filter { UserStore.movieKey(it.id) in favorites } }
    val series = remember(favorites, catalog) { catalog.series.filter { UserStore.seriesKey(it.id) in favorites } }

    if (channels.isEmpty() && movies.isEmpty() && series.isEmpty()) {
        MessageView(
            "Nessun preferito",
            "Nella TV in diretta tieni premuto OK su un canale per aggiungerlo. Per film e serie usa \"Aggiungi ai preferiti\" nella scheda del titolo.",
        )
        return
    }
    val first = rememberInitialFocus()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 48.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        item("title") {
            Text("I tuoi preferiti", style = MaterialTheme.typography.headlineLarge, color = Palette.Text, modifier = Modifier.padding(start = 48.dp))
        }
        if (channels.isNotEmpty()) item("ch") {
            Shelf("Canali", channels, key = { it.id }) { ch ->
                LandscapeCard(
                    title = ch.name, image = ch.logo, logo = true, live = true, width = 220.dp,
                    modifier = if (ch == channels.first()) Modifier.focusRequester(first) else Modifier,
                    onClick = { navigator.play(PlayRequest.Live(channels, channels.indexOf(ch), "Preferiti")) },
                )
            }
        }
        if (movies.isNotEmpty()) item("mv") {
            Shelf("Film", movies, key = { it.id }) { m ->
                PosterCard(
                    Format.cleanTitle(m.name), m.poster,
                    modifier = if (channels.isEmpty() && m == movies.first()) Modifier.focusRequester(first) else Modifier,
                    onClick = { navigator.openMovie(m.id) },
                )
            }
        }
        if (series.isNotEmpty()) item("sr") {
            Shelf("Serie TV", series, key = { it.id }) { s ->
                PosterCard(
                    Format.cleanTitle(s.name), s.cover,
                    modifier = if (channels.isEmpty() && movies.isEmpty() && s == series.first()) Modifier.focusRequester(first) else Modifier,
                    onClick = { navigator.openSeries(s.id) },
                )
            }
        }
    }
}
