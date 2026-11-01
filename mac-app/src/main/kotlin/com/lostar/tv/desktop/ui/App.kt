package com.lostar.tv.desktop.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.Detail
import com.lostar.tv.desktop.Page
import com.lostar.tv.desktop.video.Vlc
import java.awt.Desktop
import java.net.URI

@Composable
fun App(state: AppState) {
    Box(Modifier.fillMaxSize().background(Palette.Background)) {
        if (!Vlc.available) {
            CenterMessage(
                "Serve VLC per riprodurre i canali",
                "LostTV usa il motore video di VLC, che legge tutti i formati delle liste IPTV. " +
                    "Scarica VLC gratis da videolan.org, mettilo nella cartella Applicazioni e riapri LostTV.",
            ) {
                PillButton("Scarica VLC", primary = true, onClick = { runCatching { Desktop.getDesktop().browse(URI("https://www.videolan.org/vlc/")) } })
            }
            return@Box
        }
        val data by state.store.data.collectAsState()
        val source = data.source
        if (source == null) {
            SetupScreen(state)
            return@Box
        }
        LaunchedEffect(source) { state.catalog.activate(source) }
        val catalogState by state.catalog.state.collectAsState()
        when (val st = catalogState) {
            CatalogRepository.State.Idle, CatalogRepository.State.Loading -> Column(
                Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spinner(size = 48.dp)
                Spacer(Modifier.height(16.dp))
                Text("Caricamento della tua lista…", color = Palette.TextMuted)
            }
            is CatalogRepository.State.Error -> CenterMessage("Impossibile caricare la lista", st.message) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillButton("Riprova", primary = true, onClick = { state.catalog.reload() })
                    PillButton("Cambia lista", onClick = {
                        state.catalog.deactivate()
                        state.store.setSource(null)
                    })
                }
            }
            is CatalogRepository.State.Ready -> Shell(state)
        }
    }
}

@Composable
private fun Shell(state: AppState) {
    val catalog = state.catalog.currentCatalog ?: return
    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            Sidebar(state, liveCount = catalog.live.channels.size, movieCount = catalog.movies.size, seriesCount = catalog.series.size)
            Column(Modifier.weight(1f).fillMaxHeight()) {
                TopBar(state)
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        state.query.isNotBlank() -> SearchScreen(state)
                        else -> when (state.page) {
                            Page.HOME -> HomeScreen(state)
                            Page.LIVE -> LiveScreen(state)
                            Page.MOVIES -> MoviesScreen(state)
                            Page.SERIES -> SeriesScreen(state)
                            Page.FAVORITES -> FavoritesScreen(state)
                            Page.SETTINGS -> SettingsScreen(state)
                        }
                    }
                    when (val d = state.detail) {
                        is Detail.OfMovie -> MovieDetail(state, d.id)
                        is Detail.OfSeries -> SeriesDetail(state, d.id)
                        null -> Unit
                    }
                }
            }
        }
        if (state.liveFullscreen) LiveFullscreen(state)
        state.vod?.let { VodPlayerScreen(state, it) }
    }
}

@Composable
private fun TopBar(state: AppState) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (state.query.isNotBlank()) "Risultati" else state.page.label,
            style = MaterialTheme.typography.headlineSmall, color = Palette.Text, fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Row(
            Modifier.width(420.dp).clip(RoundedCornerShape(12.dp)).background(Palette.Surface)
                .border(BorderStroke(1.dp, Palette.Divider), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Search, null, tint = Palette.TextMuted, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (state.query.isEmpty()) Text("Cerca canali, film e serie", color = Palette.TextDim)
                BasicTextField(
                    value = state.query,
                    onValueChange = { state.query = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Palette.Text),
                    cursorBrush = SolidColor(Palette.Accent),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (state.query.isNotEmpty()) {
                Icon(Icons.Rounded.Close, "Cancella", tint = Palette.TextMuted, modifier = Modifier.size(20.dp).hoverClick { state.query = "" })
            }
        }
    }
}

@Composable
private fun Sidebar(state: AppState, liveCount: Int, movieCount: Int, seriesCount: Int) {
    Column(
        Modifier.width(250.dp).fillMaxHeight().background(Palette.Sidebar).padding(horizontal = 14.dp, vertical = 22.dp),
    ) {
        Row(Modifier.padding(start = 10.dp, bottom = 28.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(Palette.Accent, Palette.Text, Palette.Text).forEach {
                    Box(Modifier.size(width = 6.dp, height = 26.dp).clip(RoundedCornerShape(1.dp)).background(it))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("LostTV", color = Palette.Text, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("PER MAC", color = Palette.TextDim, fontSize = 10.sp, letterSpacing = 3.sp)
            }
        }
        NavItem(state, Page.HOME, Icons.Rounded.Home, null)
        NavItem(state, Page.LIVE, Icons.Rounded.LiveTv, liveCount)
        NavItem(state, Page.MOVIES, Icons.Rounded.Movie, movieCount)
        NavItem(state, Page.SERIES, Icons.Rounded.VideoLibrary, seriesCount)
        Spacer(Modifier.height(22.dp))
        SectionLabel("La tua raccolta", Modifier.padding(start = 14.dp, bottom = 8.dp))
        NavItem(state, Page.FAVORITES, Icons.Rounded.Star, null)
        Spacer(Modifier.weight(1f))
        NavItem(state, Page.SETTINGS, Icons.Rounded.Settings, null)
    }
}

@Composable
private fun NavItem(state: AppState, page: Page, icon: ImageVector, count: Int?) {
    val selected = state.page == page && state.query.isBlank()
    Row(
        Modifier.fillMaxWidth().hoverClick(selected = selected) { state.open(page) }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = if (selected) Palette.Accent else Palette.TextMuted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Text(page.label, color = if (selected) Palette.Text else Palette.TextMuted, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (count != null && count > 0) Text("%,d".format(count).replace(',', '.'), color = Palette.TextDim, style = MaterialTheme.typography.labelMedium)
    }
}
