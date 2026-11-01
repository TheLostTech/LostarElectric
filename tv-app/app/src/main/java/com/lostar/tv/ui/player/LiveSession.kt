package com.lostar.tv.ui.player

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import com.lostar.tv.data.Channel
import com.lostar.tv.data.ChannelDeduper
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.StreamHealth
import com.lostar.tv.ui.player.PlayerEngine.load
import com.lostar.tv.ui.player.PlayerEngine.looksLikeWrongFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

/**
 * Live TV playback with automatic failover.
 *
 * Every merged channel has several links. The session starts the most promising one (quality +
 * past reliability) and keeps a watchdog on it: if the stream errors, never starts, buffers for
 * too long or the picture freezes, it records the failure and moves to the next link on its own.
 * When every link failed it waits a little and starts a new round, up to [MAX_ROUNDS].
 */
class LiveSession(
    private val context: Context,
    private val http: OkHttpClient,
    private val health: StreamHealth,
    private val hevcSupported: () -> Boolean,
    private val onChannelStarted: (LiveChannel) -> Unit,
) {
    enum class Phase { IDLE, CONNECTING, PLAYING, SWITCHING, FAILED }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var channels by mutableStateOf<List<LiveChannel>>(emptyList()); private set
    var listName by mutableStateOf(""); private set
    var index by mutableIntStateOf(-1); private set
    var ranked by mutableStateOf<List<Channel>>(emptyList()); private set
    var variantPos by mutableIntStateOf(0); private set
    var phase by mutableStateOf(Phase.IDLE); private set
    var manual by mutableStateOf(false); private set
    var notice by mutableStateOf<String?>(null); private set
    var noticeToken by mutableIntStateOf(0); private set
    var tracksVersion by mutableIntStateOf(0); private set

    val current: LiveChannel? get() = if (phase == Phase.IDLE) null else channels.getOrNull(index)
    val currentVariant: Channel? get() = ranked.getOrNull(variantPos)

    private var exo: ExoPlayer? = null
    private var attachCount = 0
    private var stopJob: Job? = null
    private var watchdog: Job? = null
    private var retryJob: Job? = null

    private val tried = mutableSetOf<String>()
    private var round = 0
    private var startedAt = 0L
    private var readyAt = 0L
    private var lastPosition = -1L
    private var lastProgressAt = 0L
    private var bufferingSince = 0L
    private var triedHls = false
    private var successRecorded = false
    private var suspended = false

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            val now = System.currentTimeMillis()
            when (state) {
                Player.STATE_READY -> {
                    if (readyAt == 0L) readyAt = now
                    lastProgressAt = now
                    bufferingSince = 0
                    if (phase == Phase.SWITCHING) showNotice("Sorgente ${variantPos + 1} di ${ranked.size} attiva")
                    if (phase != Phase.FAILED) phase = Phase.PLAYING
                }
                Player.STATE_BUFFERING -> if (bufferingSince == 0L) bufferingSince = now
                Player.STATE_ENDED -> fail("Lo stream si è interrotto")
                else -> Unit
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            when {
                error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                    exo?.seekToDefaultPosition()
                    exo?.prepare()
                }
                !triedHls && error.looksLikeWrongFormat() -> playVariant(variantPos, forceHls = true)
                else -> fail(
                    when (error.errorCode) {
                        PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Link rifiutato dal server"
                        in 4000..4999 -> "Formato non supportato"
                        else -> "Link non raggiungibile"
                    }
                )
            }
        }

        override fun onTracksChanged(tracks: Tracks) {
            tracksVersion++
        }
    }

    fun player(): ExoPlayer = exo ?: PlayerEngine.create(context, http).also {
        it.addListener(listener)
        exo = it
    }

    // ---- lifecycle -----------------------------------------------------------------------------

    /** Every visible video surface attaches; when none is left the stream is stopped (frees the connection). */
    fun attach() {
        attachCount++
        stopJob?.cancel()
    }

    fun detach() {
        attachCount = (attachCount - 1).coerceAtLeast(0)
        if (attachCount == 0) {
            stopJob?.cancel()
            stopJob = scope.launch {
                delay(400)
                if (attachCount == 0) stop()
            }
        }
    }

    /** App sent to background (Home button): drop the connection, resume on return. */
    fun pauseForBackground() {
        if (phase == Phase.IDLE) return
        suspended = true
        watchdog?.cancel()
        retryJob?.cancel()
        exo?.stop()
    }

    fun resumeFromBackground() {
        if (!suspended) return
        suspended = false
        if (phase != Phase.IDLE && channels.getOrNull(index) != null) {
            phase = Phase.CONNECTING
            playVariant(variantPos, forceHls = false)
        }
    }

    fun stop() {
        watchdog?.cancel()
        retryJob?.cancel()
        exo?.stop()
        exo?.clearMediaItems()
        phase = Phase.IDLE
        notice = null
        scope.launch(Dispatchers.IO) { health.saveIfNeeded() }
    }

    // ---- control -------------------------------------------------------------------------------

    fun start(list: List<LiveChannel>, i: Int, name: String) {
        val target = list.getOrNull(i) ?: return
        val alreadyOn = current?.id == target.id && phase != Phase.FAILED
        channels = list
        listName = name
        index = i
        if (!alreadyOn) tune(target)
    }

    fun zapTo(i: Int) {
        if (i !in channels.indices || (i == index && phase != Phase.IDLE && phase != Phase.FAILED)) return
        index = i
        tune(channels[i])
    }

    /** User picked a specific link from the panel. Failover stays active. */
    fun chooseVariant(pos: Int) {
        if (pos !in ranked.indices) return
        manual = true
        round = 0
        tried.clear()
        retryJob?.cancel()
        phase = Phase.CONNECTING
        playVariant(pos, forceHls = false)
    }

    fun retry() {
        val ch = channels.getOrNull(index) ?: return
        tune(ch)
    }

    fun variantLabel(pos: Int): String {
        val v = ranked.getOrNull(pos) ?: return ""
        val q = ChannelDeduper.qualityOf(v.name).label
        return "Sorgente ${pos + 1} · $q"
    }

    private fun tune(ch: LiveChannel) {
        retryJob?.cancel()
        manual = false
        round = 0
        tried.clear()
        ranked = health.rank(ch.variants, hevcSupported())
        phase = Phase.CONNECTING
        notice = null
        onChannelStarted(ch)
        playVariant(0, forceHls = false)
    }

    private fun playVariant(pos: Int, forceHls: Boolean) {
        val v = ranked.getOrNull(pos) ?: return
        variantPos = pos
        tried += v.id
        triedHls = forceHls
        successRecorded = false
        startedAt = System.currentTimeMillis()
        readyAt = 0
        lastPosition = -1
        lastProgressAt = startedAt
        bufferingSince = 0
        player().load(v.url, forceHls)
        startWatchdog()
    }

    private fun fail(reason: String) {
        if (phase == Phase.IDLE || phase == Phase.FAILED || suspended) return
        val v = currentVariant ?: return
        health.recordFailure(v.id)
        scope.launch(Dispatchers.IO) { health.saveIfNeeded() }

        val next = ranked.indices.firstOrNull { ranked[it].id !in tried }
        when {
            next != null -> {
                phase = Phase.SWITCHING
                showNotice("$reason: passo alla sorgente ${next + 1} di ${ranked.size}")
                playVariant(next, forceHls = false)
            }
            round < MAX_ROUNDS - 1 -> {
                round++
                tried.clear()
                phase = Phase.SWITCHING
                showNotice(if (ranked.size > 1) "$reason: riprovo tutte le sorgenti…" else "$reason: mi riconnetto…")
                watchdog?.cancel()
                retryJob?.cancel()
                retryJob = scope.launch {
                    delay(1500L * round)
                    val ch = channels.getOrNull(index) ?: return@launch
                    ranked = health.rank(ch.variants, hevcSupported())
                    playVariant(0, forceHls = false)
                }
            }
            else -> {
                phase = Phase.FAILED
                watchdog?.cancel()
                exo?.stop()
                notice = null
            }
        }
    }

    private fun showNotice(text: String) {
        notice = text
        noticeToken++
    }

    private fun startWatchdog() {
        watchdog?.cancel()
        watchdog = scope.launch {
            while (isActive) {
                delay(1000)
                val p = exo ?: continue
                if (suspended || phase == Phase.FAILED || phase == Phase.IDLE) continue
                val now = System.currentTimeMillis()
                if (readyAt == 0L) {
                    if (now - startedAt > START_TIMEOUT_MS) fail("Il canale non parte")
                    continue
                }
                if (p.playbackState == Player.STATE_BUFFERING) {
                    if (bufferingSince == 0L) bufferingSince = now
                    if (now - bufferingSince > STALL_TIMEOUT_MS) {
                        fail("Il segnale si è bloccato")
                        continue
                    }
                } else {
                    bufferingSince = 0
                }
                if (p.playbackState == Player.STATE_READY && p.playWhenReady) {
                    val pos = p.currentPosition
                    if (pos != lastPosition) {
                        lastPosition = pos
                        lastProgressAt = now
                    } else if (now - lastProgressAt > FREEZE_TIMEOUT_MS) {
                        fail("Immagine bloccata")
                        continue
                    }
                }
                if (!successRecorded && p.isPlaying && now - readyAt > STABLE_AFTER_MS) {
                    successRecorded = true
                    currentVariant?.let { health.recordSuccess(it.id, readyAt - startedAt) }
                    round = 0
                    tried.clear()
                    currentVariant?.let { tried += it.id }
                    scope.launch(Dispatchers.IO) { health.saveIfNeeded() }
                }
            }
        }
    }

    companion object {
        private const val START_TIMEOUT_MS = 12_000L
        private const val STALL_TIMEOUT_MS = 8_000L
        private const val FREEZE_TIMEOUT_MS = 10_000L
        private const val STABLE_AFTER_MS = 20_000L
        private const val MAX_ROUNDS = 3
    }
}
