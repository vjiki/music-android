package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.music.android.data.model.PlaylistKind
import com.music.android.ui.component.BrokenHeartIconFilled
import com.music.android.ui.viewmodel.SongManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun PlaylistsScreen(
    onDismiss: () -> Unit,
    songManagerViewModel: SongManagerViewModel
) {
    val librarySongs by songManagerViewModel.librarySongs.collectAsState()
    val likedSongs = librarySongs.filter { it.isLiked }
    val dislikedSongs = librarySongs.filter { it.isDisliked }
    
    val playlists = listOf(
        PlaylistCard(
            kind = PlaylistKind.LIKED,
            title = "Liked Songs",
            subtitle = "${likedSongs.size} songs",
            icon = Icons.Default.Favorite,
            gradient = listOf(Color(0xFF9932CC), Color(0xFFFF1493)),
            coverUrl = likedSongs.firstOrNull()?.cover
        ),
        PlaylistCard(
            kind = PlaylistKind.DISLIKED,
            title = "Disliked Songs",
            subtitle = "${dislikedSongs.size} songs",
            icon = Icons.Default.FavoriteBorder, // Will be replaced with broken heart in UI
            gradient = listOf(Color.Red, Color(0xFFFF8C00)),
            coverUrl = dislikedSongs.firstOrNull()?.cover
        )
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Playlists",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
                
                // Playlist grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistTile(
                            playlist = playlist,
                            onClick = {
                                // Navigate to playlist detail
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

data class PlaylistCard(
    val kind: PlaylistKind?,
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradient: List<Color>,
    val coverUrl: String?
)

@Composable
fun PlaylistTile(
    playlist: PlaylistCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Box {
            if (playlist.coverUrl != null && playlist.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = playlist.coverUrl,
                    contentDescription = playlist.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(playlist.gradient)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (playlist.kind == PlaylistKind.DISLIKED) {
                        BrokenHeartIconFilled(
                            tint = Color.White,
                            size = 16.dp
                        )
                    } else {
                        Icon(
                            imageVector = playlist.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = playlist.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = playlist.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

