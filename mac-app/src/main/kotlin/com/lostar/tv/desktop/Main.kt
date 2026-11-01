package com.lostar.tv.desktop

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.lostar.tv.desktop.ui.App
import com.lostar.tv.desktop.ui.LostTheme

fun main() {
    System.setProperty("apple.awt.application.name", "LostTV")
    application {
        val state = remember { AppState() }
        val windowState = rememberWindowState(width = 1440.dp, height = 900.dp, position = WindowPosition(Alignment.Center))
        state.window = windowState
        Window(
            onCloseRequest = {
                state.shutdown()
                exitApplication()
            },
            state = windowState,
            title = "LostTV",
            onKeyEvent = { e -> state.keyHandler?.invoke(e) ?: false },
        ) {
            setSingletonImageLoaderFactory { context ->
                ImageLoader.Builder(context)
                    .components { add(OkHttpNetworkFetcherFactory(callFactory = { state.http })) }
                    .crossfade(true)
                    .build()
            }
            LostTheme { App(state) }
        }
    }
}
