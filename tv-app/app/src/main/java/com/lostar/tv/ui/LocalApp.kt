package com.lostar.tv.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.lostar.tv.AppContainer

val LocalApp = staticCompositionLocalOf<AppContainer> { error("AppContainer not provided") }
