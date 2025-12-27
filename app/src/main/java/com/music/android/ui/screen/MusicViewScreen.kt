package com.music.android.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.music.android.data.model.Song
import com.music.android.ui.component.BrokenHeartIcon
import com.music.android.ui.component.BrokenHeartIconFilled
import com.music.android.ui.viewmodel.SongManagerViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicViewScreen(
    navController: NavController,
    songManagerViewModel: SongManagerViewModel
) {
    val currentSong by songManagerViewModel.currentSong.collectAsState()
    val isPlaying by songManagerViewModel.isPlaying.collectAsState()
    val currentPosition by songManagerViewModel.currentPosition.collectAsState()
    val duration by songManagerViewModel.duration.collectAsState()
    val isShuffling by songManagerViewModel.isShuffling.collectAsState()
    
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    if (currentSong == null) {
        navController.popBackStack()
        return
    }
    
    val song = currentSong!!
    
    // Format time
    val formattedCurrentTime = formatTime(currentPosition)
    val formattedDuration = formatTime(duration)
    
    // Calculate progress (0f to 1f)
    val progress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2196F3), // Blue
                            Color.Transparent
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = offsetY.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetY > size.height * 0.4f) {
                                navController.popBackStack()
                            } else {
                                offsetY = 0f
                            }
                            isDragging = false
                        }
                    ) { change, dragAmount ->
                        if (!isDragging) {
                            isDragging = true
                        }
                        // Only allow downward drag from lower area
                        if (change.position.y > 100 && dragAmount.y > 0) {
                            offsetY = (offsetY + dragAmount.y).coerceAtLeast(0f)
                        }
                    }
                }
                .padding(horizontal = 25.dp)
                .padding(top = 80.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                
                // Playlist info (placeholder)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Playlist from album",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Top Hits",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                
                // Menu button
                IconButton(onClick = { /* Menu action */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Album cover
            AsyncImage(
                model = song.cover ?: "",
                contentDescription = song.title ?: "Album cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Player controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title and Artist
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = song.title ?: "Unknown",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 2
                    )
                    
                    Text(
                        text = song.artist ?: "Unknown Artist",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
                
                // Progress slider
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Slider(
                        value = progress,
                        onValueChange = { newValue ->
                            val newPosition = (newValue * duration).toLong()
                            songManagerViewModel.seekTo(newPosition)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    
                    // Time indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedCurrentTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = formattedDuration,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Control buttons
                ControlButtonsRow(
                    song = song,
                    isPlaying = isPlaying,
                    isShuffling = isShuffling,
                    songManagerViewModel = songManagerViewModel
                )
            }
        }
    }
}

@Composable
fun ControlButtonsRow(
    song: Song,
    isPlaying: Boolean,
    isShuffling: Boolean,
    songManagerViewModel: SongManagerViewModel
) {
    val likeIconSize = songManagerViewModel.likeIconSize(song)
    val dislikeIconSize = songManagerViewModel.dislikeIconSize(song)
    
    val likeScale by animateFloatAsState(
        targetValue = likeIconSize / 20f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "likeScale"
    )
    val dislikeScale by animateFloatAsState(
        targetValue = dislikeIconSize / 20f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "dislikeScale"
    )
    
    // Try horizontal layout first, fallback to vertical if needed
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dislike button
        IconButton(
            onClick = { songManagerViewModel.toggleDislike(song) },
            modifier = Modifier.size(40.dp)
        ) {
            if (song.isDisliked) {
                BrokenHeartIconFilled(
                    tint = Color.Red,
                    size = (20.dp * dislikeScale)
                )
            } else {
                BrokenHeartIcon(
                    tint = Color.White.copy(alpha = 0.7f),
                    size = (20.dp * dislikeScale)
                )
            }
        }
        
        // Shuffle button
        IconButton(
            onClick = { songManagerViewModel.toggleShuffle() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffling) Color.White else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Previous button
        IconButton(
            onClick = { songManagerViewModel.playPrevious() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Play/Pause button - large white circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, CircleShape)
                .clickable { songManagerViewModel.togglePlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
        
        // Next button
        IconButton(
            onClick = { songManagerViewModel.playNext() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Repeat button (placeholder - would need repeat mode state)
        IconButton(
            onClick = { /* Repeat action */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = "Repeat",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Like button
        IconButton(
            onClick = { songManagerViewModel.toggleLike(song) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like",
                tint = if (song.isLiked) Color(0xFFFF1493) else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size((20.dp * likeScale))
            )
        }
    }
}

fun formatTime(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

