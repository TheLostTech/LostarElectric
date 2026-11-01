package com.lostar.tv.desktop.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.data.Genre
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.DesktopStore
import com.lostar.tv.desktop.video.LiveSession
import com.lostar.tv.desktop.video.VideoView
import com.lostar.tv.ui.Format
import kotlinx.coroutines.delay

private const val ALL = "@all"
private const val FAV = "@fav"
private const val RECENT = "@recent"

@Composable
fun LiveScreen(state: AppState) {
    val catalog = state.catalog.currentCatalog ?: return
    val index = catalog.live
    val data by state.store.data.collectAsState()
    if (index.channels.isEmpty()) {
        CenterMessage("Nessun canale", "La tua lista non contiene canali in diretta.")
        return
    }
    var genre by remember { mutableStateOf<Genre?>(null) }
    var selected by remember { mutableStateOf(ALL) }

    val inGenre = remember(index, genre) { index.channels.filter { genre == null || it.genre == genre } }
    val ids = remember(inGenre) { inGenre.mapTo(HashSet()) { it.id } }
    val favs = remember(index, data.favorites, ids) { index.channels.filter { it.id in ids && DesktopStore.isLiveFavorite(it, data.favorites) } }
    val recent = remember(index, data.recentChannels, ids) { data.recentChannels.mapNotNull { index.find(it) }.distinctBy { it.id }.filter { it.id in ids } }
    val entries = remember(index, inGenre, favs.size, recent.size) {
        buildList {
            add(ListEntry(ALL, "Tutti", inGenre.size))
            if (favs.isNotEmpty()) add(ListEntry(FAV, "★  Preferiti", favs.size))
            if (recent.isNotEmpty()) add(ListEntry(RECENT, "Visti di recente", recent.size))
            index.groups.forEach { g ->
                val n = g.channelIds.count { it in ids }
                if (n > 0) add(ListEntry(g.id, g.name, n))
            }
        }
    }
    val effective = selected.takeIf { s -> entries.any { it.id == s } } ?: ALL
    val channels = remember(effective, inGenre, favs, recent) {
        when (effective) {
            ALL -> inGenre
            FAV -> favs
            RECENT -> recent
            else -> index.groups.firstOrNull { it.id == effective }?.let { index.channelsOf(it) }.orEmpty().filter { it.id in ids }
        }
    }
    val listName = entries.firstOrNull { it.id == effective }?.name?.removePrefix("★  ").orEmpty()
    val session = state.live
    val playing = session.current

    Column(Modifier.fillMaxSize()) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 14.dp),
        ) {
            item { Chip("Tutti", genre == null) { genre = null; selected = ALL } }
            items(index.genres) { g -> Chip(g.label, genre == g) { genre = g; selected = ALL } }
        }
        Row(Modifier.fillMaxSize()) {
            CategoryColumn(entries, effective, { selected = it }, Modifier.width(280.dp).fillMaxHeight())
            val listState = rememberLazyListState()
            LaunchedEffect(effective, genre) { listState.scrollToItem(0) }
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                itemsIndexed(channels, key = { _, c -> c.id }) { i, ch ->
                    ChannelRow(
                        state, ch,
                        playing = playing?.id == ch.id,
                        favorite = DesktopStore.isLiveFavorite(ch, data.favorites),
                        onClick = { session.start(channels, i, listName) },
                        onDoubleClick = {
                            session.start(channels, i, listName)
                            state.liveFullscreen = true
                        },
                    )
                }
            }
            PreviewPanel(state, Modifier.width(520.dp).fillMaxHeight())
        }
    }
}

@Composable
fun ChannelRow(
    state: AppState,
    channel: LiveChannel,
    playing: Boolean,
    favorite: Boolean,
    onClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
    translucent: Boolean = false,
) {
    var now by remember(channel.id) { mutableStateOf<EpgProgram?>(null) }
    LaunchedEffect(channel.id) {
        delay(400)
        now = state.catalog.nowPlaying(channel)
    }
    val textAlpha = if (translucent) 0.85f else 1f
    Row(
        Modifier.fillMaxWidth()
            .hoverClick(
                selected = playing,
                hoverColor = if (translucent) Color.White.copy(alpha = 0.10f) else Palette.SurfaceHigh,
                selectedColor = if (translucent) Color.White.copy(alpha = 0.16f) else Palette.SurfaceFocus,
                onDoubleClick = onDoubleClick,
                onClick = onClick,
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChannelLogo(channel.logo, channel.name, Modifier.size(width = 72.dp, height = 44.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                channel.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold,
                color = (if (playing) Palette.Accent else Palette.Text).copy(alpha = textAlpha),
                style = MaterialTheme.typography.titleMedium,
            )
            now?.let {
                Text(it.title, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Palette.TextMuted.copy(alpha = textAlpha), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (channel.variants.size > 1) {
            Text("${channel.variants.size} link", color = Palette.TextDim.copy(alpha = textAlpha), style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(8.dp))
        }
        if (playing) Icon(Icons.Rounded.GraphicEq, null, tint = Palette.Accent, modifier = Modifier.size(20.dp))
        if (favorite) Icon(Icons.Rounded.Star, null, tint = Palette.Accent, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun PreviewPanel(state: AppState, modifier: Modifier) {
    val session = state.live
    val playing = session.current
    val data by state.store.data.collectAsState()
    var epg by remember { mutableStateOf<List<EpgProgram>?>(null) }
    LaunchedEffect(playing?.id) {
        epg = null
        playing?.let { epg = state.catalog.epg(it) }
    }
    Column(modifier.background(Palette.Surface)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black).hoverClick(
            shape = RoundedCornerShape(0.dp), hoverColor = Color.Transparent,
            onDoubleClick = { if (playing != null) state.liveFullscreen = true },
            onClick = {},
        ), contentAlignment = Alignment.Center) {
            if (playing != null) {
                VideoView(session.video, Modifier.fillMaxSize())
                when (session.phase) {
                    LiveSession.Phase.CONNECTING, LiveSession.Phase.SWITCHING -> Spinner()
                    LiveSession.Phase.FAILED -> Text("Canale non disponibile", color = Palette.TextMuted)
                    else -> Unit
                }
                LiveBadge(Modifier.align(Alignment.TopStart).padding(12.dp))
                NoticeToast(session, Modifier.align(Alignment.BottomCenter).padding(12.dp))
            } else {
                Text("Clic su un canale per l'anteprima · doppio clic per lo schermo intero", color = Palette.TextDim, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (playing == null) return@Column
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(playing.name, style = MaterialTheme.typography.titleLarge, color = Palette.Text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                PillButton("", icon = Icons.Rounded.Fullscreen, onClick = { state.liveFullscreen = true })
                Spacer(Modifier.width(8.dp))
                PillButton("Interrompi", icon = Icons.Rounded.Stop, onClick = { session.stop() })
                Spacer(Modifier.width(8.dp))
                val fav = DesktopStore.isLiveFavorite(playing, data.favorites)
                PillButton("", icon = if (fav) Icons.Rounded.Star else Icons.Rounded.StarBorder, onClick = { state.store.toggleLiveFavorite(playing) })
            }
            Spacer(Modifier.height(6.dp))
            Text(sourceLine(session), color = Palette.Accent, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(18.dp))
            SectionLabel("Guida programmi")
            Spacer(Modifier.height(10.dp))
            Guide(epg, translucent = false)
        }
    }
}

fun sourceLine(session: LiveSession): String =
    if (session.ranked.size > 1) session.variantLabel(session.variantPos) + " di ${session.ranked.size} · scelta " + (if (session.manual) "manuale" else "automatica")
    else "Sorgente unica · riconnessione automatica"

@Composable
fun NoticeToast(session: LiveSession, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(session.noticeToken) {
        if (session.notice != null) {
            visible = true
            delay(4500)
            visible = false
        }
    }
    if (visible && session.notice != null) {
        Text(
            session.notice.orEmpty(), color = Color.White, style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.clip(RoundedCornerShape(10.dp)).background(Color.Black.copy(alpha = 0.75f)).padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

@Composable
fun Guide(epg: List<EpgProgram>?, translucent: Boolean, max: Int = 6) {
    val now = System.currentTimeMillis()
    val a = if (translucent) 0.85f else 1f
    when {
        epg == null -> Text("Caricamento guida…", color = Palette.TextDim.copy(alpha = a))
        epg.isEmpty() -> Text("Guida TV non disponibile per questo canale", color = Palette.TextDim.copy(alpha = a))
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            epg.take(max).forEach { p ->
                val onAir = p.startMs <= now && p.endMs > now
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(
                            if (translucent) Color.White.copy(alpha = if (onAir) 0.14f else 0.06f)
                            else if (onAir) Palette.SurfaceFocus else Palette.SurfaceHigh
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(Format.time(p.startMs), color = (if (onAir) Palette.Accent else Palette.TextMuted).copy(alpha = a), fontWeight = FontWeight.SemiBold, modifier = Modifier.width(58.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.title, color = Palette.Text.copy(alpha = a), fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        p.description?.let { Text(it, color = Palette.TextMuted.copy(alpha = a), style = MaterialTheme.typography.bodySmall, maxLines = if (onAir) 2 else 1, overflow = TextOverflow.Ellipsis) }
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

@Composable
fun BigLogo(logo: String?) {
    if (!logo.isNullOrBlank()) AsyncImage(logo, null, modifier = Modifier.size(120.dp))
}
