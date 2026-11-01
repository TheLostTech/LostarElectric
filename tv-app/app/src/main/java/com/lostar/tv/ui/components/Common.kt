package com.lostar.tv.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedButtonDefaults
import androidx.tv.material3.Text
import com.lostar.tv.ui.theme.Palette

@Composable
fun Spinner(modifier: Modifier = Modifier, size: Dp = 48.dp) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val angle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "angle",
    )
    Canvas(modifier.size(size)) {
        val stroke = size.toPx() / 10
        drawArc(Palette.Divider, 0f, 360f, false, style = Stroke(stroke))
        drawArc(Palette.Accent, angle, 90f, false, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun LoadingView(message: String? = null, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spinner()
        if (message != null) {
            Spacer(Modifier.height(20.dp))
            Text(message, style = MaterialTheme.typography.titleMedium, color = Palette.TextMuted)
        }
    }
}

@Composable
fun MessageView(
    title: String,
    message: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val focus = remember { FocusRequester() }
    Column(
        modifier.fillMaxSize().padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Palette.Text, textAlign = TextAlign.Center)
        if (message != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                message, style = MaterialTheme.typography.bodyLarge, color = Palette.TextMuted,
                textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 640.dp),
            )
        }
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccentButton(actionLabel, onClick = onAction, modifier = Modifier.focusRequester(focus))
                if (secondaryLabel != null && onSecondary != null) GhostButton(secondaryLabel, onClick = onSecondary)
            }
            LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
        }
    }
}

@Composable
fun AccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.colors(
            containerColor = Palette.SurfaceHigh,
            contentColor = Palette.Text,
            focusedContainerColor = Palette.Accent,
            focusedContentColor = Palette.OnAccent,
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = OutlinedButtonDefaults.colors(
            contentColor = Palette.Text,
            focusedContainerColor = Palette.Text,
            focusedContentColor = Palette.OnAccent,
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ProgressLine(progress: Float, modifier: Modifier = Modifier, height: Dp = 4.dp) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(Palette.Text.copy(alpha = 0.25f))
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(Palette.Accent)
        )
    }
}

@Composable
fun Pill(text: String, modifier: Modifier = Modifier, highlighted: Boolean = false) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = if (highlighted) Palette.OnAccent else Palette.Text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (highlighted) Palette.Accent else Palette.Text.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
fun LiveBadge(modifier: Modifier = Modifier) {
    Text(
        "LIVE",
        style = MaterialTheme.typography.labelSmall,
        color = Palette.Text,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Palette.Live)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/** "2024 · ★ 7.8 · 1 h 52 min" style metadata line. */
@Composable
fun MetaLine(parts: List<String?>, modifier: Modifier = Modifier) {
    val text = parts.filterNot { it.isNullOrBlank() }.joinToString("  ·  ")
    if (text.isNotEmpty()) {
        Text(text, style = MaterialTheme.typography.titleSmall, color = Palette.TextMuted, modifier = modifier, maxLines = 1)
    }
}
