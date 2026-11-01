package com.lostar.tv.ui.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient
import java.util.Locale

object PlayerEngine {
    fun create(context: Context, http: OkHttpClient): ExoPlayer {
        val dataSource = OkHttpDataSource.Factory(http)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(15_000, 60_000, 1_500, 3_000)
            .build()
        val renderers = DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        return ExoPlayer.Builder(context, renderers)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSource))
            .setLoadControl(loadControl)
            .setAudioAttributes(
                AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(),
                true,
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .also {
                it.trackSelectionParameters = it.trackSelectionParameters.buildUpon()
                    .setPreferredAudioLanguage("it")
                    .build()
            }
    }

    fun ExoPlayer.load(url: String, forceHls: Boolean, startMs: Long = 0) {
        val item = MediaItem.Builder().setUri(url).apply {
            if (forceHls || url.substringBefore('?').endsWith(".m3u8", ignoreCase = true)) setMimeType(MimeTypes.APPLICATION_M3U8)
        }.build()
        if (startMs > 0) setMediaItem(item, startMs) else setMediaItem(item)
        prepare()
        playWhenReady = true
    }

    /** Container not recognised: links without extension are often HLS in disguise. */
    fun PlaybackException.looksLikeWrongFormat(): Boolean =
        errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED ||
            errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED ||
            errorCode == PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED

    fun PlaybackException.isNetwork(): Boolean = errorCode in 2000..2999

    fun PlaybackException.friendly(): String = when {
        errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Il server ha rifiutato lo stream (abbonamento scaduto o troppe connessioni?)"
        errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Connessione allo stream non riuscita"
        errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Lo stream non risponde"
        errorCode in 4000..4999 -> "Formato video non supportato da questo TV"
        looksLikeWrongFormat() -> "Formato dello stream non riconosciuto"
        else -> "Riproduzione non riuscita (${errorCodeName})"
    }
}

data class TrackOption(
    val label: String,
    val type: Int,
    val group: Tracks.Group?,
    val index: Int,
    val selected: Boolean,
)

object TrackOptions {
    fun of(player: ExoPlayer, type: Int): List<TrackOption> {
        val groups = player.currentTracks.groups.filter { it.type == type }
        val options = mutableListOf<TrackOption>()
        var n = 1
        groups.forEach { g ->
            for (i in 0 until g.length) {
                if (!g.isTrackSupported(i)) continue
                val f = g.getTrackFormat(i)
                val lang = f.language?.takeIf { it != "und" }?.let { code ->
                    Locale(code).getDisplayLanguage(Locale.ITALIAN).replaceFirstChar { it.titlecase(Locale.ITALIAN) }
                }
                val extra = if (type == C.TRACK_TYPE_AUDIO) when (f.channelCount) {
                    6 -> " · 5.1"
                    8 -> " · 7.1"
                    2 -> " · Stereo"
                    else -> ""
                } else ""
                val label = (f.label ?: lang ?: "Traccia $n") + extra
                options += TrackOption(label, type, g, i, g.isTrackSelected(i))
                n++
            }
        }
        if (type == C.TRACK_TYPE_TEXT) {
            val disabled = player.trackSelectionParameters.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT) || options.none { it.selected }
            options.add(0, TrackOption("Disattivati", type, null, -1, disabled))
        }
        return options
    }

    fun apply(player: ExoPlayer, option: TrackOption) {
        val builder = player.trackSelectionParameters.buildUpon()
        val group = option.group
        if (group == null) {
            builder.setTrackTypeDisabled(option.type, true)
        } else {
            builder.setTrackTypeDisabled(option.type, false)
                .setOverrideForType(TrackSelectionOverride(group.mediaTrackGroup, option.index))
        }
        player.trackSelectionParameters = builder.build()
    }
}
