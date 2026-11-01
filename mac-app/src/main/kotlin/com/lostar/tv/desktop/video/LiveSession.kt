package com.lostar.tv.desktop.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lostar.tv.data.Channel
import com.lostar.tv.data.ChannelDeduper
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.StreamHealth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Same failover rules as the TV app: best link first; on error, no start within 12 s,
 * buffering over 8 s or a frozen picture for 10 s, move to the next link automatically.
 */
class LiveSession(
    private val scope: CoroutineScope,
    private val player: VideoPlayer,
    private val health: StreamHealth,
    private val onChannelStarted: (LiveChannel) -> Unit,
) : VideoPlayer.Listener {

    enum class Phase { IDLE, CONNECTING, PLAYING, SWITCHING, FAILED }

    var channels by mutableStateOf<List<LiveChannel>>(emptyList()); private set
    var listName by mutableStateOf(""); private set
    var index by mutableIntStateOf(-1); private set
    var ranked by mutableStateOf<List<Channel>>(emptyList()); private set
    var variantPos by mutableIntStateOf(0); private set
    var phase by mutableStateOf(Phase.IDLE); private set
    var manual by mutableStateOf(false); private set
    var notice by mutableStateOf<String?>(null); private set
    var noticeToken by mutableIntStateOf(0); private set

    val current: LiveChannel? get() = if (phase == Phase.IDLE) null else channels.getOrNull(index)
    val video: VideoPlayer get() = player

    private var watchdog: Job? = null
    private var retryJob: Job? = null
    private val tried = mutableSetOf<String>()
    private var round = 0
    private var startedAt = 0L
    private var readyAt = 0L
    private var lastTime = -1L
    private var lastProgressAt = 0L
    private var bufferingSince = 0L
    private var successRecorded = false

    init {
        player.listener = this
    }

    override fun onPlaying() {
        if (phase == Phase.IDLE) return
        val now = System.currentTimeMillis()
        if (readyAt == 0L) readyAt = now
        lastProgressAt = now
        if (phase == Phase.SWITCHING) showNotice("Sorgente ${variantPos + 1} di ${ranked.size} attiva")
        if (phase != Phase.FAILED) phase = Phase.PLAYING
    }

    override fun onBuffering(percent: Float) {
        if (percent < 100f) {
            if (bufferingSince == 0L) bufferingSince = System.currentTimeMillis()
        } else {
            bufferingSince = 0
        }
    }

    override fun onTime(ms: Long) {
        if (ms != lastTime) {
            lastTime = ms
            lastProgressAt = System.currentTimeMillis()
            bufferingSince = 0
        }
    }

    override fun onError() = fail("Link non raggiungibile")
    override fun onFinished() = fail("Lo stream si è interrotto")

    fun start(list: List<LiveChannel>, i: Int, name: String) {
        val target = list.getOrNull(i) ?: return
        val alreadyOn = current?.id == target.id && phase != Phase.FAILED
        channels = list
        listName = name
        index = i
        if (!alreadyOn) tune(target)
    }

    fun zapTo(i: Int) {
        if (i !in channels.indices) return
        if (i == index && phase != Phase.IDLE && phase != Phase.FAILED) return
        index = i
        tune(channels[i])
    }

    fun zap(delta: Int) {
        if (channels.size < 2) return
        zapTo((index + delta).mod(channels.size))
    }

    fun chooseVariant(pos: Int) {
        if (pos !in ranked.indices) return
        manual = true
        round = 0
        tried.clear()
        retryJob?.cancel()
        phase = Phase.CONNECTING
        playVariant(pos)
    }

    fun retry() {
        channels.getOrNull(index)?.let { tune(it) }
    }

    fun stop() {
        watchdog?.cancel()
        retryJob?.cancel()
        player.stop()
        phase = Phase.IDLE
        notice = null
        scope.launch(Dispatchers.IO) { health.saveIfNeeded() }
    }

    fun variantLabel(pos: Int): String {
        val v = ranked.getOrNull(pos) ?: return ""
        return "Sorgente ${pos + 1} · ${ChannelDeduper.qualityOf(v.name).label}"
    }

    private fun tune(ch: LiveChannel) {
        retryJob?.cancel()
        manual = false
        round = 0
        tried.clear()
        ranked = health.rank(ch.variants, hevcSupported = true) // VLC decodes HEVC on every Mac
        phase = Phase.CONNECTING
        notice = null
        onChannelStarted(ch)
        playVariant(0)
    }

    private fun playVariant(pos: Int) {
        val v = ranked.getOrNull(pos) ?: return
        variantPos = pos
        tried += v.id
        successRecorded = false
        startedAt = System.currentTimeMillis()
        readyAt = 0
        lastTime = -1
        lastProgressAt = startedAt
        bufferingSince = 0
        player.play(v.url)
        startWatchdog()
    }

    private fun fail(reason: String) {
        if (phase == Phase.IDLE || phase == Phase.FAILED) return
        val v = ranked.getOrNull(variantPos) ?: return
        health.recordFailure(v.id)
        scope.launch(Dispatchers.IO) { health.saveIfNeeded() }
        val next = ranked.indices.firstOrNull { ranked[it].id !in tried }
        when {
            next != null -> {
                phase = Phase.SWITCHING
                showNotice("$reason: passo alla sorgente ${next + 1} di ${ranked.size}")
                playVariant(next)
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
                    ranked = health.rank(ch.variants, hevcSupported = true)
                    playVariant(0)
                }
            }
            else -> {
                phase = Phase.FAILED
                watchdog?.cancel()
                player.stop()
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
                if (phase == Phase.FAILED || phase == Phase.IDLE) continue
                val now = System.currentTimeMillis()
                if (readyAt == 0L) {
                    if (now - startedAt > START_TIMEOUT_MS) fail("Il canale non parte")
                    continue
                }
                if (bufferingSince > 0 && now - bufferingSince > STALL_TIMEOUT_MS) {
                    fail("Il segnale si è bloccato")
                    continue
                }
                if (now - lastProgressAt > FREEZE_TIMEOUT_MS) {
                    fail("Immagine bloccata")
                    continue
                }
                if (!successRecorded && now - readyAt > STABLE_AFTER_MS) {
                    successRecorded = true
                    ranked.getOrNull(variantPos)?.let { health.recordSuccess(it.id, readyAt - startedAt) }
                    round = 0
                    tried.clear()
                    ranked.getOrNull(variantPos)?.let { tried += it.id }
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
