package com.lostar.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.lostar.tv.ui.AppRoot
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.theme.LostarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as LostarApp).container
        setContent {
            CompositionLocalProvider(LocalApp provides container) {
                LostarTheme { AppRoot() }
            }
        }
    }
}
