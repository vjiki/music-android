package com.music.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.music.android.data.model.Song
import com.music.android.ui.viewmodel.SongManagerViewModel

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    songManagerViewModel: SongManagerViewModel,
    onExpand: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .clickable(onClick = onExpand)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = Color.Black.copy(alpha = 0.24f)
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // Cover image with circular progress (simplified - just rounded image)
            Box {
                AsyncImage(
                    model = song.cover ?: "",
                    contentDescription = song.title ?: "Song",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Circular progress indicator would go here
            }
            
            // Title and artist
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = song.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            
            // Play/Pause button - white circle with black icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { songManagerViewModel.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

