package com.lostar.tv.desktop.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Forward30
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.VodRequest
import com.lostar.tv.desktop.WatchEntry
import com.lostar.tv.desktop.video.VideoPlayer
import com.lostar.tv.desktop.video.VideoView
import com.lostar.tv.ui.Format
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VodPlayerScreen(state: AppState, request: VodRequest) {
    var index by remember(request) { mutableIntStateOf(request.index) }
    var startAt by remember(request) { mutableLongStateOf(request.startMs) }
    var controls by remember { mutableStateOf(true) }
    var lastMove by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var tracksMenu by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var seeking by remember { mutableStateOf<Float?>(null) }
    val player = remember(request) { VideoPlayer(state.scope, AppState.USER_AGENT).also { it.setVolume(state.store.data.value.volume) } }
    val item = request.queue[index]

    fun save(finished: Boolean = false) {
        val cur = request.queue[index]
        val d = player.lengthMs
        if (d <= 0) return
        state.store.saveProgress(
            WatchEntry(cur.key, cur.section, cur.title, cur.subtitle, cur.image, cur.url, if (finished) d else player.timeMs, d, System.currentTimeMillis(), cur.seriesId)
        )
    }

    fun close() {
        save()
        state.vod = null
    }

    DisposableEffect(player) {
        player.listener = object : VideoPlayer.Listener {
            override fun onError() { error = true }
            override fun onFinished() {
                save(finished = true)
                if (index < request.queue.lastIndex) index++ else state.vod = null
            }
        }
        onDispose { player.release() }
    }
    LaunchedEffect(index) {
        error = false
        player.play(request.queue[index].url, startAt, networkCachingMs = 3000)
        startAt = 0
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            if (player.isPlaying) save()
        }
    }
    LaunchedEffect(lastMove, player.isPlaying, tracksMenu) {
        controls = true
        if (player.isPlaying && !tracksMenu) {
            delay(3000)
            controls = false
        }
    }
    fun poke() { lastMove = System.currentTimeMillis() }

    DisposableEffect(Unit) {
        val previous = state.keyHandler
        state.keyHandler = { e ->
            if (e.type != KeyEventType.KeyDown) false
            else when (e.key) {
                Key.Spacebar, Key.K -> { player.togglePause(); poke(); true }
                Key.DirectionLeft, Key.J -> { player.seekTo(player.timeMs - 10_000); poke(); true }
                Key.DirectionRight, Key.L -> { player.seekTo(player.timeMs + 30_000); poke(); true }
                Key.F -> { state.toggleWindowFullscreen(); true }
                Key.Escape -> { close(); true }
                else -> false
            }
        }
        onDispose { state.keyHandler = previous }
    }

    Box(Modifier.fillMaxSize().background(Color.Black).onPointerEvent(PointerEventType.Move) { poke() }) {
        VideoView(player, Modifier.fillMaxSize())
        Box(
            Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                player.togglePause()
                poke()
            }
        )
        if (player.frame == null && !error) Spinner(Modifier.align(Alignment.Center), size = 52.dp)
        if (error) {
            CenterMessage("Impossibile riprodurre", "Il server non ha inviato il video. Riprova più tardi.") {
                PillButton("Chiudi", primary = true, onClick = ::close)
            }
        }

        AnimatedVisibility(controls, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.TopStart).fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                    .padding(start = 32.dp, end = 20.dp, top = 22.dp, bottom = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(item.title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    item.subtitle?.let { Text(it, color = Color.White.copy(alpha = 0.75f)) }
                }
                Box {
                    PillButton("", icon = Icons.Rounded.Subtitles, onClick = { tracksMenu = true })
                    TracksMenu(state, tracksMenu, player) { tracksMenu = false }
                }
                Spacer(Modifier.width(8.dp))
                PillButton("", icon = Icons.Rounded.Fullscreen, onClick = { state.toggleWindowFullscreen() })
                Spacer(Modifier.width(8.dp))
                PillButton("", icon = Icons.Rounded.Close, onClick = ::close)
            }
        }

        AnimatedVisibility(controls, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
                    .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 24.dp)
            ) {
                val length = player.lengthMs.coerceAtLeast(1)
                Slider(
                    value = seeking ?: (player.timeMs.toFloat() / length).coerceIn(0f, 1f),
                    onValueChange = { seeking = it; poke() },
                    onValueChangeFinished = {
                        seeking?.let { player.seekTo((it * length).toLong()) }
                        seeking = null
                    },
                    colors = SliderDefaults.colors(thumbColor = Palette.Accent, activeTrackColor = Palette.Accent, inactiveTrackColor = Color.White.copy(alpha = 0.25f)),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PillButton("", icon = Icons.Rounded.Replay10, onClick = { player.seekTo(player.timeMs - 10_000) })
                    Spacer(Modifier.width(8.dp))
                    PillButton("", icon = if (player.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, primary = true, onClick = { player.togglePause() })
                    Spacer(Modifier.width(8.dp))
                    PillButton("", icon = Icons.Rounded.Forward30, onClick = { player.seekTo(player.timeMs + 30_000) })
                    Spacer(Modifier.width(16.dp))
                    val shown = seeking?.let { (it * length).toLong() } ?: player.timeMs
                    Text("${Format.clock(shown)} / ${if (player.lengthMs > 0) Format.clock(player.lengthMs) else "--:--"}", color = Color.White)
                    Spacer(Modifier.weight(1f))
                    if (index < request.queue.lastIndex) {
                        Text("Poi: ${request.queue[index + 1].subtitle ?: request.queue[index + 1].title}", color = Color.White.copy(alpha = 0.7f))
                        Spacer(Modifier.width(10.dp))
                        PillButton("Successivo", icon = Icons.Rounded.SkipNext, onClick = { save(); index++ })
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text("Spazio pausa · ← −10 s · → +30 s · F schermo intero · Esc chiudi", color = Color.White.copy(alpha = 0.45f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
