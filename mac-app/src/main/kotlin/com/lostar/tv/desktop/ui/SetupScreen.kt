package com.lostar.tv.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.lostar.tv.data.PlaylistSource
import com.lostar.tv.desktop.AppState
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(state: AppState) {
    var xtream by remember { mutableStateOf(true) }
    var server by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit() {
        if (busy) return
        val source = if (xtream) {
            if (server.isBlank() || user.isBlank() || pass.isBlank()) { error = "Compila server, utente e password"; return }
            PlaylistSource.Xtream(PlaylistSource.normalizeServer(server), user.trim(), pass.trim())
        } else {
            if (link.isBlank()) { error = "Incolla il link della lista"; return }
            PlaylistSource.fromM3uUrl(link)
        }
        busy = true
        error = null
        state.scope.launch {
            val r = state.catalog.test(source)
            busy = false
            r.fold(onSuccess = { state.store.setSource(source) }, onFailure = { error = it.message ?: "Connessione non riuscita" })
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Palette.Accent, unfocusedBorderColor = Palette.Divider,
        focusedLabelColor = Palette.Accent, cursorColor = Palette.Accent,
        focusedTextColor = Palette.Text, unfocusedTextColor = Palette.Text,
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.width(560.dp).clip(RoundedCornerShape(20.dp)).background(Palette.Surface).padding(40.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf(Palette.Accent, Palette.Text, Palette.Text).forEach {
                        Box(Modifier.size(width = 8.dp, height = 34.dp).clip(RoundedCornerShape(2.dp)).background(it))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Text("Benvenuto in LostTV", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Palette.Text)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Collega il tuo abbonamento. I canali doppi vengono uniti e, se uno stream si blocca, LostTV passa da solo al link successivo.",
                color = Palette.TextMuted,
            )
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Chip("Xtream Codes", xtream) { xtream = true; error = null }
                Chip("Link M3U", !xtream) { xtream = false; error = null }
            }
            Spacer(Modifier.height(20.dp))
            if (xtream) {
                OutlinedTextField(server, { server = it }, label = { Text("Server (es. http://dominio.com:8080)") }, singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(user, { user = it }, label = { Text("Nome utente") }, singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    pass, { pass = it }, label = { Text("Password") }, singleLine = true, colors = fieldColors,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
                )
            } else {
                OutlinedTextField(link, { link = it }, label = { Text("Link della lista (…/get.php?username=… oppure …/lista.m3u)") }, singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("I link get.php vengono convertiti in Xtream: locandine, trame e guida TV.", color = Palette.TextDim, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                PillButton(if (busy) "Verifica in corso…" else "Connetti", primary = true, onClick = ::submit)
                if (busy) {
                    Spacer(Modifier.width(16.dp))
                    Spinner(size = 26.dp)
                }
            }
            error?.let {
                Spacer(Modifier.height(14.dp))
                Text(it, color = Palette.Live)
            }
            Spacer(Modifier.height(6.dp))
            Box(Modifier.padding(top = 10.dp)) {
                Text("LostTV non fornisce contenuti: riproduce solo la lista del tuo abbonamento.", color = Palette.TextDim, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
