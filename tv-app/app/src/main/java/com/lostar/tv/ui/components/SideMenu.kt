package com.lostar.tv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lostar.tv.ui.theme.Palette

enum class Destination(val label: String, val icon: ImageVector) {
    SEARCH("Cerca", Icons.Rounded.Search),
    HOME("Home", Icons.Rounded.Home),
    LIVE("TV in diretta", Icons.Rounded.LiveTv),
    MOVIES("Film", Icons.Rounded.Movie),
    SERIES("Serie TV", Icons.Rounded.VideoLibrary),
    FAVORITES("Preferiti", Icons.Rounded.Favorite),
    SETTINGS("Impostazioni", Icons.Rounded.Settings),
}

val CollapsedMenuWidth = 88.dp
private val ExpandedMenuWidth = 280.dp

/**
 * Sky / Google TV style rail: icons only while browsing, expands with labels when it gets focus.
 * Focus always lands on the current section when entering from the content.
 */
@Composable
fun SideMenu(
    selected: Destination,
    onSelect: (Destination) -> Unit,
    requesters: Map<Destination, FocusRequester>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val width by animateDpAsState(if (expanded) ExpandedMenuWidth else CollapsedMenuWidth, label = "menuWidth")

    Box(
        modifier
            .fillMaxHeight()
            .width(width)
            .background(
                if (expanded) Brush.horizontalGradient(listOf(Palette.Background, Palette.Background.copy(alpha = 0.92f)))
                else Brush.horizontalGradient(listOf(Palette.Background.copy(alpha = 0.85f), Color.Transparent))
            )
            .onFocusChanged { expanded = it.hasFocus }
            .focusProperties { enter = { requesters[selected] ?: FocusRequester.Default } }
            .focusGroup()
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 14.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BrandMark(expanded)
            Spacer(Modifier.weight(1f))
            Destination.entries.forEach { dest ->
                MenuItem(
                    dest = dest,
                    selected = dest == selected,
                    expanded = expanded,
                    onClick = { onSelect(dest) },
                    modifier = Modifier.focusRequester(requesters.getValue(dest)),
                )
            }
            Spacer(Modifier.weight(1.3f))
        }
    }
}

@Composable
private fun BrandMark(expanded: Boolean) {
    Row(
        Modifier.padding(start = 12.dp).height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Three bus-bars: the Lostar mark.
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(Palette.Accent, Palette.Text, Palette.Text).forEach {
                Box(Modifier.size(width = 6.dp, height = 26.dp).clip(RoundedCornerShape(1.dp)).background(it))
            }
        }
        AnimatedVisibility(expanded, enter = fadeIn(), exit = fadeOut()) {
            Text(
                "Lostar TV",
                style = MaterialTheme.typography.titleLarge,
                color = Palette.Text,
                modifier = Modifier.padding(start = 14.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MenuItem(
    dest: Destination,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(26.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected && expanded) Palette.SurfaceHigh else Color.Transparent,
            contentColor = if (selected) Palette.Accent else Palette.TextMuted,
            focusedContainerColor = Palette.Accent,
            focusedContentColor = Palette.OnAccent,
        ),
    ) {
        Row(
            Modifier.fillMaxHeight().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(dest.icon, contentDescription = dest.label, modifier = Modifier.size(24.dp))
            if (expanded) {
                Text(
                    dest.label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
