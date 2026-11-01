package com.lostar.tv.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.CollapsedMenuWidth
import com.lostar.tv.ui.components.Destination
import com.lostar.tv.ui.components.SideMenu
import com.lostar.tv.ui.favorites.FavoritesScreen
import com.lostar.tv.ui.home.HomeScreen
import com.lostar.tv.ui.live.LiveScreen
import com.lostar.tv.ui.search.SearchScreen
import com.lostar.tv.ui.settings.SettingsScreen
import com.lostar.tv.ui.vod.MoviesScreen
import com.lostar.tv.ui.vod.SeriesScreen

@Composable
fun MainShell(navigator: Navigator) {
    var destination by rememberSaveable { mutableStateOf(Destination.HOME) }
    val requesters = remember { Destination.entries.associateWith { FocusRequester() } }
    var menuFocused by remember { mutableStateOf(false) }

    // Back: content -> menu -> Home -> exit app (default handling).
    BackHandler(enabled = !menuFocused || destination != Destination.HOME) {
        if (!menuFocused) {
            runCatching { requesters.getValue(destination).requestFocus() }
        } else {
            destination = Destination.HOME
        }
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(start = CollapsedMenuWidth)
        ) {
            // Each section asks for initial focus on its first item when it appears,
            // so choosing a section in the menu jumps straight into its content.
            val open = navigator
            when (destination) {
                Destination.SEARCH -> SearchScreen(open)
                Destination.HOME -> HomeScreen(open)
                Destination.LIVE -> LiveScreen(open)
                Destination.MOVIES -> MoviesScreen(open)
                Destination.SERIES -> SeriesScreen(open)
                Destination.FAVORITES -> FavoritesScreen(open)
                Destination.SETTINGS -> SettingsScreen()
            }
        }
        SideMenu(
            selected = destination,
            onSelect = { destination = it },
            requesters = requesters,
            modifier = Modifier.onFocusChanged { menuFocused = it.hasFocus },
        )
    }

}
