package com.lostar.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.delay

data class CategoryEntry(val id: String, val name: String, val count: Int)

/**
 * Vertical category list (left column). Moving focus selects the category after a short pause,
 * so scrolling quickly through dozens of groups does not reload the grid at every step.
 */
@Composable
fun CategoryList(
    entries: List<CategoryEntry>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 300.dp,
) {
    val currentOnSelect by rememberUpdatedState(onSelect)
    var focusedId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(focusedId) {
        val id = focusedId ?: return@LaunchedEffect
        delay(280)
        if (id != selectedId) currentOnSelect(id)
    }
    val initialIndex = remember(entries) { entries.indexOfFirst { it.id == selectedId }.coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LazyColumn(
        state = listState,
        modifier = modifier.width(width).fillMaxHeight().focusRestorer(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(entries, key = { it.id }) { entry ->
            val selected = entry.id == selectedId
            Surface(
                onClick = { currentOnSelect(entry.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .onFocusChanged { if (it.isFocused) focusedId = entry.id },
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(10.dp)),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = if (selected) Palette.SurfaceHigh else Color.Transparent,
                    contentColor = if (selected) Palette.Accent else Palette.TextMuted,
                    focusedContainerColor = Palette.Text,
                    focusedContentColor = Palette.OnAccent,
                ),
            ) {
                Row(
                    Modifier.fillMaxHeight().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        entry.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (entry.count > 0) {
                        Text(
                            entry.count.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 12.dp),
                        )
                    }
                }
            }
        }
    }
}
