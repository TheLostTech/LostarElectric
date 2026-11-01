package com.lostar.tv.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lostar.tv.data.MovieDetail
import com.lostar.tv.data.UserStore
import com.lostar.tv.data.TmdbClient
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.rememberTmdb
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.AccentButton
import com.lostar.tv.ui.components.GhostButton
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.MetaLine
import com.lostar.tv.ui.components.PosterCard
import com.lostar.tv.ui.components.ProgressLine
import com.lostar.tv.ui.components.Shelf
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette
import com.lostar.tv.ui.toVodItem
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(id: String, navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog
    val movie = catalog?.movieById?.get(id)
    if (catalog == null || movie == null) {
        MessageView("Titolo non disponibile", actionLabel = "Indietro", onAction = { navigator.back() })
        return
    }
    val detail by produceState<MovieDetail?>(null, id) { value = app.catalog.movieDetail(movie) }
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val history by app.userStore.history.collectAsState(initial = emptyList())
    val key = UserStore.movieKey(movie.id)
    val entry = history.firstOrNull { it.key == key }?.takeIf { it.isInProgress }
    val isFavorite = key in favorites
    val similar = catalog.moviesByCategory[movie.categoryId].orEmpty().filter { it.id != movie.id }.take(30)
    val playFocus = rememberInitialFocus()
    val d = detail
    val t = rememberTmdb(TmdbClient.Kind.MOVIE, Format.cleanTitle(movie.name), movie.year, delayMs = 0)

    Box(Modifier.fillMaxSize()) {
        DetailBackdrop(d?.backdrop ?: t?.backdrop ?: t?.poster ?: movie.poster)
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 72.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            item("info") {
                Column(Modifier.padding(start = 72.dp).fillMaxWidth(0.55f)) {
                    Text(
                        Format.cleanTitle(movie.name),
                        style = MaterialTheme.typography.displayMedium,
                        color = Palette.Text,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(14.dp))
                    MetaLine(
                        listOf(
                            d?.year ?: movie.year ?: t?.year,
                            t?.let { tt -> tt.ratingLabel?.let { "★ $it TMDB (${tt.votes} voti)" } } ?: Format.rating(d?.rating ?: movie.rating),
                            Format.duration(d?.durationSecs),
                            d?.genre,
                        )
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        d?.plot ?: t?.overview ?: if (d == null) "…" else "Trama non disponibile.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.TextMuted,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis,
                    )
                    d?.director?.let {
                        Spacer(Modifier.height(12.dp))
                        Text("Regia: $it", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    d?.cast?.let {
                        Spacer(Modifier.height(4.dp))
                        Text("Con: $it", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    if (entry != null) {
                        Spacer(Modifier.height(20.dp))
                        ProgressLine(entry.progress, Modifier.width(360.dp))
                    }
                    Spacer(Modifier.height(28.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val item = movie.toVodItem()
                        if (entry != null) {
                            AccentButton(
                                "Riprendi da ${Format.clock(entry.positionMs)}",
                                icon = Icons.Rounded.PlayArrow,
                                modifier = Modifier.focusRequester(playFocus),
                                onClick = { navigator.play(PlayRequest.Vod(listOf(item), 0, entry.positionMs)) },
                            )
                            GhostButton(
                                "Dall'inizio",
                                icon = Icons.Rounded.Replay,
                                onClick = { navigator.play(PlayRequest.Vod(listOf(item), 0, 0)) },
                            )
                        } else {
                            AccentButton(
                                "Guarda",
                                icon = Icons.Rounded.PlayArrow,
                                modifier = Modifier.focusRequester(playFocus),
                                onClick = { navigator.play(PlayRequest.Vod(listOf(item), 0, 0)) },
                            )
                        }
                        GhostButton(
                            if (isFavorite) "Nei preferiti" else "Aggiungi ai preferiti",
                            icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            onClick = { app.scope.launch { app.userStore.toggleFavorite(key) } },
                        )
                    }
                }
            }
            if (similar.isNotEmpty()) {
                item("similar") {
                    Shelf("Altri titoli della stessa categoria", similar, key = { it.id }, startPadding = 72.dp) { m ->
                        val mt = rememberTmdb(TmdbClient.Kind.MOVIE, Format.cleanTitle(m.name), m.year)
                        PosterCard(Format.cleanTitle(m.name), mt?.poster ?: m.poster, onClick = { navigator.openMovie(m.id) }, rating = mt?.ratingLabel)
                    }
                }
            }
        }
    }
}
