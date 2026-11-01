package com.lostar.tv.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.lostar.tv.ui.theme.Palette

/** Full-bleed artwork faded into the background, as on Apple TV / Sky detail pages. */
@Composable
fun DetailBackdrop(image: String?) {
    Box(Modifier.fillMaxSize()) {
        if (!image.isNullOrBlank()) {
            AsyncImage(
                model = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.align(Alignment.TopEnd).fillMaxWidth(0.75f).fillMaxHeight(),
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    0f to Palette.Background,
                    0.35f to Palette.Background.copy(alpha = 0.92f),
                    0.65f to Palette.Background.copy(alpha = 0.35f),
                    1f to Color.Transparent,
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.5f to Color.Transparent, 1f to Palette.Background)
            )
        )
    }
}
