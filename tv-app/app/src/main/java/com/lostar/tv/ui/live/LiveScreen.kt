package com.lostar.tv.ui.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.data.Genre
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.LiveIndex
import com.lostar.tv.data.UserStore
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.CategoryEntry
import com.lostar.tv.ui.components.CategoryList
import com.lostar.tv.ui.components.GhostButton
import com.lostar.tv.ui.components.LiveBadge
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.ProgressLine
import com.lostar.tv.ui.components.Spinner
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.LiveSession
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.player.VideoSurface
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val FAVORITES_ID = "@fav"
const val RECENT_ID = "@recent"
const val ALL_ID = "@all"

/**
 * Live TV in the Etticio / Sky Q layout, simplified:
 * genre chips · merged categories · channels with "now on" · preview + programme guide.
 * Duplicated links are hidden: each row is one channel and the player picks the link.
 */
@Composable
fun LiveScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val index = catalog.live
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val recentIds by app.userStore.recentChannels.collectAsState(initial = emptyList())

    if (index.channels.isEmpty()) {
        MessageView("Nessun canale", "La tua lista non contiene canali in diretta.")
        return
    }

    var genre by rememberSaveable { mutableStateOf<Genre?>(null) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    val inGenre = remember(index, genre) { index.channels.filter { genre == null || it.genre == genre } }
    val inGenreIds = remember(inGenre) { inGenre.mapTo(HashSet()) { it.id } }
    val favChannels = remember(index, favorites, inGenreIds) {
        index.channels.filter { it.id in inGenreIds && UserStore.isLiveFavorite(it, favorites) }
    }
    val recent = remember(index, recentIds, inGenreIds) {
        recentIds.mapNotNull { index.find(it) }.distinctBy { it.id }.filter { it.id in inGenreIds }
    }
    val entries = remember(index, inGenre, favChannels.size, recent.size) {
        buildList {
            add(CategoryEntry(ALL_ID, "Tutti", inGenre.size))
            if (favChannels.isNotEmpty()) add(CategoryEntry(FAVORITES_ID, "★  Preferiti", favChannels.size))
            if (recent.isNotEmpty()) add(CategoryEntry(RECENT_ID, "Visti di recente", recent.size))
            index.groups.forEach { g ->
                val count = g.channelIds.count { it in inGenreIds }
                if (count > 0) add(CategoryEntry(g.id, g.name, count))
            }
        }
    }
    val effectiveId = selectedId?.takeIf { id -> entries.any { it.id == id } } ?: ALL_ID
    val channels = remember(effectiveId, inGenre, favChannels, recent) {
        when (effectiveId) {
            ALL_ID -> inGenre
            FAVORITES_ID -> favChannels
            RECENT_ID -> recent
            else -> index.groups.firstOrNull { it.id == effectiveId }?.let { index.channelsOf(it) }.orEmpty().filter { it.id in inGenreIds }
        }
    }
    val categoryName = entries.firstOrNull { it.id == effectiveId }?.name?.removePrefix("★  ").orEmpty()
    var focused by remember(effectiveId) { mutableStateOf(channels.firstOrNull()) }

    val session = app.liveSession
    val playing = session.current

    fun onChannelClick(i: Int) {
        val ch = channels[i]
        if (playing?.id == ch.id) {
            navigator.play(PlayRequest.Live(channels, i, categoryName)) // second OK: full screen
        } else {
            session.start(channels, i, categoryName) // first OK: preview
        }
    }

    Column(Modifier.fillMaxSize()) {
        Header(index, genre) { g ->
            genre = g
            selectedId = null
        }
        Row(Modifier.weight(1f).fillMaxWidth()) {
            CategoryList(entries, effectiveId, onSelect = { selectedId = it }, modifier = Modifier.padding(start = 16.dp), width = 270.dp)

            val firstFocus = rememberInitialFocus()
            val listState = rememberLazyListState()
            LaunchedEffect(effectiveId, genre) { listState.scrollToItem(0) }
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxHeight().focusRestorer(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(channels, key = { _, ch -> ch.id }) { i, ch ->
                    ChannelRow(
                        channel = ch,
                        favorite = UserStore.isLiveFavorite(ch, favorites),
                        playing = playing?.id == ch.id,
                        modifier = if (i == 0) Modifier.focusRequester(firstFocus) else Modifier,
                        onFocused = { focused = ch },
                        onClick = { onChannelClick(i) },
                        onLongClick = { app.scope.launch { app.userStore.toggleLiveFavorite(ch) } },
                    )
                }
            }

            PreviewPanel(
                session = session,
                focused = focused,
                isFavorite = { UserStore.isLiveFavorite(it, favorites) },
                onFullscreen = {
                    val list = session.channels
                    if (list.isNotEmpty()) navigator.play(PlayRequest.Live(list, session.index, session.listName))
                },
                onToggleFavorite = { ch -> app.scope.launch { app.userStore.toggleLiveFavorite(ch) } },
                modifier = Modifier.width(500.dp).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun Header(index: LiveIndex, selected: Genre?, onSelect: (Genre?) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 28.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("TV in diretta", style = MaterialTheme.typography.headlineMedium, color = Palette.Text)
            Text(
                "${index.channels.size} canali · doppioni uniti da ${index.variantCount} link",
                style = MaterialTheme.typography.bodySmall,
                color = Palette.TextDim,
            )
        }
        Spacer(Modifier.width(36.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 6.dp)) {
            item(key = "all") { GenreChip("Tutti", selected == null) { onSelect(null) } }
            items(index.genres, key = { it.name }) { g -> GenreChip(g.label, selected == g) { onSelect(g) } }
        }
    }
}

@Composable
private fun GenreChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(22.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) Palette.SurfaceFocus else Palette.Surface,
            contentColor = if (selected) Palette.Accent else Palette.TextMuted,
            focusedContainerColor = Palette.Accent,
            focusedContentColor = Palette.OnAccent,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(BorderStroke(1.dp, if (selected) Palette.Accent else Palette.Divider), shape = RoundedCornerShape(22.dp)),
        ),
    ) {
        Text(label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp))
    }
}

@Composable
private fun ChannelRow(
    channel: LiveChannel,
    favorite: Boolean,
    playing: Boolean,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalApp.current
    var now by remember(channel.id) { mutableStateOf<EpgProgram?>(null) }
    // Only rows on screen are composed; the pause avoids loading the guide while scrolling fast.
    LaunchedEffect(channel.id) {
        delay(600)
        now = app.catalog.nowPlaying(channel)
    }
    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (playing) Palette.SurfaceHigh else Color.Transparent,
            contentColor = Palette.Text,
            focusedContainerColor = Palette.SurfaceFocus,
            focusedContentColor = Palette.Text,
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, Palette.Accent), shape = RoundedCornerShape(12.dp)),
        ),
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(width = 76.dp, height = 46.dp).clip(RoundedCornerShape(8.dp)).background(Palette.SurfaceHigh),
                contentAlignment = Alignment.Center,
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    AsyncImage(channel.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(5.dp))
                } else {
                    Text(channel.name.take(3).uppercase(), style = MaterialTheme.typography.labelMedium, color = Palette.TextMuted)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    channel.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (playing) Palette.Accent else Palette.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                now?.let {
                    Text(it.title, style = MaterialTheme.typography.bodySmall, color = Palette.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (playing) Icon(Icons.Rounded.GraphicEq, contentDescription = "In riproduzione", tint = Palette.Accent, modifier = Modifier.size(22.dp))
            if (favorite) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Star, contentDescription = "Preferito", tint = Palette.Accent, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun PreviewPanel(
    session: LiveSession,
    focused: LiveChannel?,
    isFavorite: (LiveChannel) -> Boolean,
    onFullscreen: () -> Unit,
    onToggleFavorite: (LiveChannel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalApp.current
    val playing = session.current
    val shown = playing ?: focused
    var epg by remember { mutableStateOf<List<EpgProgram>?>(null) }
    LaunchedEffect(shown?.id) {
        epg = null
        val ch = shown ?: return@LaunchedEffect
        if (playing == null) delay(350)
        epg = app.catalog.epg(ch)
    }

    Column(modifier.background(Palette.Surface)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black), contentAlignment = Alignment.Center) {
            if (playing != null) {
                val player = remember { session.player() }
                DisposableEffect(Unit) {
                    session.attach()
                    onDispose { session.detach() }
                }
                VideoSurface(player, Modifier.fillMaxSize())
                when (session.phase) {
                    LiveSession.Phase.CONNECTING, LiveSession.Phase.SWITCHING -> Spinner(size = 40.dp)
                    LiveSession.Phase.FAILED -> Text("Canale non disponibile", color = Palette.TextMuted, style = MaterialTheme.typography.bodyMedium)
                    else -> Unit
                }
            } else if (shown != null) {
                if (!shown.logo.isNullOrBlank()) {
                    AsyncImage(shown.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(56.dp))
                }
                Text(
                    "OK per l'anteprima",
                    style = MaterialTheme.typography.labelMedium,
                    color = Palette.TextDim,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                )
            }
            if (playing != null) LiveBadge(Modifier.align(Alignment.TopStart).padding(12.dp))
        }

        if (shown == null) return@Column
        Column(Modifier.padding(horizontal = 24.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    shown.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Palette.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (isFavorite(shown)) Icon(Icons.Rounded.Star, contentDescription = null, tint = Palette.Accent, modifier = Modifier.size(24.dp))
            }
            if (playing != null) {
                Spacer(Modifier.height(4.dp))
                val source = if (session.ranked.size > 1) {
                    session.variantLabel(session.variantPos) + " di ${session.ranked.size} · scelta " + (if (session.manual) "manuale" else "automatica")
                } else "Sorgente unica · riconnessione automatica"
                Text(source, style = MaterialTheme.typography.bodySmall, color = Palette.Accent, maxLines = 1)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GhostButton("Schermo intero", onClick = onFullscreen, icon = Icons.Rounded.Fullscreen)
                    GhostButton("Interrompi", onClick = { session.stop() }, icon = Icons.Rounded.Stop)
                    GhostButton(
                        "",
                        onClick = { onToggleFavorite(shown) },
                        icon = if (isFavorite(shown)) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("GUIDA PROGRAMMI", style = MaterialTheme.typography.labelMedium, color = Palette.Accent)
            Spacer(Modifier.height(10.dp))
            GuideList(epg)
        }
    }
}

@Composable
private fun GuideList(epg: List<EpgProgram>?) {
    val now = System.currentTimeMillis()
    when {
        epg == null -> Text("Caricamento guida…", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim)
        epg.isEmpty() -> Text("Guida TV non disponibile per questo canale", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim)
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            epg.take(5).forEach { p ->
                val onAir = p.startMs <= now && p.endMs > now
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (onAir) Palette.SurfaceFocus else Palette.SurfaceHigh)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        Format.time(p.startMs),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (onAir) Palette.Accent else Palette.TextMuted,
                        modifier = Modifier.width(56.dp),
                    )
                    Column(Modifier.weight(1f)) {
                        Text(p.title, style = MaterialTheme.typography.titleSmall, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        p.description?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Palette.TextMuted, maxLines = if (onAir) 2 else 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (onAir) {
                            Spacer(Modifier.height(6.dp))
                            ProgressLine(p.progressAt(now), height = 3.dp)
                        }
                    }
                }
            }
        }
    }
}
