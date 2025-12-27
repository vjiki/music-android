package com.music.android.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Broken heart icon - heart with a slash through it
 * This represents the dislike button, matching iOS "heart.slash" icon
 */
@Composable
fun BrokenHeartIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Heart outline
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Dislike",
            tint = tint,
            modifier = Modifier.size(size)
        )
        
        // Slash overlay (diagonal line)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = size.toPx() * 0.12f
            drawLine(
                color = tint,
                start = androidx.compose.ui.geometry.Offset(size.toPx() * 0.25f, size.toPx() * 0.25f),
                end = androidx.compose.ui.geometry.Offset(size.toPx() * 0.75f, size.toPx() * 0.75f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun BrokenHeartIconFilled(
    modifier: Modifier = Modifier,
    tint: Color = Color.Red,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Filled heart
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Disliked",
            tint = tint,
            modifier = Modifier.size(size)
        )
        
        // Slash overlay (diagonal line)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = size.toPx() * 0.12f
            drawLine(
                color = Color.White,
                start = androidx.compose.ui.geometry.Offset(size.toPx() * 0.25f, size.toPx() * 0.25f),
                end = androidx.compose.ui.geometry.Offset(size.toPx() * 0.75f, size.toPx() * 0.75f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

