package com.lostar.tv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.delay

/** A requester that grabs focus once its target is laid out (retries briefly for lazy lists). */
@Composable
fun rememberInitialFocus(key: Any? = Unit): FocusRequester {
    val requester = remember { FocusRequester() }
    LaunchedEffect(key) {
        repeat(10) {
            delay(50)
            if (runCatching { requester.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }
    return requester
}
