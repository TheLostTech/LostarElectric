package com.lostar.tv.ui.live

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.lostar.tv.data.Catalog
import com.lostar.tv.data.Channel
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.data.UserStore
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.CategoryEntry
import com.lostar.tv.ui.components.CategoryList
import com.lostar.tv.ui.components.LiveBadge
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.ProgressLine
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val FAVORITES_ID = "@fav"
const val RECENT_ID = "@recent"
const val ALL_ID = "@all"

@Composable
fun LiveScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    val recentIds by app.userStore.recentChannels.collectAsState(initial = emptyList())

    val favChannels = remember(favorites, catalog) {
        catalog.channels.filter { UserStore.liveKey(it.id) in favorites }
    }
    val recent = remember(recentIds, catalog) { recentIds.mapNotNull { catalog.channelById[it] } }
    val entries = remember(catalog, favChannels.size, recent.size) {
        buildList {
            if (favChannels.isNotEmpty()) add(CategoryEntry(FAVORITES_ID, "★  Preferiti", favChannels.size))
            if (recent.isNotEmpty()) add(CategoryEntry(RECENT_ID, "Visti di recente", recent.size))
            add(CategoryEntry(ALL_ID, "Tutti i canali", catalog.channels.size))
            catalog.nonEmptyLive().forEach { add(CategoryEntry(it.id, it.name, catalog.channelsByCategory[it.id].orEmpty().size)) }
        }
    }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val effectiveId = selectedId?.takeIf { id -> entries.any { it.id == id } }
        ?: entries.firstOrNull { it.id != RECENT_ID && it.id != FAVORITES_ID && it.id != ALL_ID }?.id
        ?: ALL_ID

    val channels = remember(effectiveId, catalog, favChannels, recent) { channelsFor(effectiveId, catalog, favChannels, recent) }
    val categoryName = entries.firstOrNull { it.id == effectiveId }?.name.orEmpty()
    var focused by remember(effectiveId) { mutableStateOf(channels.firstOrNull()) }

    if (catalog.channels.isEmpty()) {
        MessageView("Nessun canale", "La tua lista non contiene canali in diretta.")
        return
    }

    Row(Modifier.fillMaxSize()) {
        CategoryList(entries, effectiveId, onSelect = { selectedId = it }, modifier = Modifier.padding(start = 16.dp))

        val firstFocus = rememberInitialFocus()
        val listState = rememberLazyListState()
        LaunchedEffect(effectiveId) { listState.scrollToItem(0) }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .focusRestorer(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            itemsIndexed(channels, key = { _, ch -> ch.id }) { index, ch ->
                ChannelRow(
                    channel = ch,
                    favorite = UserStore.liveKey(ch.id) in favorites,
                    modifier = if (index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                    onFocused = { focused = ch },
                    onClick = { navigator.play(PlayRequest.Live(channels, index, categoryName)) },
                    onLongClick = { app.scope.launch { app.userStore.toggleFavorite(UserStore.liveKey(ch.id)) } },
                )
            }
        }

        ChannelPreview(focused, Modifier.width(440.dp).fillMaxHeight())
    }
}

private fun channelsFor(id: String, c: Catalog, fav: List<Channel>, recent: List<Channel>): List<Channel> = when (id) {
    FAVORITES_ID -> fav
    RECENT_ID -> recent
    ALL_ID -> c.channels
    else -> c.channelsByCategory[id].orEmpty()
}

@Composable
private fun ChannelRow(
    channel: Channel,
    favorite: Boolean,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Palette.Surface,
            contentColor = Palette.Text,
            focusedContainerColor = Palette.SurfaceFocus,
            focusedContentColor = Palette.Text,
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                androidx.compose.foundation.BorderStroke(2.dp, Palette.Accent),
                shape = RoundedCornerShape(12.dp),
            ),
        ),
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                channel.number.takeIf { it > 0 }?.toString().orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                color = Palette.TextDim,
                modifier = Modifier.width(48.dp),
            )
            Box(
                Modifier.size(width = 72.dp, height = 44.dp).clip(RoundedCornerShape(6.dp)).background(Palette.SurfaceHigh),
                contentAlignment = Alignment.Center,
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    AsyncImage(channel.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(4.dp))
                } else {
                    Text(channel.name.take(3).uppercase(), style = MaterialTheme.typography.labelMedium, color = Palette.TextMuted)
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                channel.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (favorite) Icon(Icons.Rounded.Star, contentDescription = "Preferito", tint = Palette.Accent, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun ChannelPreview(channel: Channel?, modifier: Modifier = Modifier) {
    val app = LocalApp.current
    var epg by remember { mutableStateOf<List<EpgProgram>?>(null) }
    LaunchedEffect(channel?.id) {
        epg = null
        val ch = channel ?: return@LaunchedEffect
        delay(350)
        epg = app.catalog.epg(ch)
    }
    Column(
        modifier
            .background(Palette.Surface)
            .padding(32.dp)
    ) {
        if (channel == null) return@Column
        Box(
            Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(14.dp)).background(Palette.SurfaceHigh),
            contentAlignment = Alignment.Center,
        ) {
            if (!channel.logo.isNullOrBlank()) {
                AsyncImage(channel.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(36.dp))
            }
            LiveBadge(Modifier.align(Alignment.TopStart).padding(12.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(channel.name, style = MaterialTheme.typography.headlineSmall, color = Palette.Text, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(20.dp))

        val now = System.currentTimeMillis()
        val list = epg
        when {
            list == null -> Text("Caricamento guida…", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim)
            list.isEmpty() -> Text("Guida TV non disponibile per questo canale", style = MaterialTheme.typography.bodyMedium, color = Palette.TextDim)
            else -> {
                val current = list.firstOrNull { it.startMs <= now && it.endMs > now }
                if (current != null) {
                    Text("IN ONDA", style = MaterialTheme.typography.labelSmall, color = Palette.Accent)
                    Spacer(Modifier.height(4.dp))
                    Text(current.title, style = MaterialTheme.typography.titleLarge, color = Palette.Text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${Format.time(current.startMs)} – ${Format.time(current.endMs)}",
                        style = MaterialTheme.typography.bodySmall, color = Palette.TextMuted,
                    )
                    Spacer(Modifier.height(8.dp))
                    ProgressLine(current.progressAt(now))
                    current.description?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = Palette.TextMuted, maxLines = 4, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(20.dp))
                }
                val upcoming = list.filter { it.startMs > now }.take(4)
                if (upcoming.isNotEmpty()) {
                    Text("A SEGUIRE", style = MaterialTheme.typography.labelSmall, color = Palette.TextDim)
                    Spacer(Modifier.height(8.dp))
                    upcoming.forEach { p ->
                        Row(Modifier.padding(vertical = 5.dp)) {
                            Text(Format.time(p.startMs), style = MaterialTheme.typography.bodyMedium, color = Palette.TextMuted, modifier = Modifier.width(60.dp))
                            Text(p.title, style = MaterialTheme.typography.bodyMedium, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            "OK per guardare · Tieni premuto OK per aggiungere ai preferiti",
            style = MaterialTheme.typography.bodySmall,
            color = Palette.TextDim.copy(alpha = 0.8f),
        )
    }
}

