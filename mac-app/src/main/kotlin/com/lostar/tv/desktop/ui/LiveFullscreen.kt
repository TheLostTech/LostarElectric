package com.lostar.tv.desktop.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.DesktopStore
import com.lostar.tv.desktop.video.LiveSession
import com.lostar.tv.desktop.video.VideoView
import com.lostar.tv.ui.Format
import kotlinx.coroutines.delay

/**
 * Full-screen live TV. Clicking the channel name opens a translucent drop-down over the video
 * with the channel list (and what's on) and the guide of the current channel, without stopping playback.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveFullscreen(state: AppState) {
    val session = state.live
    val channel = session.current
    val data by state.store.data.collectAsState()
    var dropdown by remember { mutableStateOf(false) }
    var controls by remember { mutableStateOf(true) }
    var lastMove by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var epg by remember { mutableStateOf<List<EpgProgram>?>(null) }

    LaunchedEffect(channel?.id) {
        epg = null
        channel?.let { epg = state.catalog.epg(it) }
    }
    LaunchedEffect(lastMove, dropdown) {
        controls = true
        if (!dropdown) {
            delay(3500)
            controls = false
        }
    }
    fun poke() { lastMove = System.currentTimeMillis() }

    DisposableEffect(Unit) {
        state.keyHandler = { e ->
            if (e.type != KeyEventType.KeyDown) false
            else when (e.key) {
                Key.DirectionUp, Key.PageUp -> { session.zap(+1); poke(); true }
                Key.DirectionDown, Key.PageDown -> { session.zap(-1); poke(); true }
                Key.Enter, Key.L -> { dropdown = !dropdown; true }
                Key.F -> { state.toggleWindowFullscreen(); true }
                Key.Escape -> {
                    if (dropdown) dropdown = false else state.liveFullscreen = false
                    true
                }
                else -> false
            }
        }
        onDispose { state.keyHandler = null }
    }

    Box(
        Modifier.fillMaxSize().background(Color.Black)
            .onPointerEvent(PointerEventType.Move) { poke() }
    ) {
        VideoView(session.video, Modifier.fillMaxSize())

        // Click on the video closes the drop-down.
        Box(
            Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                if (dropdown) dropdown = false else poke()
            }
        )

        when (session.phase) {
            LiveSession.Phase.CONNECTING, LiveSession.Phase.SWITCHING -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Spinner(size = 52.dp)
                if (session.phase == LiveSession.Phase.SWITCHING) {
                    Spacer(Modifier.height(12.dp))
                    Text("Cambio sorgente…", color = Color.White)
                }
            }
            LiveSession.Phase.FAILED -> Column(
                Modifier.align(Alignment.Center).clip(RoundedCornerShape(16.dp)).background(Color.Black.copy(alpha = 0.75f)).padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Canale non disponibile", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text("Nessuna delle ${session.ranked.size} sorgenti risponde in questo momento.", color = Palette.TextMuted)
                Spacer(Modifier.height(16.dp))
                PillButton("Riprova", primary = true, onClick = { session.retry() })
            }
            else -> Unit
        }

        NoticeToast(session, Modifier.align(Alignment.BottomEnd).padding(28.dp))

        AnimatedVisibility(controls || dropdown, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.TopStart).fillMaxWidth()) {
            Column(Modifier.fillMaxWidth()) {
                TopControls(
                    state = state,
                    dropdownOpen = dropdown,
                    onToggleDropdown = { dropdown = !dropdown },
                )
                AnimatedVisibility(dropdown, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    ChannelDropdown(state, epg, favorites = data.favorites, onPick = { i ->
                        session.zapTo(i)
                    })
                }
            }
        }

        AnimatedVisibility(controls && !dropdown, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()) {
            BottomInfo(epg)
        }
    }
}

@Composable
private fun TopControls(state: AppState, dropdownOpen: Boolean, onToggleDropdown: () -> Unit) {
    val session = state.live
    val channel = session.current
    var sourcesMenu by remember { mutableStateOf(false) }
    var tracksMenu by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)))
            .padding(start = 28.dp, end = 20.dp, top = 22.dp, bottom = 36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChannelLogo(channel?.logo, channel?.name.orEmpty(), Modifier.size(width = 84.dp, height = 50.dp))
        Spacer(Modifier.width(16.dp))
        // The channel name is the drop-down trigger.
        Row(
            Modifier.hoverClick(hoverColor = Color.White.copy(alpha = 0.10f), selected = dropdownOpen, selectedColor = Color.White.copy(alpha = 0.14f), onClick = onToggleDropdown)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveBadge()
                    Spacer(Modifier.width(10.dp))
                    Text(session.listName, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                }
                Text(channel?.name.orEmpty(), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Icon(if (dropdownOpen) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown, "Canali", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.weight(1f))
        Text(sourceLine(session), color = Palette.Accent, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(14.dp))
        Box {
            PillButton("", icon = Icons.Rounded.SwapHoriz, onClick = { sourcesMenu = true })
            DropdownMenu(sourcesMenu, onDismissRequest = { sourcesMenu = false }) {
                Text("Sorgente (cambio automatico attivo)", color = Palette.TextDim, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                session.ranked.indices.forEach { i ->
                    DropdownMenuItem(
                        text = { Text((if (i == session.variantPos) "✓ " else "   ") + session.variantLabel(i)) },
                        onClick = { session.chooseVariant(i); sourcesMenu = false },
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Box {
            PillButton("", icon = Icons.Rounded.Subtitles, onClick = { tracksMenu = true })
            TracksMenu(state, tracksMenu) { tracksMenu = false }
        }
        Spacer(Modifier.width(8.dp))
        PillButton("", icon = Icons.Rounded.Fullscreen, onClick = { state.toggleWindowFullscreen() })
        Spacer(Modifier.width(8.dp))
        PillButton("", icon = Icons.Rounded.Close, onClick = { state.liveFullscreen = false })
    }
}

@Composable
fun TracksMenu(state: AppState, expanded: Boolean, player: com.lostar.tv.desktop.video.VideoPlayer = state.livePlayer, onDismiss: () -> Unit) {
    DropdownMenu(expanded, onDismissRequest = onDismiss) {
        val audio = remember(expanded, player.tracksVersion) { runCatching { player.audioTracks() }.getOrDefault(emptyList()) }
        val subs = remember(expanded, player.tracksVersion) { runCatching { player.subtitleTracks() }.getOrDefault(emptyList()) }
        Text("Audio", color = Palette.TextDim, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
        if (audio.isEmpty()) Text("Traccia unica", color = Palette.TextMuted, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
        audio.forEach { t ->
            DropdownMenuItem(text = { Text((if (t.selected) "✓ " else "   ") + t.label) }, onClick = { player.setAudioTrack(t.id); onDismiss() })
        }
        HorizontalDivider()
        Text("Sottotitoli", color = Palette.TextDim, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
        if (subs.isEmpty()) Text("Nessuno", color = Palette.TextMuted, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
        subs.forEach { t ->
            DropdownMenuItem(text = { Text((if (t.selected) "✓ " else "   ") + t.label) }, onClick = { player.setSubtitleTrack(t.id); onDismiss() })
        }
    }
}

/** The "exploded" drop-down: translucent panel, text kept semi-transparent so the video shows through. */
@Composable
private fun ChannelDropdown(state: AppState, epg: List<EpgProgram>?, favorites: Set<String>, onPick: (Int) -> Unit) {
    val session = state.live
    val channels = session.channels
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (session.index - 3).coerceAtLeast(0))
    Row(
        Modifier.padding(start = 28.dp, end = 28.dp)
            .fillMaxWidth(0.82f)
            .height(560.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(Modifier.weight(1.2f).fillMaxHeight()) {
            Text(
                "${session.listName} · ${channels.size} canali", color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
            LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                itemsIndexed(channels, key = { _, c -> c.id }) { i, ch ->
                    ChannelRow(
                        state, ch,
                        playing = i == session.index,
                        favorite = DesktopStore.isLiveFavorite(ch, favorites),
                        onClick = { onPick(i) },
                        translucent = true,
                    )
                }
            }
        }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Text(
                "In onda su ${session.current?.name.orEmpty()}", color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Guide(epg, translucent = true, max = 7)
            Spacer(Modifier.weight(1f))
            Text("↑ ↓ cambia canale · Invio apre/chiude · Esc esce · F schermo intero", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BottomInfo(epg: List<EpgProgram>?) {
    val now = System.currentTimeMillis()
    val current = epg?.firstOrNull { it.startMs <= now && it.endMs > now }
    val next = epg?.firstOrNull { it.startMs >= (current?.endMs ?: now) }
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
            .padding(start = 40.dp, end = 40.dp, top = 50.dp, bottom = 30.dp)
    ) {
        if (current != null) {
            Text("${Format.time(current.startMs)} – ${Format.time(current.endMs)}  ·  ${current.title}", color = Color.White, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            ProgressLine(current.progressAt(now), Modifier.width(560.dp))
        }
        if (next != null) {
            Spacer(Modifier.height(8.dp))
            Text("Dopo · ${Format.time(next.startMs)}  ${next.title}", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(6.dp))
        Text("Clicca sul nome del canale per la lista · ↑ ↓ cambia canale", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
    }
}
