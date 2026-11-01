package com.lostar.tv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lostar.tv.BuildConfig
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.data.SourceState
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.components.AccentButton
import com.lostar.tv.ui.components.GhostButton
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val app = LocalApp.current
    val scope = rememberCoroutineScope()
    val source by app.userStore.source.collectAsState(initial = null)
    val account by app.catalog.account.collectAsState()
    val state by app.catalog.state.collectAsState()
    val catalog = (state as? CatalogRepository.State.Ready)?.catalog
    val refreshing = (state as? CatalogRepository.State.Ready)?.refreshing == true
    var confirmLogout by remember { mutableStateOf(false) }
    var historyCleared by remember { mutableStateOf(false) }
    val first = rememberInitialFocus()

    Column(Modifier.fillMaxSize().padding(horizontal = 56.dp, vertical = 48.dp)) {
        Text("Impostazioni", style = MaterialTheme.typography.headlineLarge, color = Palette.Text)
        Spacer(Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            InfoCard("Abbonamento", Modifier.weight(1f)) {
                Line("Sorgente", (source as? SourceState.Some)?.source?.label ?: "—")
                account?.let { a ->
                    a.status?.let { Line("Stato", if (it.equals("Active", true)) "Attivo" else it) }
                    Line("Scadenza", a.expiresAtMs?.let { Format.date(it) } ?: "Illimitato")
                    a.maxConnections?.let { Line("Connessioni", "${a.activeConnections ?: 0} attive su $it") }
                }
            }
            InfoCard("Contenuti", Modifier.weight(1f)) {
                Line("Canali", catalog?.live?.let { "${it.channels.size} (uniti da ${it.variantCount} link)" } ?: "—")
                Line("Film", catalog?.movies?.size?.toString() ?: "—")
                Line("Serie TV", catalog?.series?.size?.toString() ?: "—")
                Line("Stato", if (refreshing) "Aggiornamento in corso…" else "Aggiornato")
            }
        }

        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AccentButton(
                "Aggiorna lista",
                icon = Icons.Rounded.Refresh,
                modifier = Modifier.focusRequester(first),
                onClick = { app.catalog.reload() },
            )
            GhostButton(
                if (historyCleared) "Cronologia cancellata" else "Cancella cronologia",
                icon = Icons.Rounded.DeleteSweep,
                onClick = {
                    scope.launch {
                        app.userStore.clearHistory()
                        historyCleared = true
                    }
                },
            )
            GhostButton(
                if (confirmLogout) "Premi di nuovo per confermare" else "Cambia lista",
                icon = Icons.Rounded.Logout,
                onClick = {
                    if (!confirmLogout) {
                        confirmLogout = true
                    } else {
                        scope.launch {
                            app.catalog.deactivate()
                            app.userStore.setSource(null)
                        }
                    }
                },
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            "LostTV ${BuildConfig.VERSION_NAME} · Lettore per liste IPTV di tua proprietà. L'app non fornisce contenuti.",
            style = MaterialTheme.typography.bodySmall,
            color = Palette.TextDim,
        )
    }
}

@Composable
private fun InfoCard(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Palette.Surface)
            .padding(28.dp)
    ) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelMedium, color = Palette.Accent)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun Line(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Palette.TextMuted, modifier = Modifier.width(150.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Palette.Text)
    }
}
