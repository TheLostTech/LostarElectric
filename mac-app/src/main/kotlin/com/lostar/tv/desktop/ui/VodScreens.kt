package com.lostar.tv.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lostar.tv.data.Category
import com.lostar.tv.data.Episode
import com.lostar.tv.data.MovieDetail
import com.lostar.tv.data.SeriesDetail
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.Detail
import com.lostar.tv.desktop.DesktopStore
import com.lostar.tv.desktop.VodRequest
import com.lostar.tv.desktop.toVodItem
import com.lostar.tv.ui.Format

private data class GridItem(val id: String, val title: String, val image: String?, val added: Long, val rating: Double?, val categoryId: String)

@Composable
fun MoviesScreen(state: AppState) {
    val c = state.catalog.currentCatalog ?: return
    val items = remember(c) { c.movies.map { GridItem(it.id, Format.cleanTitle(it.name), it.poster, it.added, it.rating, it.categoryId) } }
    Browser(state, items, c.nonEmptyMovies(), { DesktopStore.movieKey(it) }) { state.detail = Detail.OfMovie(it) }
}

@Composable
fun SeriesScreen(state: AppState) {
    val c = state.catalog.currentCatalog ?: return
    val items = remember(c) { c.series.map { GridItem(it.id, Format.cleanTitle(it.name), it.cover, it.added, it.rating, it.categoryId) } }
    Browser(state, items, c.nonEmptySeries(), { DesktopStore.seriesKey(it) }) { state.detail = Detail.OfSeries(it) }
}

@Composable
private fun Browser(state: AppState, items: List<GridItem>, categories: List<Category>, favKey: (String) -> String, onOpen: (String) -> Unit) {
    val data by state.store.data.collectAsState()
    if (items.isEmpty()) {
        CenterMessage("Niente da mostrare", "La tua lista non contiene titoli in questa sezione.")
        return
    }
    val byCat = remember(items) { items.groupBy { it.categoryId } }
    val recent = remember(items) { items.sortedByDescending { it.added }.take(200) }
    val top = remember(items) { items.filter { (it.rating ?: 0.0) in 7.0..10.0 }.sortedByDescending { it.rating }.take(200) }
    val favs = remember(items, data.favorites) { items.filter { favKey(it.id) in data.favorites } }
    val entries = remember(categories, favs.size, top.size) {
        buildList {
            add(ListEntry("@recent", "Aggiunti di recente", 0))
            if (favs.isNotEmpty()) add(ListEntry("@fav", "★  Preferiti", favs.size))
            if (top.size >= 6) add(ListEntry("@top", "I più votati", 0))
            categories.forEach { add(ListEntry(it.id, it.name, byCat[it.id].orEmpty().size)) }
        }
    }
    var selected by remember { mutableStateOf("@recent") }
    val shown = when (selected) {
        "@recent" -> recent
        "@fav" -> favs
        "@top" -> top
        else -> byCat[selected].orEmpty()
    }
    val grid = rememberLazyGridState()
    LaunchedEffect(selected) { grid.scrollToItem(0) }
    Row(Modifier.fillMaxSize()) {
        CategoryColumn(entries, selected, { selected = it }, Modifier.width(280.dp).fillMaxHeight())
        LazyVerticalGrid(
            columns = GridCells.Adaptive(170.dp),
            state = grid,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(shown, key = { it.id }) { item ->
                Poster(item.title, item.image, onClick = { onOpen(item.id) }, width = 170.dp)
            }
        }
    }
}

@Composable
private fun DetailFrame(state: AppState, backdrop: String?, content: @Composable ColumnScope.() -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Palette.Background)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
    ) {
        if (!backdrop.isNullOrBlank()) {
            AsyncImage(backdrop, null, contentScale = ContentScale.Crop, modifier = Modifier.align(Alignment.TopEnd).fillMaxWidth(0.7f).fillMaxHeight())
        }
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(0f to Palette.Background, 0.4f to Palette.Background.copy(alpha = 0.92f), 1f to Color.Transparent)))
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0.55f to Color.Transparent, 1f to Palette.Background)))
        Column(Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = 20.dp)) {
            PillButton("Indietro", icon = Icons.Rounded.ArrowBack, onClick = { state.detail = null })
            Spacer(Modifier.height(30.dp))
            content()
        }
    }
}

@Composable
fun MovieDetail(state: AppState, id: String) {
    val c = state.catalog.currentCatalog ?: return
    val movie = c.movieById[id] ?: return
    val data by state.store.data.collectAsState()
    val d by produceState<MovieDetail?>(null, id) { value = state.catalog.movieDetail(movie) }
    val key = DesktopStore.movieKey(movie.id)
    val entry = data.history.firstOrNull { it.key == key }?.takeIf { it.isInProgress }
    val fav = key in data.favorites
    val similar = remember(id) { c.moviesByCategory[movie.categoryId].orEmpty().filter { it.id != id }.take(30) }
    DetailFrame(state, d?.backdrop ?: movie.poster) {
        Column(Modifier.fillMaxWidth(0.55f)) {
            Text(Format.cleanTitle(movie.name), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Palette.Text, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Text(
                listOfNotNull(d?.year ?: movie.year, Format.rating(d?.rating ?: movie.rating), Format.duration(d?.durationSecs), d?.genre).joinToString("  ·  "),
                color = Palette.TextMuted, style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(16.dp))
            Text(d?.plot ?: if (d == null) "…" else "Trama non disponibile.", color = Palette.TextMuted, style = MaterialTheme.typography.bodyLarge, maxLines = 6, overflow = TextOverflow.Ellipsis)
            d?.director?.let { Text("Regia: $it", color = Palette.TextDim, modifier = Modifier.padding(top = 10.dp)) }
            d?.cast?.let { Text("Con: $it", color = Palette.TextDim, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            if (entry != null) {
                Spacer(Modifier.height(16.dp))
                ProgressLine(entry.progress, Modifier.width(360.dp))
            }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val item = movie.toVodItem()
                if (entry != null) {
                    PillButton("Riprendi da ${Format.clock(entry.positionMs)}", icon = Icons.Rounded.PlayArrow, primary = true, onClick = { state.playVod(VodRequest(listOf(item), 0, entry.positionMs)) })
                    PillButton("Dall'inizio", icon = Icons.Rounded.Replay, onClick = { state.playVod(VodRequest(listOf(item), 0, 0)) })
                } else {
                    PillButton("Guarda", icon = Icons.Rounded.PlayArrow, primary = true, onClick = { state.playVod(VodRequest(listOf(item), 0, 0)) })
                }
                PillButton(if (fav) "Nei preferiti" else "Aggiungi ai preferiti", icon = if (fav) Icons.Rounded.Star else Icons.Rounded.StarBorder, onClick = { state.store.toggleFavorite(key) })
            }
        }
        Spacer(Modifier.weight(1f))
        Shelf("Della stessa categoria", similar, key = { it.id }) { m ->
            Poster(Format.cleanTitle(m.name), m.poster, onClick = { state.detail = Detail.OfMovie(m.id) }, width = 140.dp)
        }
    }
}

@Composable
fun SeriesDetail(state: AppState, id: String) {
    val c = state.catalog.currentCatalog ?: return
    val series = c.seriesById[id] ?: return
    val data by state.store.data.collectAsState()
    val d by produceState<SeriesDetail?>(null, id) { value = runCatching { state.catalog.seriesDetail(series) }.getOrDefault(SeriesDetail()) }
    val seasons = d?.seasons.orEmpty()
    var season by remember(id) { mutableStateOf<Int?>(null) }
    val lastWatched = data.history.firstOrNull { it.seriesId == series.id }
    LaunchedEffect(seasons) {
        if (season == null && seasons.isNotEmpty()) {
            season = seasons.values.flatten().firstOrNull { DesktopStore.episodeKey(it.id) == lastWatched?.key }?.season ?: seasons.keys.first()
        }
    }
    val favKey = DesktopStore.seriesKey(series.id)
    val fav = favKey in data.favorites

    fun play(list: List<Episode>, i: Int) {
        val pos = data.history.firstOrNull { it.key == DesktopStore.episodeKey(list[i].id) }?.takeIf { it.isInProgress }?.positionMs ?: 0
        state.playVod(VodRequest(list.map { it.toVodItem(series) }, i, pos))
    }

    DetailFrame(state, d?.backdrop ?: series.backdrop ?: series.cover) {
        Column(Modifier.fillMaxWidth(0.55f)) {
            Text(Format.cleanTitle(series.name), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Palette.Text, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Text(
                listOfNotNull(series.year, Format.rating(series.rating), d?.genre ?: series.genre, seasons.size.takeIf { it > 0 }?.let { if (it == 1) "1 stagione" else "$it stagioni" }).joinToString("  ·  "),
                color = Palette.TextMuted, style = MaterialTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(16.dp))
            Text(d?.plot ?: series.plot.orEmpty(), color = Palette.TextMuted, style = MaterialTheme.typography.bodyLarge, maxLines = 5, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (d == null) Spinner(size = 30.dp)
                PillButton(if (fav) "Nei preferiti" else "Aggiungi ai preferiti", icon = if (fav) Icons.Rounded.Star else Icons.Rounded.StarBorder, onClick = { state.store.toggleFavorite(favKey) })
            }
        }
        Spacer(Modifier.height(24.dp))
        if (seasons.size > 1) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(seasons.keys.toList()) { s -> Chip("Stagione $s", s == season) { season = s } }
            }
            Spacer(Modifier.height(14.dp))
        }
        val episodes = season?.let { seasons[it] }.orEmpty()
        LazyColumn(Modifier.fillMaxWidth(0.7f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(episodes, key = { it.id }) { ep ->
                val h = data.history.firstOrNull { it.key == DesktopStore.episodeKey(ep.id) }
                Row(
                    Modifier.fillMaxWidth().hoverClick { play(episodes, episodes.indexOf(ep)) }.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(180.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp)).background(Palette.SurfaceHigh)) {
                        val img = ep.image ?: d?.backdrop ?: series.cover
                        if (!img.isNullOrBlank()) AsyncImage(img, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        if (h != null && h.progress > 0f) ProgressLine(h.progress, Modifier.align(Alignment.BottomCenter).padding(6.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${ep.episode}. " + (ep.title.takeIf { it.isNotBlank() && !it.contains(series.name, true) } ?: "Episodio ${ep.episode}"), color = Palette.Text, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(listOfNotNull(Format.duration(ep.durationSecs), ep.plot).joinToString(" · "), color = Palette.TextMuted, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    androidx.compose.material3.Icon(Icons.Rounded.PlayArrow, null, tint = Palette.Accent, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}
