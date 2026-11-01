package com.lostar.tv.desktop.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

/** libVLC comes from the VLC app installed on the Mac (/Applications/VLC.app). */
object Vlc {
    val available: Boolean by lazy { runCatching { NativeDiscovery().discover() }.getOrDefault(false) }

    val factory: MediaPlayerFactory by lazy {
        MediaPlayerFactory("--no-video-title-show", "--no-snapshot-preview", "--quiet")
    }
}

data class TrackItem(val id: Int, val label: String, val selected: Boolean)

/**
 * A VLC player that renders frames into Compose (no Swing overlay), so menus and the channel
 * drop-down can be drawn transparently on top of the video.
 */
class VideoPlayer(private val scope: CoroutineScope, private val userAgent: String) {
    interface Listener {
        fun onPlaying() {}
        fun onBuffering(percent: Float) {}
        fun onError() {}
        fun onFinished() {}
        fun onTime(ms: Long) {}
    }

    var frame by mutableStateOf<ImageBitmap?>(null); private set
    var isPlaying by mutableStateOf(false); private set
    var timeMs by mutableLongStateOf(0L); private set
    var lengthMs by mutableLongStateOf(0L); private set
    var tracksVersion by mutableIntStateOf(0); private set
    var listener: Listener? = null

    private val mp: EmbeddedMediaPlayer = Vlc.factory.mediaPlayers().newEmbeddedMediaPlayer()
    private val busy = AtomicBoolean(false)
    @Volatile private var bytes = ByteArray(0)

    private val formatCallback = object : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat =
            RV32BufferFormat(sourceWidth, sourceHeight)

        override fun allocatedBuffers(buffers: Array<ByteBuffer>) = Unit
    }

    private val renderCallback = object : RenderCallback {
        override fun display(mediaPlayer: MediaPlayer, nativeBuffers: Array<ByteBuffer>, bufferFormat: BufferFormat) {
            // Drop frames while the UI thread is still busy with the previous one.
            if (!busy.compareAndSet(false, true)) return
            val w = bufferFormat.width
            val h = bufferFormat.height
            val size = w * h * 4
            if (bytes.size != size) bytes = ByteArray(size)
            val src = nativeBuffers[0]
            src.rewind()
            src.get(bytes, 0, minOf(size, src.remaining()))
            val data = bytes
            scope.launch(Dispatchers.Main) {
                try {
                    val info = ImageInfo(w, h, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
                    frame = org.jetbrains.skia.Image.makeRaster(info, data, w * 4).toComposeImageBitmap()
                } finally {
                    busy.set(false)
                }
            }
        }
    }

    init {
        mp.videoSurface().set(Vlc.factory.videoSurfaces().newVideoSurface(formatCallback, renderCallback, true))
        mp.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun playing(mediaPlayer: MediaPlayer) = ui { isPlaying = true; listener?.onPlaying() }
            override fun paused(mediaPlayer: MediaPlayer) = ui { isPlaying = false }
            override fun stopped(mediaPlayer: MediaPlayer) = ui { isPlaying = false }
            override fun finished(mediaPlayer: MediaPlayer) = ui { isPlaying = false; listener?.onFinished() }
            override fun error(mediaPlayer: MediaPlayer) = ui { isPlaying = false; listener?.onError() }
            override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) = ui { listener?.onBuffering(newCache) }
            override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) = ui { timeMs = newTime; listener?.onTime(newTime) }
            override fun lengthChanged(mediaPlayer: MediaPlayer, newLength: Long) = ui { lengthMs = newLength }
            override fun mediaPlayerReady(mediaPlayer: MediaPlayer) = ui { tracksVersion++ }
        })
    }

    // VLC events arrive on a native thread: never call back into libVLC from there.
    private fun ui(block: () -> Unit) {
        scope.launch(Dispatchers.Main) { block() }
    }

    fun play(url: String, startMs: Long = 0, networkCachingMs: Int = 1500) {
        frame = null
        timeMs = 0
        lengthMs = 0
        val options = buildList {
            add(":http-user-agent=$userAgent")
            add(":network-caching=$networkCachingMs")
            add(":http-reconnect")
            if (startMs > 0) add(":start-time=${startMs / 1000.0}")
        }
        mp.media().play(url, *options.toTypedArray())
    }

    fun togglePause() = mp.controls().pause()
    fun pause() = mp.controls().setPause(true)
    fun resume() = mp.controls().setPause(false)
    fun seekTo(ms: Long) = mp.controls().setTime(ms.coerceAtLeast(0))
    fun stop() {
        mp.controls().stop()
        isPlaying = false
    }

    fun setVolume(v: Int) {
        mp.audio().setVolume(v)
    }

    fun audioTracks(): List<TrackItem> {
        val current = mp.audio().track()
        return mp.audio().trackDescriptions().filter { it.id() >= 0 }.map { TrackItem(it.id(), it.description(), it.id() == current) }
    }

    fun subtitleTracks(): List<TrackItem> {
        val current = mp.subpictures().track()
        return mp.subpictures().trackDescriptions().map {
            TrackItem(it.id(), if (it.id() < 0) "Disattivati" else it.description(), it.id() == current)
        }
    }

    fun setAudioTrack(id: Int) { mp.audio().setTrack(id); tracksVersion++ }
    fun setSubtitleTrack(id: Int) { mp.subpictures().setTrack(id); tracksVersion++ }

    fun release() {
        listener = null
        runCatching { mp.controls().stop() }
        mp.release()
    }
}

/** Draws the latest decoded frame, letterboxed. */
@Composable
fun VideoView(player: VideoPlayer, modifier: Modifier = Modifier) {
    Box(modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        player.frame?.let { Image(it, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.matchParentSize()) }
    }
}
