package com.lostar.tv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.lostar.tv.ui.theme.Palette

private val CardShape = RoundedCornerShape(10.dp)

@Composable
private fun cardBorder() = CardDefaults.border(
    focusedBorder = Border(BorderStroke(3.dp, Palette.Accent), shape = CardShape),
)

/** Vertical 2:3 poster used for movies and series. */
@Composable
fun PosterCard(
    title: String,
    image: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 150.dp,
    progress: Float? = null,
    showTitle: Boolean = true,
    onFocused: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    rating: String? = null,
) {
    var focused by remember { mutableStateOf(false) }
    Column(modifier.width(width)) {
        Card(
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .onFocusChanged {
                    focused = it.isFocused
                    if (it.isFocused) onFocused()
                },
            shape = CardDefaults.shape(CardShape),
            scale = CardDefaults.scale(focusedScale = 1.08f),
            border = cardBorder(),
            colors = CardDefaults.colors(containerColor = Palette.SurfaceHigh),
        ) {
            Box(Modifier.fillMaxSize()) {
                Artwork(title, image, ContentScale.Crop, Modifier.fillMaxSize())
                if (rating != null) RatingBadge(rating, Modifier.align(Alignment.TopEnd).padding(6.dp))
                if (progress != null && progress > 0f) {
                    ProgressLine(progress, Modifier.align(Alignment.BottomCenter).padding(8.dp))
                }
            }
        }
        if (showTitle) {
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = if (focused) Palette.Text else Palette.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 16:9 tile. With [logo] = true the image is a channel logo drawn centred on a soft gradient;
 * otherwise it is a still that fills the card (continue watching, episodes).
 */
@Composable
fun LandscapeCard(
    title: String,
    image: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    subtitle: String? = null,
    logo: Boolean = false,
    progress: Float? = null,
    live: Boolean = false,
    onFocused: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    Column(modifier.width(width)) {
        Card(
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .onFocusChanged {
                    focused = it.isFocused
                    if (it.isFocused) onFocused()
                },
            shape = CardDefaults.shape(CardShape),
            scale = CardDefaults.scale(focusedScale = 1.06f),
            border = cardBorder(),
            colors = CardDefaults.colors(containerColor = Palette.SurfaceHigh),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        if (logo) Brush.linearGradient(listOf(Palette.SurfaceFocus, Palette.Surface))
                        else Brush.linearGradient(listOf(Palette.SurfaceHigh, Palette.SurfaceHigh))
                    )
            ) {
                Artwork(
                    title, image,
                    contentScale = if (logo) ContentScale.Fit else ContentScale.Crop,
                    imageModifier = if (logo) Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 22.dp) else Modifier.fillMaxSize(),
                )
                if (!logo) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.7f)))
                    )
                }
                if (live) LiveBadge(Modifier.align(Alignment.TopStart).padding(8.dp))
                if (progress != null && progress > 0f) {
                    ProgressLine(progress, Modifier.align(Alignment.BottomCenter).padding(10.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (focused) Palette.Text else Palette.TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Palette.TextDim,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** "★ 8,2" from TMDB, top-right on posters. */
@Composable
fun RatingBadge(rating: String, modifier: Modifier = Modifier) {
    Text(
        "★ $rating",
        style = MaterialTheme.typography.labelSmall,
        color = Palette.Accent,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/** Image with a title placeholder that stays visible only while the image is missing or failed. */
@Composable
fun Artwork(title: String, image: String?, contentScale: ContentScale, imageModifier: Modifier) {
    var loaded by remember(image) { mutableStateOf(false) }
    if (!loaded) ArtworkFallback(title)
    if (!image.isNullOrBlank()) {
        AsyncImage(
            model = image,
            contentDescription = title,
            contentScale = contentScale,
            modifier = imageModifier,
            onState = { loaded = it is AsyncImagePainter.State.Success },
        )
    }
}

@Composable
private fun ArtworkFallback(title: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Palette.SurfaceFocus, Palette.Surface)))
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = Palette.TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
