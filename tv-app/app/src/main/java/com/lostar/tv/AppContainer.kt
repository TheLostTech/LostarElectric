package com.lostar.tv

import android.content.Context
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.mediacodec.MediaCodecUtil
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.data.StreamHealth
import com.lostar.tv.data.UserStore
import com.lostar.tv.ui.player.LiveSession
import com.lostar.tv.ui.player.PlayRequest
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val req = chain.request()
            chain.proceed(
                if (req.header("User-Agent") == null) req.newBuilder().header("User-Agent", USER_AGENT).build() else req
            )
        }
        .build()

    val userStore = UserStore(context)
    val catalog = CatalogRepository(http, context.cacheDir, scope)

    /** Per-link reliability history used to pick and replace live streams. */
    val streamHealth = StreamHealth(File(context.filesDir, "stream_health.json"))

    val hevcSupported: Boolean by lazy {
        runCatching { MediaCodecUtil.getDecoderInfos(MimeTypes.VIDEO_H265, false, false).isNotEmpty() }.getOrDefault(false)
    }

    /** One live player for the whole app: preview and full screen share it, so switching is instant. */
    val liveSession: LiveSession by lazy {
        LiveSession(context.applicationContext, http, streamHealth, { hevcSupported }) { channel ->
            scope.launch { userStore.pushRecentChannel(channel.id) }
        }
    }

    /** Hand-off between a screen and the player route (keeps big channel lists out of nav arguments). */
    @Volatile
    var pendingPlayback: PlayRequest? = null

    companion object {
        const val USER_AGENT = "VLC/3.0.20 LibVLC/3.0.20"
    }
}
