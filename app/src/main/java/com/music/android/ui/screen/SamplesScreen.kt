package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.music.android.data.model.ShortModel
import com.music.android.data.api.RetrofitClient
import com.music.android.domain.player.SamplesPlayerService
import com.music.android.ui.viewmodel.SongManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun SamplesScreen(
    navController: NavController,
    songManagerViewModel: SongManagerViewModel
) {
    val context = LocalContext.current
    val samplesPlayer = remember { SamplesPlayerService(context) }
    
    var shorts by remember { mutableStateOf<List<ShortModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableStateOf(0) }
    var lastPlayedIndex by remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userId = songManagerViewModel.authRepository.currentUserId
                val response = RetrofitClient.apiService.getShorts(userId)
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
    
    // Play first short when loaded
    LaunchedEffect(shorts.size) {
        if (shorts.isNotEmpty() && lastPlayedIndex == -1) {
            currentIndex = 0
            scope.launch {
                samplesPlayer.loadShort(shorts[0])
                if (shorts[0].type != "SHORT_VIDEO") {
                    samplesPlayer.play()
                }
            }
            lastPlayedIndex = 0
        }
    }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            samplesPlayer.stop()
            samplesPlayer.release()
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
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { shorts.size }
            )
            
            // Track page changes
            LaunchedEffect(pagerState.currentPage) {
                val newIndex = pagerState.currentPage
                if (newIndex != currentIndex && newIndex >= 0 && newIndex < shorts.size) {
                    currentIndex = newIndex
                    if (newIndex != lastPlayedIndex) {
                        scope.launch {
                            samplesPlayer.loadShort(shorts[newIndex])
                            if (shorts[newIndex].type != "SHORT_VIDEO") {
                                samplesPlayer.play()
                            }
                        }
                        lastPlayedIndex = newIndex
                    }
                }
            }
            
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ShortCard(
                    short = shorts[page],
                    isActive = page == currentIndex,
                    samplesPlayer = samplesPlayer,
                    onLike = {
                        scope.launch {
                            try {
                                val userId = songManagerViewModel.authRepository.currentUserId
                                RetrofitClient.apiService.likeSong(
                                    shorts[page].id,
                                    com.music.android.data.api.SongLikeRequest(
                                        userId = userId,
                                        songId = shorts[page].id
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onDislike = {
                        scope.launch {
                            try {
                                val userId = songManagerViewModel.authRepository.currentUserId
                                RetrofitClient.apiService.dislikeSong(
                                    shorts[page].id,
                                    com.music.android.data.api.SongLikeRequest(
                                        userId = userId,
                                        songId = shorts[page].id
                                    )
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


@Composable
fun ShortCard(
    short: ShortModel,
    isActive: Boolean,
    samplesPlayer: SamplesPlayerService,
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    val isVideo = short.type == "SHORT_VIDEO"
    val isCurrentlyPlaying = isActive && samplesPlayer.currentShort?.id == short.id
    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Video player for SHORT_VIDEO type
        if (isVideo && isCurrentlyPlaying) {
            val player = samplesPlayer.getPlayer()
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Cover image for SONG type or when video is not playing
            AsyncImage(
                model = short.cover ?: "",
                contentDescription = short.title ?: "Short",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Dark overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.3f)
                    )
            )
        }
        
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
        
        // Tap area for play/pause (only for audio, not video)
        if (isCurrentlyPlaying && !isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Toggle play/pause
                        if (samplesPlayer.isPlaying) {
                            samplesPlayer.pause()
                        } else {
                            samplesPlayer.play()
                        }
                    }
            )
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

