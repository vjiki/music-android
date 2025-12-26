package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.music.android.data.model.ShortModel
import com.music.android.data.api.RetrofitClient
import com.music.android.ui.viewmodel.SongManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun SamplesScreen(
    navController: NavController,
    songManagerViewModel: SongManagerViewModel
) {
    var shorts by remember { mutableStateOf<List<ShortModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Get userId from auth repository - need to pass it through
                val response = RetrofitClient.apiService.getShorts("3762deba-87a9-482e-b716-2111232148ca")
                if (response.isSuccessful) {
                    shorts = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else if (shorts.isEmpty()) {
            Text(
                text = "No shorts available",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(shorts.size) { index ->
                    ShortCard(
                        short = shorts[index],
                        isActive = index == currentIndex,
                        onLike = {
                            scope.launch {
                                try {
                                    RetrofitClient.apiService.likeSong(
                                        shorts[index].id,
                                        com.music.android.data.api.LikeRequest("3762deba-87a9-482e-b716-2111232148ca")
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        onDislike = {
                            scope.launch {
                                try {
                                    RetrofitClient.apiService.dislikeSong(
                                        shorts[index].id,
                                        com.music.android.data.api.LikeRequest("3762deba-87a9-482e-b716-2111232148ca")
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShortCard(
    short: ShortModel,
    isActive: Boolean,
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillParentMaxHeight()
    ) {
        // Cover/Video
        AsyncImage(
            model = short.cover ?: "",
            contentDescription = short.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Bottom info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 100.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                short.cover?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    short.title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    short.artist?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
        
        // Right side buttons
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Like button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (short.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (short.isLiked) Color(0xFFFF1493) else Color.White
                    )
                }
                Text(
                    text = "${short.likesCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            
            // Dislike button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDislike,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (short.isDisliked) Icons.Default.Block else Icons.Default.ThumbDown,
                        contentDescription = "Dislike",
                        tint = if (short.isDisliked) Color.Red else Color.White
                    )
                }
                Text(
                    text = "${short.dislikesCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            
            // Comment button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* Comment action */ },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = Color.White
                    )
                }
                Text(
                    text = "0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

