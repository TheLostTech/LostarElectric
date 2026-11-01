package com.lostar.tv.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lostar.tv.data.Catalog
import com.lostar.tv.data.LiveChannel
import com.lostar.tv.data.Movie
import com.lostar.tv.data.Series
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.LandscapeCard
import com.lostar.tv.ui.components.PosterCard
import com.lostar.tv.ui.components.Shelf
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.Normalizer

private data class Results(val channels: List<LiveChannel>, val movies: List<Movie>, val series: List<Series>) {
    val isEmpty get() = channels.isEmpty() && movies.isEmpty() && series.isEmpty()
}

@Composable
fun SearchScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    var query by rememberSaveable { mutableStateOf("") }
    var results by remember { mutableStateOf<Results?>(null) }
    var focused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val fieldFocus = rememberInitialFocus()

    LaunchedEffect(query) {
        if (query.trim().length < 2) {
            results = null
            return@LaunchedEffect
        }
        delay(300)
        results = withContext(Dispatchers.Default) { search(catalog, query) }
    }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 48.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        item("field") {
            Row(
                Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(if (focused) Palette.SurfaceFocus else Palette.SurfaceHigh)
                    .border(BorderStroke(2.dp, if (focused) Palette.Accent else Palette.Divider), RoundedCornerShape(30.dp))
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Search, null, tint = Palette.TextMuted, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(14.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Cerca canali, film e serie…", style = MaterialTheme.typography.titleMedium, color = Palette.TextDim)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = Palette.Text),
                        cursorBrush = SolidColor(Palette.Accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search, autoCorrectEnabled = false),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fieldFocus)
                            .onFocusChanged { focused = it.isFocused },
                    )
                }
            }
        }
        val r = results
        when {
            r == null -> item("hint") {
                Text(
                    "Scrivi almeno due lettere. Premi \"Cerca\" sulla tastiera e poi scendi per scegliere un risultato.",
                    style = MaterialTheme.typography.bodyLarge, color = Palette.TextDim, modifier = Modifier.padding(horizontal = 48.dp),
                )
            }
            r.isEmpty -> item("none") {
                Text("Nessun risultato per \"$query\"", style = MaterialTheme.typography.titleMedium, color = Palette.TextMuted, modifier = Modifier.padding(horizontal = 48.dp))
            }
            else -> {
                if (r.channels.isNotEmpty()) item("ch") {
                    Shelf("Canali", r.channels, key = { it.id }) { ch ->
                        LandscapeCard(
                            title = ch.name, image = ch.logo, logo = true, live = true, width = 220.dp,
                            onClick = { navigator.play(PlayRequest.Live(r.channels, r.channels.indexOf(ch), "Ricerca")) },
                        )
                    }
                }
                if (r.movies.isNotEmpty()) item("mv") {
                    Shelf("Film", r.movies, key = { it.id }) { m ->
                        PosterCard(Format.cleanTitle(m.name), m.poster, onClick = { navigator.openMovie(m.id) })
                    }
                }
                if (r.series.isNotEmpty()) item("sr") {
                    Shelf("Serie TV", r.series, key = { it.id }) { s ->
                        PosterCard(Format.cleanTitle(s.name), s.cover, onClick = { navigator.openSeries(s.id) })
                    }
                }
            }
        }
    }
}

private fun normalize(s: String): String =
    Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

private fun search(c: Catalog, query: String): Results {
    val terms = normalize(query).split(' ').filter { it.isNotBlank() }
    fun matches(name: String): Boolean {
        val n = normalize(name)
        return terms.all { it in n }
    }
    return Results(
        channels = c.live.channels.asSequence().filter { matches(it.name) || it.variants.any { v -> matches(v.name) } }.take(40).toList(),
        movies = c.movies.asSequence().filter { matches(it.name) }.take(40).toList(),
        series = c.series.asSequence().filter { matches(it.name) }.take(40).toList(),
    )
}
