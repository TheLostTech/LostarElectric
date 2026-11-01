package com.lostar.tv.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.data.GuideHit
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.desktop.AppState
import com.lostar.tv.desktop.Detail
import com.lostar.tv.desktop.DesktopStore
import com.lostar.tv.desktop.Page
import com.lostar.tv.desktop.VodRequest
import com.lostar.tv.desktop.toVodItem
import com.lostar.tv.ui.Format
import java.text.Normalizer

@Composable
private fun ChannelTile(state: AppState, ch: LiveChannel, list: List<LiveChannel>, listName: String) {
    Column(Modifier.width(200.dp).hoverClick {
        state.live.start(list, list.indexOf(ch), listName)
        state.liveFullscreen = true
    }.padding(6.dp)) {
        ChannelLogo(ch.logo, ch.name, Modifier.fillMaxWidth().aspectRatio(16f / 9f))
        Spacer(Modifier.height(8.dp))
        Text(ch.name, color = Palette.Text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun HomeScreen(state: AppState) {
    val c = state.catalog.currentCatalog ?: return
    val data by state.store.data.collectAsState()
    val cont = data.history.filter { it.isInProgress }.take(20)
    val favCh = remember(c, data.favorites) { c.live.channels.filter { DesktopStore.isLiveFavorite(it, data.favorites) } }
    val recentCh = remember(c, data.recentChannels) { data.recentChannels.mapNotNull { c.live.find(it) }.distinctBy { it.id } }
    val newMovies = remember(c) { c.movies.sortedByDescending { it.added }.take(30) }
    val newSeries = remember(c) { c.series.sortedByDescending { it.added }.take(30) }
    LazyColumn(contentPadding = PaddingValues(bottom = 40.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        item { Shelf("Continua a guardare", cont, key = { it.key }) { e -> Poster(e.title, e.image, progress = e.progress, onClick = { state.playVod(VodRequest(listOf(e.toVodItem()), 0, e.positionMs)) }) } }
        item { Shelf("I tuoi canali", favCh, key = { it.id }) { ch -> ChannelTile(state, ch, favCh, "Preferiti") } }
        item { Shelf("Visti di recente", recentCh, key = { it.id }) { ch -> ChannelTile(state, ch, recentCh, "Visti di recente") } }
        item { Shelf("Film aggiunti di recente", newMovies, key = { it.id }) { m -> MoviePoster(state, m, onClick = { state.detail = Detail.OfMovie(m.id) }) } }
        item { Shelf("Serie TV aggiornate", newSeries, key = { it.id }) { s -> SeriesPoster(state, s, onClick = { state.detail = Detail.OfSeries(s.id) }) } }
        c.live.groups.take(4).forEach { g ->
            item { val list = c.live.channelsOf(g).take(30); Shelf("In diretta · ${g.name}", list, key = { it.id }) { ch -> ChannelTile(state, ch, list, g.name) } }
        }
    }
}

private fun norm(s: String) = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

@Composable
fun SearchScreen(state: AppState) {
    val c = state.catalog.currentCatalog ?: return
    val terms = norm(state.query).split(' ').filter { it.isNotBlank() }
    fun match(n: String) = norm(n).let { x -> terms.all { it in x } }
    val guide by state.catalog.guide.collectAsState()
    val guideLoading by state.catalog.guideLoading.collectAsState()
    val onTv = remember(c, state.query, guide) { state.catalog.searchGuide(state.query) }
    val ch = remember(c, state.query) { c.live.channels.filter { x -> match(x.name) || x.variants.any { match(it.name) } }.take(40) }
    val mv = remember(c, state.query) { c.movies.asSequence().filter { match(it.name) }.take(40).toList() }
    val sr = remember(c, state.query) { c.series.asSequence().filter { match(it.name) }.take(40).toList() }
    if (onTv.isEmpty() && ch.isEmpty() && mv.isEmpty() && sr.isEmpty()) {
        CenterMessage(
            "Nessun risultato per \"${state.query}\"",
            if (guide == null && guideLoading) "La guida TV si sta ancora caricando: tra poco potrai cercare anche cosa va in onda." else null,
        )
        return
    }
    LazyColumn(contentPadding = PaddingValues(bottom = 40.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        if (onTv.isNotEmpty()) item { OnTvResults(state, onTv) }
        item { Shelf("Canali", ch, key = { it.id }) { x -> ChannelTile(state, x, ch, "Ricerca") } }
        item { Shelf("Film", mv, key = { it.id }) { m -> MoviePoster(state, m, onClick = { state.detail = Detail.OfMovie(m.id) }) } }
        item { Shelf("Serie TV", sr, key = { it.id }) { s -> SeriesPoster(state, s, onClick = { state.detail = Detail.OfSeries(s.id) }) } }
    }
}

/** "Matrix · Sky Cinema Uno · in onda ora" — programmes found in the TV guide. */
@Composable
private fun OnTvResults(state: AppState, hits: List<GuideHit>) {
    val now = System.currentTimeMillis()
    val channels = hits.map { it.channel }.distinctBy { it.id }
    Column(Modifier.padding(horizontal = 32.dp)) {
        Text("In TV: ora e più tardi", style = MaterialTheme.typography.titleLarge, color = Palette.Text, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        hits.take(15).forEach { h ->
            val onAir = h.onAir(now)
            Row(
                Modifier.fillMaxWidth().hoverClick {
                    state.live.start(channels, channels.indexOfFirst { it.id == h.channel.id }, "Ricerca")
                    state.liveFullscreen = true
                }.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChannelLogo(h.channel.logo, h.channel.name, Modifier.width(72.dp).height(44.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(h.program.title, color = Palette.Text, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        h.channel.name + "  ·  " + Format.time(h.program.startMs) + "–" + Format.time(h.program.endMs),
                        color = Palette.TextMuted, style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (onAir) {
                    LiveBadge()
                    Spacer(Modifier.width(10.dp))
                    ProgressLine(h.program.progressAt(now), Modifier.width(120.dp))
                } else {
                    Text("alle ${Format.time(h.program.startMs)}", color = Palette.Accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun FavoritesScreen(state: AppState) {
    val c = state.catalog.currentCatalog ?: return
    val data by state.store.data.collectAsState()
    val ch = remember(c, data.favorites) { c.live.channels.filter { DesktopStore.isLiveFavorite(it, data.favorites) } }
    val mv = remember(c, data.favorites) { c.movies.filter { DesktopStore.movieKey(it.id) in data.favorites } }
    val sr = remember(c, data.favorites) { c.series.filter { DesktopStore.seriesKey(it.id) in data.favorites } }
    if (ch.isEmpty() && mv.isEmpty() && sr.isEmpty()) {
        CenterMessage("Nessun preferito", "Usa la ★ accanto a canali, film e serie per ritrovarli qui.")
        return
    }
    LazyColumn(contentPadding = PaddingValues(bottom = 40.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        item { Shelf("Canali", ch, key = { it.id }) { x -> ChannelTile(state, x, ch, "Preferiti") } }
        item { Shelf("Film", mv, key = { it.id }) { m -> MoviePoster(state, m, onClick = { state.detail = Detail.OfMovie(m.id) }) } }
        item { Shelf("Serie TV", sr, key = { it.id }) { s -> SeriesPoster(state, s, onClick = { state.detail = Detail.OfSeries(s.id) }) } }
    }
}

@Composable
fun SettingsScreen(state: AppState) {
    val data by state.store.data.collectAsState()
    val account by state.catalog.account.collectAsState()
    val st by state.catalog.state.collectAsState()
    val c = (st as? CatalogRepository.State.Ready)?.catalog
    var confirm by remember { mutableStateOf(false) }
    val guide by state.catalog.guide.collectAsState()
    val guideLoading by state.catalog.guideLoading.collectAsState()
    var keyDraft by remember(data.tmdbKey) { mutableStateOf(data.tmdbKey) }
    Column(Modifier.fillMaxSize().padding(horizontal = 32.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            InfoCard("Abbonamento", Modifier.weight(1f)) {
                Line("Sorgente", data.source?.label ?: "—")
                account?.let { a ->
                    a.status?.let { Line("Stato", if (it.equals("Active", true)) "Attivo" else it) }
                    Line("Scadenza", a.expiresAtMs?.let { Format.date(it) } ?: "Illimitato")
                    a.maxConnections?.let { Line("Connessioni", "${a.activeConnections ?: 0} attive su $it") }
                }
            }
            InfoCard("Contenuti", Modifier.weight(1f)) {
                Line("Canali", c?.live?.let { "${it.channels.size} (uniti da ${it.variantCount} link)" } ?: "—")
                Line("Film", c?.movies?.size?.toString() ?: "—")
                Line("Serie TV", c?.series?.size?.toString() ?: "—")
                Line("Stato", if ((st as? CatalogRepository.State.Ready)?.refreshing == true) "Aggiornamento…" else "Aggiornato")
                Line("Guida TV", guide?.let { "${it.byChannel.size} canali · ${it.size} programmi" } ?: if (guideLoading) "Caricamento…" else "Non disponibile")
                Line("Locandine e voti", if (state.tmdb.enabled) "TMDB attivo" else "Chiave TMDB mancante")
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                keyDraft, { keyDraft = it }, singleLine = true,
                label = { Text("Chiave API TMDB (themoviedb.org → Impostazioni → API)") },
                modifier = Modifier.width(620.dp),
            )
            Spacer(Modifier.width(12.dp))
            PillButton("Salva", primary = true, onClick = { state.store.setTmdbKey(keyDraft) })
        }
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PillButton("Aggiorna lista", icon = Icons.Rounded.Refresh, primary = true, onClick = { state.catalog.reload() })
            PillButton("Cancella cronologia", icon = Icons.Rounded.DeleteSweep, onClick = { state.store.clearHistory() })
            PillButton(if (confirm) "Clicca di nuovo per confermare" else "Cambia lista", icon = Icons.Rounded.Logout, onClick = {
                if (!confirm) confirm = true else {
                    state.live.stop()
                    state.catalog.deactivate()
                    state.store.setSource(null)
                    state.open(Page.HOME)
                }
            })
        }
        Spacer(Modifier.weight(1f))
        Text("LostTV per Mac · riproduce solo la lista del tuo abbonamento, non fornisce contenuti.", color = Palette.TextDim, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun InfoCard(title: String, modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(16.dp)).background(Palette.Surface).padding(24.dp)) {
        SectionLabel(title)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun Line(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(label, color = Palette.TextMuted, modifier = Modifier.width(140.dp))
        Text(value, color = Palette.Text, fontWeight = FontWeight.Medium)
    }
}
