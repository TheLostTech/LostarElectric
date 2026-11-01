package com.lostar.tv.desktop.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun Spinner(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    val t = rememberInfiniteTransition(label = "spin")
    val angle by t.animateFloat(0f, 360f, infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart), label = "a")
    Canvas(modifier.size(size)) {
        val w = this.size.minDimension / 10
        drawArc(Palette.Divider, 0f, 360f, false, style = Stroke(w))
        drawArc(Palette.Accent, angle, 90f, false, style = Stroke(w, cap = StrokeCap.Round))
    }
}

@Composable
fun CenterMessage(title: String, message: String? = null, action: (@Composable () -> Unit)? = null) {
    Column(Modifier.fillMaxSize().padding(48.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = Palette.Text, textAlign = TextAlign.Center)
        if (message != null) {
            Spacer(Modifier.height(10.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = Palette.TextMuted, textAlign = TextAlign.Center, modifier = Modifier.width(560.dp))
        }
        if (action != null) {
            Spacer(Modifier.height(24.dp))
            action()
        }
    }
}

/** A clickable row/box with a hover highlight, like on the Mac version of Etticio. */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.hoverClick(
    selected: Boolean = false,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    hoverColor: Color = Palette.SurfaceHigh,
    selectedColor: Color = Palette.SurfaceFocus,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier = composedHover(selected, shape, hoverColor, selectedColor, onDoubleClick, onClick)

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.composedHover(
    selected: Boolean,
    shape: RoundedCornerShape,
    hoverColor: Color,
    selectedColor: Color,
    onDoubleClick: (() -> Unit)?,
    onClick: () -> Unit,
): Modifier = androidx.compose.ui.composed {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    this
        .clip(shape)
        .background(if (selected) selectedColor else if (hovered) hoverColor else Color.Transparent)
        .hoverable(source)
        .combinedClickable(interactionSource = source, indication = null, onDoubleClick = onDoubleClick, onClick = onClick)
}

@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    primary: Boolean = false,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (primary) Palette.Accent else Palette.SurfaceHigh)
            .border(BorderStroke(1.dp, if (primary) Palette.Accent else Palette.Divider), RoundedCornerShape(10.dp))
            .hoverClick(hoverColor = Color.White.copy(alpha = 0.06f), onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val color = if (primary) Palette.OnAccent else Palette.Text
        if (icon != null) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            if (text.isNotEmpty()) Spacer(Modifier.width(8.dp))
        }
        if (text.isNotEmpty()) Text(text, color = color, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Chip(text: String, selected: Boolean, icon: ImageVector? = null, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(22.dp))
            .border(BorderStroke(1.dp, if (selected) Palette.Accent else Palette.Divider), RoundedCornerShape(22.dp))
            .hoverClick(selected = selected, shape = RoundedCornerShape(22.dp), onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = if (selected) Palette.Accent else Palette.TextMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = if (selected) Palette.Text else Palette.TextMuted, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProgressLine(progress: Float, modifier: Modifier = Modifier, height: Dp = 4.dp) {
    Box(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(height)).background(Color.White.copy(alpha = 0.2f))) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0f, 1f)).background(Palette.Accent))
    }
}

@Composable
fun LiveBadge(modifier: Modifier = Modifier) {
    Text(
        "LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White,
        modifier = modifier.clip(RoundedCornerShape(4.dp)).background(Palette.Live).padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
fun ChannelLogo(logo: String?, name: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(8.dp)).background(Palette.SurfaceHigh), contentAlignment = Alignment.Center) {
        if (!logo.isNullOrBlank()) {
            AsyncImage(logo, null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(5.dp))
        } else {
            Text(name.take(3).uppercase(), color = Palette.TextMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun Poster(title: String, image: String?, onClick: () -> Unit, modifier: Modifier = Modifier, width: Dp = 160.dp, progress: Float? = null) {
    Column(modifier.width(width).hoverClick(shape = RoundedCornerShape(12.dp), onClick = onClick).padding(6.dp)) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(2f / 3f).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Palette.SurfaceFocus, Palette.Surface))),
            contentAlignment = Alignment.Center,
        ) {
            Text(title, color = Palette.TextMuted, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 3, modifier = Modifier.padding(10.dp))
            if (!image.isNullOrBlank()) AsyncImage(image, title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            if (progress != null && progress > 0f) ProgressLine(progress, Modifier.align(Alignment.BottomCenter).padding(8.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, color = Palette.Text, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun <T> Shelf(title: String, items: List<T>, key: (T) -> Any, content: @Composable (T) -> Unit) {
    if (items.isEmpty()) return
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, color = Palette.Text, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 32.dp))
        Spacer(Modifier.height(10.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 26.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(items, key = key) { content(it) }
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), color = Palette.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, modifier = modifier)
}

data class ListEntry(val id: String, val name: String, val count: Int)

@Composable
fun CategoryColumn(entries: List<ListEntry>, selectedId: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier, contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items(entries, key = { it.id }) { e ->
            Row(
                Modifier.fillMaxWidth().hoverClick(selected = e.id == selectedId) { onSelect(e.id) }.padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    e.name, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = if (e.id == selectedId) Palette.Text else Palette.TextMuted,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (e.id == selectedId) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (e.count > 0) Text(e.count.toString(), color = Palette.TextDim, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
