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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lostar.tv.data.Episode
import com.lostar.tv.data.SeriesDetail
import com.lostar.tv.data.UserStore
import com.lostar.tv.data.TmdbClient
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.rememberTmdb
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.AccentButton
import com.lostar.tv.ui.components.GhostButton
import com.lostar.tv.ui.components.LandscapeCard
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.MetaLine
import com.lostar.tv.ui.components.Spinner
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette
import com.lostar.tv.ui.toVodItem
import kotlinx.coroutines.launch

@Composable
fun SeriesDetailScreen(id: String, navigator: Navigator) {
    val app = LocalApp.current
    val series = app.catalog.currentCatalog?.seriesById?.get(id)
    if (series == null) {
        MessageView("Serie non disponibile", actionLabel = "Indietro", onAction = { navigator.back() })
        return
    }
    val detail by produceState<SeriesDetail?>(null, id) {
        value = runCatching { app.catalog.seriesDetail(series) }.getOrDefault(SeriesDetail())
    }
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val history by app.userStore.history.collectAsState(initial = emptyList())
    val favKey = UserStore.seriesKey(series.id)
    val isFavorite = favKey in favorites
    val lastWatched = history.firstOrNull { it.seriesId == series.id }

    val d = detail
    val t = rememberTmdb(TmdbClient.Kind.TV, Format.cleanTitle(series.name), series.year, delayMs = 0)
    val seasons = d?.seasons.orEmpty()
    var season by rememberSaveable { mutableStateOf<Int?>(null) }
    LaunchedEffect(seasons) {
        if (season == null && seasons.isNotEmpty()) {
            val lastEpisode = seasons.values.flatten().firstOrNull { UserStore.episodeKey(it.id) == lastWatched?.key }
            season = lastEpisode?.season ?: seasons.keys.first()
        }
    }
    val episodes = season?.let { seasons[it] }.orEmpty()
    val allEpisodes = seasons.values.flatten()
    val playFocus = rememberInitialFocus(d != null)

    fun play(list: List<Episode>, index: Int) {
        val item = list[index]
        val pos = history.firstOrNull { it.key == UserStore.episodeKey(item.id) }?.takeIf { it.isInProgress }?.positionMs ?: 0
        navigator.play(PlayRequest.Vod(list.map { it.toVodItem(series) }, index, pos))
    }

    Box(Modifier.fillMaxSize()) {
        DetailBackdrop(d?.backdrop ?: series.backdrop ?: t?.backdrop ?: series.cover)
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 64.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            item("info") {
                Column(Modifier.padding(start = 72.dp).fillMaxWidth(0.55f)) {
                    Text(
                        Format.cleanTitle(series.name),
                        style = MaterialTheme.typography.displayMedium,
                        color = Palette.Text,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(14.dp))
                    MetaLine(
                        listOf(
                            series.year ?: t?.year,
                            t?.ratingLabel?.let { "★ $it TMDB" } ?: Format.rating(series.rating),
                            d?.genre ?: series.genre,
                            seasons.size.takeIf { it > 0 }?.let { if (it == 1) "1 stagione" else "$it stagioni" },
                        )
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        d?.plot ?: series.plot ?: t?.overview ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Palette.TextMuted,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(28.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val resumeIndex = allEpisodes.indexOfFirst { UserStore.episodeKey(it.id) == lastWatched?.key }
                        when {
                            d == null -> Spinner(size = 36.dp)
                            allEpisodes.isEmpty() -> Text("Nessun episodio disponibile", color = Palette.TextDim)
                            resumeIndex >= 0 -> {
                                val inProgress = lastWatched?.isInProgress == true
                                val target = if (inProgress || resumeIndex == allEpisodes.lastIndex) resumeIndex else resumeIndex + 1
                                val t = allEpisodes[target]
                                AccentButton(
                                    (if (target == resumeIndex && inProgress) "Riprendi" else "Guarda") + " S${t.season} E${t.episode}",
                                    icon = Icons.Rounded.PlayArrow,
                                    modifier = Modifier.focusRequester(playFocus),
                                    onClick = {
                                        val list = seasons[t.season].orEmpty()
                                        play(list, list.indexOf(t).coerceAtLeast(0))
                                    },
                                )
                            }
                            else -> {
                                val first = allEpisodes.first()
                                AccentButton(
                                    "Guarda S${first.season} E${first.episode}",
                                    icon = Icons.Rounded.PlayArrow,
                                    modifier = Modifier.focusRequester(playFocus),
                                    onClick = { play(seasons[first.season].orEmpty(), 0) },
                                )
                            }
                        }
                        GhostButton(
                            if (isFavorite) "Nei preferiti" else "Aggiungi ai preferiti",
                            icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            onClick = { app.scope.launch { app.userStore.toggleFavorite(favKey) } },
                        )
                    }
                }
            }
            if (seasons.size > 1) {
                item("seasons") {
                    LazyRow(
                        modifier = Modifier.focusRestorer(),
                        contentPadding = PaddingValues(horizontal = 72.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(seasons.keys.toList(), key = { it }) { s ->
                            Surface(
                                onClick = { season = s },
                                modifier = Modifier.onFocusChanged { if (it.isFocused) season = s },
                                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(20.dp)),
                                colors = ClickableSurfaceDefaults.colors(
                                    containerColor = if (s == season) Palette.SurfaceFocus else Palette.Surface.copy(alpha = 0.7f),
                                    contentColor = if (s == season) Palette.Accent else Palette.TextMuted,
                                    focusedContainerColor = Palette.Text,
                                    focusedContentColor = Palette.OnAccent,
                                ),
                            ) {
                                Text("Stagione $s", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
                            }
                        }
                    }
                }
            }
            if (episodes.isNotEmpty()) {
                item("episodes-$season") {
                    Column {
                        Text(
                            "Episodi · Stagione $season",
                            style = MaterialTheme.typography.titleLarge,
                            color = Palette.Text,
                            modifier = Modifier.padding(start = 72.dp),
                        )
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            modifier = Modifier.focusRestorer(),
                            contentPadding = PaddingValues(start = 72.dp, end = 48.dp, top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                        ) {
                            items(episodes, key = { it.id }) { ep ->
                                val h = history.firstOrNull { it.key == UserStore.episodeKey(ep.id) }
                                LandscapeCard(
                                    title = "${ep.episode}. " + (ep.title.takeIf { it.isNotBlank() && !it.contains(series.name, true) } ?: "Episodio ${ep.episode}"),
                                    subtitle = Format.duration(ep.durationSecs) ?: ep.plot,
                                    image = ep.image ?: d?.backdrop ?: series.cover,
                                    progress = h?.progress,
                                    width = 280.dp,
                                    onClick = { play(episodes, episodes.indexOf(ep)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
