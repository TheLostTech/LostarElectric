package com.lostar.tv.ui.setup

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Login
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lostar.tv.data.PlaylistSource
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.components.AccentButton
import com.lostar.tv.ui.components.Spinner
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.launch

private enum class Mode(val label: String) { XTREAM("Xtream Codes"), M3U("Link M3U") }

@Composable
fun SetupScreen() {
    val app = LocalApp.current
    val scope = rememberCoroutineScope()
    var mode by rememberSaveable { mutableStateOf(Mode.XTREAM) }
    var server by rememberSaveable { mutableStateOf("") }
    var user by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var m3u by rememberSaveable { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val firstField = remember { FocusRequester() }

    fun submit() {
        if (busy) return
        val source = when (mode) {
            Mode.XTREAM -> {
                if (server.isBlank() || user.isBlank() || pass.isBlank()) {
                    error = "Compila server, utente e password"
                    return
                }
                PlaylistSource.Xtream(PlaylistSource.normalizeServer(server), user.trim(), pass.trim())
            }
            Mode.M3U -> {
                if (m3u.isBlank()) {
                    error = "Inserisci il link della lista"
                    return
                }
                PlaylistSource.fromM3uUrl(m3u)
            }
        }
        busy = true
        error = null
        scope.launch {
            val result = app.catalog.test(source)
            busy = false
            result.fold(
                onSuccess = { app.userStore.setSource(source) },
                onFailure = { error = it.message ?: "Connessione non riuscita" },
            )
        }
    }

    Row(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Palette.Background, Palette.Surface, Palette.Background)))
            .padding(horizontal = 72.dp, vertical = 56.dp),
        horizontalArrangement = Arrangement.spacedBy(72.dp),
    ) {
        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(Palette.Accent, Palette.Text, Palette.Text).forEach {
                    Box(Modifier.size(width = 10.dp, height = 44.dp).clip(RoundedCornerShape(2.dp)).background(it))
                }
            }
            Spacer(Modifier.height(28.dp))
            Text("Benvenuto in Lostar TV", style = MaterialTheme.typography.displaySmall, color = Palette.Text)
            Spacer(Modifier.height(16.dp))
            Text(
                "Collega il tuo abbonamento: canali, film e serie verranno organizzati per categoria, " +
                    "con guida TV, preferiti e \"continua a guardare\".",
                style = MaterialTheme.typography.bodyLarge,
                color = Palette.TextMuted,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "Suggerimento: se il tuo fornitore ti ha dato un link che contiene get.php?username=…&password=…, " +
                    "incollalo in \"Link M3U\": verrà convertito automaticamente in Xtream Codes per avere locandine, trame e guida TV.",
                style = MaterialTheme.typography.bodyMedium,
                color = Palette.TextDim,
            )
        }

        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(Palette.Surface)
                .padding(36.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Mode.entries.forEach { m ->
                    Surface(
                        onClick = { mode = m; error = null },
                        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(20.dp)),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = if (m == mode) Palette.SurfaceFocus else Palette.SurfaceHigh,
                            contentColor = if (m == mode) Palette.Accent else Palette.TextMuted,
                            focusedContainerColor = Palette.Accent,
                            focusedContentColor = Palette.OnAccent,
                        ),
                    ) {
                        Text(m.label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
                    }
                }
            }
            Spacer(Modifier.height(28.dp))

            when (mode) {
                Mode.XTREAM -> {
                    TvTextField(server, { server = it }, "Server (es. http://dominio.com:8080)", Modifier.focusRequester(firstField), keyboardType = KeyboardType.Uri)
                    Spacer(Modifier.height(14.dp))
                    TvTextField(user, { user = it }, "Nome utente")
                    Spacer(Modifier.height(14.dp))
                    TvTextField(pass, { pass = it }, "Password", password = true, imeAction = ImeAction.Done, onDone = ::submit)
                }
                Mode.M3U -> {
                    TvTextField(
                        m3u, { m3u = it }, "Link della lista (http://…/get.php?… oppure …/lista.m3u)",
                        Modifier.focusRequester(firstField), keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done, onDone = ::submit,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AccentButton(
                    if (busy) "Verifica in corso…" else "Connetti",
                    onClick = ::submit,
                    icon = if (mode == Mode.M3U) Icons.Rounded.Link else Icons.Rounded.Login,
                )
                if (busy) {
                    Spacer(Modifier.width(20.dp))
                    Spinner(size = 28.dp)
                }
            }
            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = Palette.Live)
            }
        }
    }
}

@Composable
private fun TvTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    password: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (focused) Palette.Accent else Palette.TextMuted)
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(color = Palette.Text),
            cursorBrush = SolidColor(Palette.Accent),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (password) KeyboardType.Password else keyboardType,
                imeAction = imeAction,
                autoCorrectEnabled = false,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { onDone() },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused }
                .clip(RoundedCornerShape(10.dp))
                .background(if (focused) Palette.SurfaceFocus else Palette.SurfaceHigh)
                .border(BorderStroke(2.dp, if (focused) Palette.Accent else Palette.Divider), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}
