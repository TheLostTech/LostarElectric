package com.lostar.tv.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.lostar.tv.data.Catalog
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.UserStore
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.data.Movie
import com.lostar.tv.data.Series
import com.lostar.tv.data.WatchEntry
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.LandscapeCard
import com.lostar.tv.ui.components.LiveBadge
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.MetaLine
import com.lostar.tv.ui.components.PosterCard
import com.lostar.tv.ui.components.Shelf
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette
import com.lostar.tv.ui.toVodItem
import kotlinx.coroutines.delay

/** What the big header shows for the focused tile. */
private sealed interface Featured {
    val key: String

    data class OfMovie(val movie: Movie) : Featured { override val key get() = "m" + movie.id }
    data class OfSeries(val series: Series) : Featured { override val key get() = "s" + series.id }
    data class OfChannel(val channel: LiveChannel) : Featured { override val key get() = "c" + channel.id }
    data class OfEntry(val entry: WatchEntry) : Featured { override val key get() = "w" + entry.key }
}

private data class HeroContent(
    val title: String,
    val meta: List<String?>,
    val description: String?,
    val image: String?,
    val live: Boolean = false,
    val progress: Float? = null,
)

@Composable
fun HomeScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val history by app.userStore.history.collectAsState(initial = emptyList())
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val recentIds by app.userStore.recentChannels.collectAsState(initial = emptyList())

    val shelves = remember(catalog) { buildCatalogShelves(catalog) }
    val continueWatching = remember(history) { history.filter { it.isInProgress }.take(20) }
    val favoriteChannels = remember(favorites, catalog) { catalog.live.channels.filter { UserStore.isLiveFavorite(it, favorites) } }
    val recentChannels = remember(recentIds, catalog) { recentIds.mapNotNull { catalog.live.find(it) }.distinctBy { it.id } }

    var featured by remember { mutableStateOf<Featured?>(null) }
    LaunchedEffect(shelves) {
        if (featured == null) {
            featured = shelves.firstOrNull()?.let { s ->
                s.movies.firstOrNull()?.let { Featured.OfMovie(it) } ?: s.series.firstOrNull()?.let { Featured.OfSeries(it) }
            } ?: catalog.live.channels.firstOrNull()?.let { Featured.OfChannel(it) }
        }
    }

    if (catalog.isEmpty) {
        MessageView("La lista è vuota", "Il fornitore non ha restituito contenuti.")
        return
    }

    val firstFocus = rememberInitialFocus()
    var firstAssigned = false
    fun firstModifier(): Modifier =
        if (!firstAssigned) { firstAssigned = true; Modifier.focusRequester(firstFocus) } else Modifier

    Box(Modifier.fillMaxSize()) {
        Hero(featured)

        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.fillMaxHeight(0.44f))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                if (continueWatching.isNotEmpty()) {
                    val m = firstModifier()
                    item("continue") {
                        Shelf("Continua a guardare", continueWatching, key = { it.key }) { entry ->
                            LandscapeCard(
                                title = entry.title,
                                subtitle = entry.subtitle,
                                image = entry.image,
                                progress = entry.progress,
                                modifier = if (entry == continueWatching.first()) m else Modifier,
                                onFocused = { featured = Featured.OfEntry(entry) },
                                onClick = {
                                    navigator.play(PlayRequest.Vod(listOf(entry.toVodItem()), 0, entry.positionMs))
                                },
                            )
                        }
                    }
                }
                if (favoriteChannels.isNotEmpty()) {
                    val m = firstModifier()
                    item("favChannels") {
                        ChannelShelf("I tuoi canali", favoriteChannels, m, navigator) { featured = Featured.OfChannel(it) }
                    }
                }
                if (recentChannels.isNotEmpty()) {
                    val m = firstModifier()
                    item("recentChannels") {
                        ChannelShelf("Visti di recente", recentChannels, m, navigator) { featured = Featured.OfChannel(it) }
                    }
                }
                shelves.forEach { shelf ->
                    val m = firstModifier()
                    item(shelf.id) {
                        if (shelf.movies.isNotEmpty()) {
                            Shelf(shelf.title, shelf.movies, key = { it.id }) { movie ->
                                PosterCard(
                                    title = Format.cleanTitle(movie.name),
                                    image = movie.poster,
                                    modifier = if (movie == shelf.movies.first()) m else Modifier,
                                    onFocused = { featured = Featured.OfMovie(movie) },
                                    onClick = { navigator.openMovie(movie.id) },
                                )
                            }
                        } else if (shelf.series.isNotEmpty()) {
                            Shelf(shelf.title, shelf.series, key = { it.id }) { s ->
                                PosterCard(
                                    title = Format.cleanTitle(s.name),
                                    image = s.cover,
                                    modifier = if (s == shelf.series.first()) m else Modifier,
                                    onFocused = { featured = Featured.OfSeries(s) },
                                    onClick = { navigator.openSeries(s.id) },
                                )
                            }
                        } else if (shelf.channels.isNotEmpty()) {
                            ChannelShelf(shelf.title, shelf.channels, m, navigator) { featured = Featured.OfChannel(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelShelf(
    title: String,
    channels: List<LiveChannel>,
    firstModifier: Modifier,
    navigator: Navigator,
    onFocused: (LiveChannel) -> Unit,
) {
    Shelf(title, channels, key = { it.id }) { ch ->
        LandscapeCard(
            title = ch.name,
            image = ch.logo,
            logo = true,
            live = true,
            width = 220.dp,
            modifier = if (ch == channels.first()) firstModifier else Modifier,
            onFocused = { onFocused(ch) },
            onClick = { navigator.play(PlayRequest.Live(channels, channels.indexOf(ch), title)) },
        )
    }
}

@Composable
private fun Hero(featured: Featured?) {
    val app = LocalApp.current
    var content by remember { mutableStateOf<HeroContent?>(null) }

    LaunchedEffect(featured?.key) {
        val f = featured ?: return@LaunchedEffect
        content = baseContent(f)
        // Wait for focus to settle before asking the server for richer details.
        delay(450)
        content = when (f) {
            is Featured.OfMovie -> {
                val d = app.catalog.movieDetail(f.movie)
                baseContent(f).copy(
                    description = d.plot,
                    image = d.backdrop ?: f.movie.poster,
                    meta = listOf(d.year ?: f.movie.year, Format.rating(d.rating ?: f.movie.rating), d.genre, Format.duration(d.durationSecs)),
                )
            }
            is Featured.OfChannel -> {
                val now = System.currentTimeMillis()
                val epg: List<EpgProgram> = app.catalog.epg(f.channel)
                val current = epg.firstOrNull { it.startMs <= now && it.endMs > now }
                val next = epg.firstOrNull { it.startMs >= (current?.endMs ?: now) }
                baseContent(f).copy(
                    title = f.channel.name,
                    meta = listOfNotNull(
                        current?.let { "Ora: ${it.title}  ${Format.time(it.startMs)}–${Format.time(it.endMs)}" },
                        next?.let { "Dopo: ${it.title} alle ${Format.time(it.startMs)}" },
                    ).ifEmpty { listOf("In diretta") },
                    description = current?.description,
                    progress = current?.progressAt(now),
                )
            }
            else -> content
        }
    }

    val c = content ?: return
    Box(Modifier.fillMaxSize()) {
        Crossfade(targetState = c.image, label = "backdrop", modifier = Modifier.align(Alignment.TopEnd)) { image ->
            Box(Modifier.fillMaxWidth(0.70f).fillMaxHeight(0.72f)) {
                if (!image.isNullOrBlank()) {
                    AsyncImage(
                        model = image,
                        contentDescription = null,
                        contentScale = if (c.live) ContentScale.Fit else ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (c.live) Modifier.padding(80.dp) else Modifier),
                    )
                }
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(0f to Palette.Background, 0.45f to Palette.Background.copy(alpha = 0.55f), 1f to Color.Transparent)
                    )
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(0.45f to Color.Transparent, 1f to Palette.Background)
                    )
                )
            }
        }
        Column(
            Modifier
                .padding(start = 48.dp, top = 48.dp)
                .fillMaxWidth(0.5f)
        ) {
            if (c.live) {
                LiveBadge()
                Spacer(Modifier.height(12.dp))
            }
            Text(
                c.title,
                style = MaterialTheme.typography.displaySmall,
                color = Palette.Text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            if (c.live) {
                c.meta.forEach { line ->
                    line?.let { Text(it, style = MaterialTheme.typography.titleSmall, color = Palette.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
                c.progress?.let {
                    Spacer(Modifier.height(10.dp))
                    com.lostar.tv.ui.components.ProgressLine(it, Modifier.width(320.dp))
                }
            } else {
                MetaLine(c.meta)
                c.progress?.let {
                    Spacer(Modifier.height(10.dp))
                    com.lostar.tv.ui.components.ProgressLine(it, Modifier.width(320.dp))
                }
            }
            c.description?.let {
                Spacer(Modifier.height(14.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Palette.TextMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun baseContent(f: Featured): HeroContent = when (f) {
    is Featured.OfMovie -> HeroContent(
        title = Format.cleanTitle(f.movie.name),
        meta = listOf(f.movie.year, Format.rating(f.movie.rating), "Film"),
        description = null,
        image = f.movie.poster,
    )
    is Featured.OfSeries -> HeroContent(
        title = Format.cleanTitle(f.series.name),
        meta = listOf(f.series.year, Format.rating(f.series.rating), f.series.genre ?: "Serie TV"),
        description = f.series.plot,
        image = f.series.backdrop ?: f.series.cover,
    )
    is Featured.OfChannel -> HeroContent(
        title = f.channel.name,
        meta = listOf("In diretta"),
        description = null,
        image = f.channel.logo,
        live = true,
    )
    is Featured.OfEntry -> HeroContent(
        title = f.entry.title,
        meta = listOf(
            f.entry.subtitle,
            "Mancano ${Format.duration(((f.entry.durationMs - f.entry.positionMs) / 1000).toInt()) ?: "pochi minuti"}",
        ),
        description = null,
        image = f.entry.image,
        progress = f.entry.progress,
    )
}

private data class HomeShelf(
    val id: String,
    val title: String,
    val movies: List<Movie> = emptyList(),
    val series: List<Series> = emptyList(),
    val channels: List<LiveChannel> = emptyList(),
)

private const val SHELF_SIZE = 30

private fun buildCatalogShelves(c: Catalog): List<HomeShelf> {
    val out = mutableListOf<HomeShelf>()
    val newMovies = c.movies.sortedByDescending { it.added }.take(SHELF_SIZE)
    if (newMovies.isNotEmpty()) out += HomeShelf("newMovies", "Film aggiunti di recente", movies = newMovies)
    val newSeries = c.series.sortedByDescending { it.added }.take(SHELF_SIZE)
    if (newSeries.isNotEmpty()) out += HomeShelf("newSeries", "Serie TV aggiornate", series = newSeries)
    val topMovies = c.movies.filter { (it.rating ?: 0.0) in 7.0..10.0 }.sortedByDescending { it.rating }.take(SHELF_SIZE)
    if (topMovies.size >= 6) out += HomeShelf("topMovies", "I più votati", movies = topMovies)

    val liveCats = c.live.groups.take(3)
    liveCats.firstOrNull()?.let { cat ->
        out.add(minOf(1, out.size), HomeShelf("live:${cat.id}", "In diretta · ${cat.name}", channels = c.live.channelsOf(cat).take(SHELF_SIZE)))
    }
    c.nonEmptyMovies().take(6).forEach { cat ->
        out += HomeShelf("mc:${cat.id}", cat.name, movies = c.moviesByCategory[cat.id].orEmpty().take(SHELF_SIZE))
    }
    c.nonEmptySeries().take(4).forEach { cat ->
        out += HomeShelf("sc:${cat.id}", cat.name, series = c.seriesByCategory[cat.id].orEmpty().take(SHELF_SIZE))
    }
    liveCats.drop(1).forEach { cat ->
        out += HomeShelf("live:${cat.id}", "In diretta · ${cat.name}", channels = c.live.channelsOf(cat).take(SHELF_SIZE))
    }
    return out
}
