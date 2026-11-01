package com.lostar.tv.ui.vod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.lostar.tv.data.Category
import com.lostar.tv.data.UserStore
import com.lostar.tv.ui.Format
import com.lostar.tv.ui.LocalApp
import com.lostar.tv.ui.Navigator
import com.lostar.tv.ui.components.CategoryEntry
import com.lostar.tv.ui.components.CategoryList
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.components.PosterCard
import com.lostar.tv.ui.components.rememberInitialFocus
import com.lostar.tv.ui.theme.Palette

private const val RECENT = "@recent"
private const val FAVORITES = "@fav"
private const val TOP = "@top"

/** One poster in the grid, independent of whether it is a movie or a series. */
private data class GridItem(val id: String, val title: String, val image: String?, val added: Long, val rating: Double?, val categoryId: String)

@Composable
fun MoviesScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val items = remember(catalog) {
        catalog.movies.map { GridItem(it.id, Format.cleanTitle(it.name), it.poster, it.added, it.rating, it.categoryId) }
    }
    PosterBrowser(
        emptyTitle = "Nessun film nella tua lista",
        items = items,
        categories = catalog.nonEmptyMovies(),
        favoriteKey = { UserStore.movieKey(it) },
        onOpen = { navigator.openMovie(it) },
    )
}

@Composable
fun SeriesScreen(navigator: Navigator) {
    val app = LocalApp.current
    val catalog = app.catalog.currentCatalog ?: return
    val items = remember(catalog) {
        catalog.series.map { GridItem(it.id, Format.cleanTitle(it.name), it.cover, it.added, it.rating, it.categoryId) }
    }
    PosterBrowser(
        emptyTitle = "Nessuna serie nella tua lista",
        items = items,
        categories = catalog.nonEmptySeries(),
        favoriteKey = { UserStore.seriesKey(it) },
        onOpen = { navigator.openSeries(it) },
    )
}

@Composable
private fun PosterBrowser(
    emptyTitle: String,
    items: List<GridItem>,
    categories: List<Category>,
    favoriteKey: (String) -> String,
    onOpen: (String) -> Unit,
) {
    val app = LocalApp.current
    val favorites by app.userStore.favorites.collectAsState(initial = emptySet())
    if (items.isEmpty()) {
        MessageView(emptyTitle)
        return
    }

    val byCategory = remember(items) { items.groupBy { it.categoryId } }
    val recent = remember(items) { items.sortedByDescending { it.added }.take(120) }
    val top = remember(items) { items.filter { (it.rating ?: 0.0) in 7.0..10.0 }.sortedByDescending { it.rating }.take(120) }
    val favItems = remember(items, favorites) { items.filter { favoriteKey(it.id) in favorites } }

    val entries = remember(categories, favItems.size, top.size) {
        buildList {
            add(CategoryEntry(RECENT, "Aggiunti di recente", 0))
            if (favItems.isNotEmpty()) add(CategoryEntry(FAVORITES, "★  Preferiti", favItems.size))
            if (top.size >= 6) add(CategoryEntry(TOP, "I più votati", 0))
            categories.forEach { add(CategoryEntry(it.id, it.name, byCategory[it.id].orEmpty().size)) }
        }
    }
    var selectedId by rememberSaveable { mutableStateOf(RECENT) }
    val effective = selectedId.takeIf { id -> entries.any { it.id == id } } ?: RECENT
    val shown = remember(effective, favItems, byCategory) {
        when (effective) {
            RECENT -> recent
            FAVORITES -> favItems
            TOP -> top
            else -> byCategory[effective].orEmpty()
        }
    }

    Row(Modifier.fillMaxSize()) {
        CategoryList(entries, effective, onSelect = { selectedId = it }, modifier = Modifier.padding(start = 16.dp))

        val firstFocus = rememberInitialFocus()
        val gridState = rememberLazyGridState()
        LaunchedEffect(effective) { gridState.scrollToItem(0) }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Text(
                entries.firstOrNull { it.id == effective }?.name?.removePrefix("★  ").orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                color = Palette.Text,
                modifier = Modifier.padding(start = 36.dp, top = 32.dp, bottom = 4.dp),
            )
            Text(
                "${shown.size} titoli",
                style = MaterialTheme.typography.bodyMedium,
                color = Palette.TextDim,
                modifier = Modifier.padding(start = 36.dp, bottom = 8.dp),
            )
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(158.dp),
                modifier = Modifier.fillMaxSize().focusRestorer(),
                contentPadding = PaddingValues(start = 36.dp, end = 40.dp, top = 16.dp, bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalArrangement = Arrangement.spacedBy(26.dp),
            ) {
                itemsIndexed(shown, key = { _, it -> it.id }) { index, item ->
                    PosterCard(
                        title = item.title,
                        image = item.image,
                        width = 158.dp,
                        modifier = if (index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                        onClick = { onOpen(item.id) },
                    )
                }
            }
        }
    }
}
