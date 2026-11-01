package com.lostar.tv.ui.player

import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.EpgProgram
import com.lostar.tv.data.WatchEntry
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.components.AccentButton
import com.lostar.tv.ui.components.LiveBadge
import com.lostar.tv.ui.components.ProgressLine
import com.lostar.tv.ui.components.Spinner
import com.lostar.tv.ui.player.PlayerEngine.friendly
import com.lostar.tv.ui.player.PlayerEngine.isNetwork
import com.lostar.tv.ui.player.PlayerEngine.load
import com.lostar.tv.ui.player.PlayerEngine.looksLikeWrongFormat
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val resizeModes = listOf(
    AspectRatioFrameLayout.RESIZE_MODE_FIT to "Adatta",
    AspectRatioFrameLayout.RESIZE_MODE_ZOOM to "Riempi (zoom)",
    AspectRatioFrameLayout.RESIZE_MODE_FILL to "Allunga",
)

@Composable
fun PlayerScreen(request: PlayRequest, onExit: () -> Unit) {
    when (request) {
        is PlayRequest.Live -> LiveFullscreen(request, onExit)
        is PlayRequest.Vod -> VodScreen(request, onExit)
    }
}

/** Video output for [player]. Several surfaces can show the same player; the newest one wins. */
@Composable
fun VideoSurface(player: ExoPlayer, modifier: Modifier = Modifier, resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT) {
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                useController = false
                setShutterBackgroundColor(AndroidColor.BLACK)
                setKeepContentOnPlayerReset(true)
                keepScreenOn = true
                isFocusable = false
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                this.player = player
            }
        },
        update = {
            if (it.player !== player) it.player = player
            it.resizeMode = resizeMode
        },
        onRelease = { it.player = null },
        modifier = modifier,
    )
}

/** Pauses/resumes with the activity (Home button on the remote). */
@Composable
private fun OnAppBackground(onStop: () -> Unit, onStart: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val stop by androidx.compose.runtime.rememberUpdatedState(onStop)
    val start by androidx.compose.runtime.rememberUpdatedState(onStart)
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> stop()
                Lifecycle.Event.ON_START -> start()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun VodScreen(request: PlayRequest.Vod, onExit: () -> Unit) {
    val app = LocalApp.current
    val context = LocalContext.current
    val player = remember { PlayerEngine.create(context, app.http) }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    DisposableEffect(Unit) { onDispose { player.release() } }
    OnAppBackground(onStop = { player.pause() }, onStart = { if (player.playbackState != Player.STATE_IDLE) player.play() })

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        VideoSurface(player, Modifier.fillMaxSize(), resizeMode)
        VodOverlay(player, request, resizeMode, { resizeMode = it }, onExit)
    }
}

/** Shared playback status: buffering spinner, errors with automatic retry/HLS fallback. */
private class PlaybackStatus {
    var buffering by mutableStateOf(true)
    var playing by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var ended by mutableIntStateOf(0)
    var tracksVersion by mutableIntStateOf(0)
}

@Composable
private fun rememberPlaybackStatus(player: ExoPlayer, live: Boolean = false, onRetry: (forceHls: Boolean) -> Unit): PlaybackStatus {
    val status = remember { PlaybackStatus() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    DisposableEffect(player) {
        var retries = 0
        var triedHls = false
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                status.buffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    retries = 0
                    status.error = null
                }
                if (state == Player.STATE_ENDED) status.ended++
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                status.playing = isPlaying
            }

            override fun onTracksChanged(tracks: Tracks) {
                status.tracksVersion++
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                triedHls = mediaItem?.localConfiguration?.mimeType == androidx.media3.common.MimeTypes.APPLICATION_M3U8
                retries = 0
            }

            override fun onPlayerError(error: PlaybackException) {
                when {
                    error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                        player.seekToDefaultPosition()
                        player.prepare()
                    }
                    !triedHls && error.looksLikeWrongFormat() -> {
                        triedHls = true
                        onRetry(true)
                    }
                    error.isNetwork() && retries < (if (live) 4 else 2) -> {
                        retries++
                        status.buffering = true
                        scope.launch {
                            delay(1500L * retries)
                            player.prepare()
                        }
                    }
                    else -> {
                        status.buffering = false
                        status.error = error.friendly()
                    }
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }
    return status
}

// ---------------------------------------------------------------------------------------------
// Live TV (full screen) — playback, failover and zapping live in LiveSession
// ---------------------------------------------------------------------------------------------

@Composable
private fun LiveFullscreen(request: PlayRequest.Live, onExit: () -> Unit) {
    val app = LocalApp.current
    val session = app.liveSession
    val player = remember { session.player() }
    var resizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }
    DisposableEffect(Unit) {
        session.attach()
        onDispose { session.detach() }
    }
    OnAppBackground(onStop = { session.pauseForBackground() }, onStart = { session.resumeFromBackground() })
    LaunchedEffect(Unit) {
        if (session.phase == LiveSession.Phase.IDLE) session.start(request.channels, request.index, request.categoryName)
    }

    val channels = session.channels.ifEmpty { request.channels }
    val index = session.index.coerceIn(0, (channels.size - 1).coerceAtLeast(0))
    var pending by remember { mutableIntStateOf(index) }
    var infoVisible by remember { mutableStateOf(true) }
    var infoToken by remember { mutableIntStateOf(0) }
    var listVisible by remember { mutableStateOf(false) }
    var settingsVisible by remember { mutableStateOf(false) }
    var noticeVisible by remember { mutableStateOf(false) }
    var epg by remember { mutableStateOf<List<EpgProgram>>(emptyList()) }
    val rootFocus = remember { FocusRequester() }

    fun showInfo() {
        infoVisible = true
        infoToken++
    }

    fun zap(delta: Int) {
        if (channels.size < 2) return
        pending = (pending + delta).mod(channels.size)
        showInfo()
    }

    LaunchedEffect(session.index) { pending = session.index.coerceAtLeast(0) }
    // Debounced zapping: several quick presses only tune the last channel.
    LaunchedEffect(pending) {
        if (pending != session.index) {
            delay(550)
            session.zapTo(pending)
        }
    }
    val shown = channels.getOrNull(pending)
    LaunchedEffect(shown?.id) {
        epg = emptyList()
        shown?.let { epg = app.catalog.epg(it) }
    }
    LaunchedEffect(infoToken, infoVisible, listVisible) {
        if (infoVisible && !listVisible) {
            delay(5000)
            infoVisible = false
        }
    }
    LaunchedEffect(session.noticeToken) {
        if (session.notice != null) {
            noticeVisible = true
            delay(4500)
            noticeVisible = false
        }
    }
    LaunchedEffect(listVisible, settingsVisible) {
        if (!listVisible && !settingsVisible) runCatching { rootFocus.requestFocus() }
    }

    BackHandler {
        when {
            settingsVisible -> settingsVisible = false
            listVisible -> listVisible = false
            infoVisible -> infoVisible = false
            else -> onExit()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(rootFocus)
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown || listVisible || settingsVisible) return@onKeyEvent false
                when (e.key) {
                    Key.DirectionUp, Key.ChannelUp, Key.PageUp -> { zap(+1); true }
                    Key.DirectionDown, Key.ChannelDown, Key.PageDown -> { zap(-1); true }
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                        if (session.phase == LiveSession.Phase.FAILED) session.retry() else listVisible = true
                        true
                    }
                    Key.DirectionLeft, Key.Info, Key.Menu -> {
                        if (infoVisible) infoVisible = false else showInfo()
                        true
                    }
                    Key.DirectionRight -> { settingsVisible = true; true }
                    else -> false
                }
            }
            .focusable()
    ) {
        VideoSurface(player, Modifier.fillMaxSize(), resizeMode)

        when (session.phase) {
            LiveSession.Phase.CONNECTING, LiveSession.Phase.SWITCHING -> Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spinner(size = 56.dp)
                if (session.phase == LiveSession.Phase.SWITCHING) {
                    Spacer(Modifier.height(14.dp))
                    Text("Cambio sorgente…", style = MaterialTheme.typography.titleMedium, color = Palette.Text)
                }
            }
            LiveSession.Phase.FAILED -> ErrorBanner(
                "Nessuna delle ${session.ranked.size} sorgenti di questo canale funziona in questo momento. Premi OK per riprovare.",
                Modifier.align(Alignment.Center),
            )
            else -> Unit
        }

        AnimatedVisibility(
            visible = noticeVisible && session.notice != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp),
        ) {
            Text(
                session.notice.orEmpty(),
                style = MaterialTheme.typography.titleSmall,
                color = Palette.Text,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }

        AnimatedVisibility(
            visible = infoVisible && !listVisible && shown != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            if (shown != null) {
                LiveInfoBar(
                    shown, epg, position = pending + 1, total = channels.size, category = session.listName.ifEmpty { request.categoryName },
                    source = if (pending == session.index && session.ranked.size > 1)
                        session.variantLabel(session.variantPos) + " di ${session.ranked.size} · " + (if (session.manual) "manuale" else "automatica")
                    else null,
                )
            }
        }

        AnimatedVisibility(
            visible = listVisible,
            enter = slideInHorizontally { -it } + fadeIn(),
            exit = slideOutHorizontally { -it } + fadeOut(),
        ) {
            ChannelListPanel(channels, session.index, session.listName.ifEmpty { request.categoryName }) { i ->
                pending = i
                session.zapTo(i)
                listVisible = false
                showInfo()
            }
        }

        AnimatedVisibility(
            visible = settingsVisible,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            SettingsPanel(
                player, session.tracksVersion, resizeMode, { resizeMode = it },
                sources = session.ranked.indices.map { session.variantLabel(it) },
                selectedSource = session.variantPos,
                onSource = { session.chooseVariant(it) },
            ) { settingsVisible = false }
        }
    }
}

@Composable
private fun LiveInfoBar(channel: LiveChannel, epg: List<EpgProgram>, position: Int, total: Int, category: String, source: String?) {
    val now = System.currentTimeMillis()
    val current = epg.firstOrNull { it.startMs <= now && it.endMs > now }
    val next = epg.firstOrNull { it.startMs >= (current?.endMs ?: now) }
    Box(
        Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))))
            .padding(start = 56.dp, end = 56.dp, top = 80.dp, bottom = 40.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(width = 150.dp, height = 90.dp).clip(RoundedCornerShape(12.dp)).background(Palette.SurfaceHigh.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center,
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    AsyncImage(channel.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(12.dp))
                } else {
                    Text(channel.name.take(4).uppercase(), style = MaterialTheme.typography.titleLarge, color = Palette.Text)
                }
            }
            Spacer(Modifier.width(28.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LiveBadge()
                    Spacer(Modifier.width(12.dp))
                    Text("$position / $total · $category", style = MaterialTheme.typography.labelMedium, color = Palette.TextMuted, maxLines = 1)
                }
                Spacer(Modifier.height(6.dp))
                Text(channel.name, style = MaterialTheme.typography.headlineMedium, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (current != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${Format.time(current.startMs)} – ${Format.time(current.endMs)}   ${current.title}",
                        style = MaterialTheme.typography.titleMedium, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    ProgressLine(current.progressAt(now), Modifier.width(520.dp))
                }
                if (next != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("Dopo · ${Format.time(next.startMs)}  ${next.title}", style = MaterialTheme.typography.bodyMedium, color = Palette.TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(Format.time(now), style = MaterialTheme.typography.headlineSmall, color = Palette.Text)
                if (source != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(source, style = MaterialTheme.typography.bodySmall, color = Palette.Accent, maxLines = 1)
                }
                Spacer(Modifier.height(8.dp))
                Text("▲▼ canale · OK lista · ▶ sorgente e audio", style = MaterialTheme.typography.bodySmall, color = Palette.TextDim)
            }
        }
    }
}

@Composable
private fun ChannelListPanel(channels: List<LiveChannel>, current: Int, title: String, onPick: (Int) -> Unit) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (current - 3).coerceAtLeast(0))
    val currentFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        repeat(10) {
            delay(40)
            if (runCatching { currentFocus.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }
    Column(
        Modifier
            .fillMaxHeight()
            .width(460.dp)
            .background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.95f), Color.Black.copy(alpha = 0.8f))))
            .padding(top = 36.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Palette.Text, modifier = Modifier.padding(horizontal = 28.dp))
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(channels, key = { _, c -> c.id }) { i, ch ->
                Surface(
                    onClick = { onPick(i) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .then(if (i == current) Modifier.focusRequester(currentFocus) else Modifier),
                    shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(10.dp)),
                    scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = if (i == current) Palette.SurfaceHigh else Color.Transparent,
                        contentColor = if (i == current) Palette.Accent else Palette.Text,
                        focusedContainerColor = Palette.Accent,
                        focusedContentColor = Palette.OnAccent,
                    ),
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(44.dp))
                        if (!ch.logo.isNullOrBlank()) {
                            AsyncImage(ch.logo, null, contentScale = ContentScale.Fit, modifier = Modifier.size(width = 54.dp, height = 34.dp))
                            Spacer(Modifier.width(12.dp))
                        }
                        Text(ch.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Movies & episodes
// ---------------------------------------------------------------------------------------------

@Composable
private fun VodOverlay(
    player: ExoPlayer,
    request: PlayRequest.Vod,
    resizeMode: Int,
    onResize: (Int) -> Unit,
    onExit: () -> Unit,
) {
    val app = LocalApp.current
    var index by remember { mutableIntStateOf(request.index.coerceIn(0, request.queue.lastIndex)) }
    val item = request.queue[index]
    var startAt by remember { mutableLongStateOf(request.startPositionMs) }
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var seekTarget by remember { mutableStateOf<Long?>(null) }
    var seekToken by remember { mutableIntStateOf(0) }
    var overlayVisible by remember { mutableStateOf(true) }
    var overlayToken by remember { mutableIntStateOf(0) }
    var settingsVisible by remember { mutableStateOf(false) }
    val rootFocus = remember { FocusRequester() }

    val status = rememberPlaybackStatus(player, live = false) { hls ->
        player.load(request.queue[index].url, forceHls = hls, startMs = player.currentPosition.coerceAtLeast(startAt))
    }

    // Reads the queue position from state so long-lived effects never save a stale item.
    fun save(finished: Boolean = false) {
        val item = request.queue[index]
        val d = player.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: return
        val p = if (finished) d else player.currentPosition
        val entry = WatchEntry(
            key = item.key, section = item.section, title = item.title, subtitle = item.subtitle, image = item.image,
            url = item.url, positionMs = p, durationMs = d, updatedAt = System.currentTimeMillis(), seriesId = item.seriesId,
        )
        app.scope.launch { app.userStore.saveProgress(entry) }
    }

    fun showOverlay() {
        overlayVisible = true
        overlayToken++
    }

    fun seekBy(deltaMs: Long) {
        val d = duration.takeIf { it > 0 } ?: return
        seekTarget = ((seekTarget ?: player.currentPosition) + deltaMs).coerceIn(0, d - 1000)
        seekToken++
        showOverlay()
    }

    LaunchedEffect(index) {
        status.error = null
        player.load(item.url, forceHls = false, startMs = startAt)
        startAt = 0
    }
    LaunchedEffect(Unit) {
        while (true) {
            position = player.currentPosition
            duration = player.duration.takeIf { it != C.TIME_UNSET } ?: 0
            delay(500)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            if (player.isPlaying) save()
        }
    }
    LaunchedEffect(seekToken) {
        val t = seekTarget ?: return@LaunchedEffect
        delay(700)
        player.seekTo(t)
        position = t
        seekTarget = null
    }
    LaunchedEffect(overlayToken, overlayVisible, status.playing, settingsVisible) {
        if (overlayVisible && status.playing && !settingsVisible) {
            delay(4500)
            overlayVisible = false
        }
    }
    LaunchedEffect(status.ended) {
        if (status.ended == 0) return@LaunchedEffect
        save(finished = true)
        if (index < request.queue.lastIndex) {
            index++
            showOverlay()
        } else {
            onExit()
        }
    }
    LaunchedEffect(settingsVisible) {
        if (!settingsVisible) runCatching { rootFocus.requestFocus() }
    }
    DisposableEffect(Unit) { onDispose { save() } }

    BackHandler {
        when {
            settingsVisible -> settingsVisible = false
            else -> {
                save()
                onExit()
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .focusRequester(rootFocus)
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown || settingsVisible) return@onKeyEvent false
                when (e.key) {
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter, Key.MediaPlayPause, Key.Spacebar -> {
                        if (player.isPlaying) player.pause() else player.play()
                        showOverlay(); true
                    }
                    Key.MediaPlay -> { player.play(); showOverlay(); true }
                    Key.MediaPause -> { player.pause(); showOverlay(); true }
                    Key.DirectionLeft, Key.MediaRewind -> { seekBy(-10_000); true }
                    Key.DirectionRight, Key.MediaFastForward -> { seekBy(30_000); true }
                    Key.MediaNext -> {
                        if (index < request.queue.lastIndex) { save(); index++ }
                        true
                    }
                    Key.MediaPrevious -> {
                        if (index > 0) { save(); index-- }
                        true
                    }
                    Key.DirectionUp, Key.Menu -> { settingsVisible = true; true }
                    Key.DirectionDown, Key.Info -> { showOverlay(); true }
                    else -> false
                }
            }
            .focusable()
    ) {
        if (status.buffering && status.error == null) Spinner(Modifier.align(Alignment.Center), size = 56.dp)
        status.error?.let { msg ->
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                ErrorBanner(msg)
            }
        }

        AnimatedVisibility(
            visible = overlayVisible || seekTarget != null || !status.playing,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)))
                        .padding(start = 56.dp, end = 56.dp, top = 40.dp, bottom = 60.dp)
                ) {
                    Column {
                        Text(item.title, style = MaterialTheme.typography.headlineMedium, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        item.subtitle?.let { Text(it, style = MaterialTheme.typography.titleMedium, color = Palette.TextMuted, maxLines = 1) }
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))))
                        .padding(start = 56.dp, end = 56.dp, top = 60.dp, bottom = 40.dp)
                ) {
                    val shown = seekTarget ?: position
                    val progress = if (duration > 0) shown.toFloat() / duration else 0f
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (status.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Palette.Text,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(Format.clock(shown), style = MaterialTheme.typography.titleMedium, color = Palette.Text)
                        Spacer(Modifier.width(16.dp))
                        ProgressLine(progress, Modifier.weight(1f), height = 6.dp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            if (duration > 0) "-" + Format.clock(duration - shown) else "--:--",
                            style = MaterialTheme.typography.titleMedium, color = Palette.TextMuted,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        Text(
                            "OK pausa · ◀ -10s · ▶ +30s · ▲ audio e sottotitoli",
                            style = MaterialTheme.typography.bodySmall, color = Palette.TextDim,
                        )
                        Spacer(Modifier.weight(1f))
                        if (index < request.queue.lastIndex) {
                            Text(
                                "Poi: ${request.queue[index + 1].subtitle ?: request.queue[index + 1].title}",
                                style = MaterialTheme.typography.bodySmall, color = Palette.TextMuted, maxLines = 1,
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = settingsVisible,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            SettingsPanel(player, status.tracksVersion, resizeMode, onResize) { settingsVisible = false }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Shared pieces
// ---------------------------------------------------------------------------------------------

@Composable
private fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(horizontal = 36.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Impossibile riprodurre", style = MaterialTheme.typography.titleLarge, color = Palette.Text)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Palette.TextMuted)
        Spacer(Modifier.height(8.dp))
        Text("Indietro per uscire", style = MaterialTheme.typography.bodySmall, color = Palette.TextDim)
    }
}

/** Audio, subtitles and picture format, sliding in from the right. */
@Composable
private fun SettingsPanel(
    player: ExoPlayer,
    tracksVersion: Int,
    resizeMode: Int,
    onResize: (Int) -> Unit,
    sources: List<String> = emptyList(),
    selectedSource: Int = -1,
    onSource: (Int) -> Unit = {},
    onClose: () -> Unit,
) {
    var refresh by remember { mutableIntStateOf(0) }
    val text = remember(tracksVersion, refresh) { TrackOptions.of(player, C.TRACK_TYPE_TEXT) }
    val audioSel = remember(tracksVersion, refresh) { TrackOptions.of(player, C.TRACK_TYPE_AUDIO) }
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        repeat(10) {
            delay(40)
            if (runCatching { firstFocus.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }
    LazyColumn(
        Modifier
            .fillMaxHeight()
            .width(420.dp)
            .background(Color.Black.copy(alpha = 0.92f)),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (sources.size > 1) {
            item { PanelHeader("Sorgente") }
            item { PanelNote("Se il canale si blocca l'app cambia sorgente da sola.") }
            itemsIndexed(sources) { i, label ->
                PanelOption(label, i == selectedSource, if (i == 0) Modifier.focusRequester(firstFocus) else Modifier) { onSource(i) }
            }
        }
        item { PanelHeader("Audio") }
        if (audioSel.isEmpty()) item { PanelNote("Nessuna traccia audio selezionabile") }
        itemsIndexed(audioSel) { i, opt ->
            PanelOption(opt.label, opt.selected, if (sources.size <= 1 && i == 0) Modifier.focusRequester(firstFocus) else Modifier) {
                TrackOptions.apply(player, opt); refresh++
            }
        }
        item { PanelHeader("Sottotitoli") }
        itemsIndexed(text) { i, opt ->
            PanelOption(opt.label, opt.selected, if (sources.size <= 1 && audioSel.isEmpty() && i == 0) Modifier.focusRequester(firstFocus) else Modifier) {
                TrackOptions.apply(player, opt); refresh++
            }
        }
        item { PanelHeader("Formato immagine") }
        resizeModes.forEach { (mode, label) ->
            item { PanelOption(label, mode == resizeMode) { onResize(mode) } }
        }
        item {
            Spacer(Modifier.height(16.dp))
            AccentButton("Chiudi", onClick = onClose)
        }
    }
}

@Composable
private fun PanelHeader(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = Palette.Accent,
        modifier = Modifier.padding(top = 18.dp, bottom = 6.dp, start = 8.dp),
    )
}

@Composable
private fun PanelNote(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = Palette.TextDim, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
}

@Composable
private fun PanelOption(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(10.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = if (selected) Palette.Accent else Palette.Text,
            focusedContainerColor = Palette.Text,
            focusedContentColor = Palette.OnAccent,
        ),
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (selected) Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}
