package com.music.android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.music.android.data.model.Song
import com.music.android.ui.viewmodel.SongManagerViewModel

@Composable
fun StoriesSection(
    authViewModel: com.music.android.ui.viewmodel.AuthViewModel,
    songManagerViewModel: SongManagerViewModel
) {
    // Placeholder for stories - would fetch from API
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Your story (create new)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF1493), Color(0xFFFF8C00), Color(0xFF9932CC))
                            )
                        )
                        .padding(2.dp)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Your story",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Text(
                    text = "Your story",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DiscoverRow(
    librarySongs: List<Song>,
    songManagerViewModel: SongManagerViewModel,
    isAuthenticated: Boolean
) {
    // Get My Vibe song - most liked for authenticated, first for guest
    val myVibeSong = if (isAuthenticated && librarySongs.isNotEmpty()) {
        librarySongs.maxByOrNull { it.likesCount }
    } else {
        librarySongs.firstOrNull()
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DiscoverCard(
            title = "My Vibe",
            subtitle = myVibeSong?.title ?: "Breathe with me",
            icon = Icons.Default.PlayArrow,
            onClick = {
                myVibeSong?.let {
                    songManagerViewModel.playSong(it, librarySongs)
                } ?: librarySongs.firstOrNull()?.let {
                    songManagerViewModel.playSong(it, librarySongs)
                }
            },
            modifier = Modifier.weight(1f)
        )
        DiscoverCard(
            title = "For You",
            subtitle = "Tailored tracks",
            icon = Icons.Default.People,
            onClick = {
                librarySongs.firstOrNull()?.let {
                    songManagerViewModel.playSong(it, librarySongs)
                }
            },
            modifier = Modifier.weight(1f)
        )
        DiscoverCard(
            title = "Trends",
            subtitle = "What's hot now",
            icon = Icons.Default.Whatshot,
            onClick = {
                librarySongs.randomOrNull()?.let {
                    songManagerViewModel.playSong(it, librarySongs.shuffled())
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DiscoverCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.18f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun TagsSection() {
    val tags = listOf("Pop", "Rock", "Hip Hop", "Jazz", "Electronic", "Classical")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Genres & moods",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(tags) { tag ->
                Surface(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 18.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPlaySection(
    songs: List<Song>,
    songManagerViewModel: SongManagerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quick Play",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(onClick = {}) {
                Text(
                    text = "See all",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            items(songs) { song ->
                QuickPlayItem(
                    song = song,
                    onClick = { songManagerViewModel.playSong(song, songs) }
                )
            }
        }
    }
}

@Composable
fun QuickPlayItem(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = song.cover ?: "",
                contentDescription = song.title ?: "Song",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = song.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MixesSection(
    songs: List<Song>,
    songManagerViewModel: SongManagerViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mixes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = {
                songs.firstOrNull()?.let {
                    songManagerViewModel.playSong(it, songs)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            items(songs) { song ->
                MixItem(
                    song = song,
                    onClick = { songManagerViewModel.playSong(song, songs) }
                )
            }
        }
    }
}

@Composable
fun MixItem(song: Song, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = song.cover ?: "",
            contentDescription = song.title ?: "Song",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(26.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = song.title ?: "Unknown",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = song.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

