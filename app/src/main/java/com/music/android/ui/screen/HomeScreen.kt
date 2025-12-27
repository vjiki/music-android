package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.music.android.ui.component.*
import com.music.android.ui.component.BrokenHeartIcon
import com.music.android.ui.component.BrokenHeartIconFilled
import com.music.android.ui.viewmodel.AuthViewModel
import com.music.android.ui.viewmodel.SongManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    songManagerViewModel: SongManagerViewModel
) {
    val librarySongs by songManagerViewModel.librarySongs.collectAsState()
    val currentSong by songManagerViewModel.currentSong.collectAsState()
    val isPlaying by songManagerViewModel.isPlaying.collectAsState()

    var showPlaylists by remember { mutableStateOf(false) }
    var showMessages by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentSong != null) {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        songManagerViewModel = songManagerViewModel,
                        onExpand = { navController.navigate("music") }
                    )
                }
                BottomNavigationBar(navController, currentRoute = "home")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                // Dislike button (broken heart)
                currentSong?.let { song ->
                    val dislikeIconSize = songManagerViewModel.dislikeIconSize(song)
                    IconButton(
                        onClick = { songManagerViewModel.toggleDislike(song) },
                        modifier = Modifier.size((dislikeIconSize + 8).dp)
                    ) {
                        if (song.isDisliked) {
                            BrokenHeartIconFilled(
                                tint = Color.Red,
                                size = dislikeIconSize.dp
                            )
                        } else {
                            BrokenHeartIcon(
                                tint = Color.White,
                                size = dislikeIconSize.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Like button
                currentSong?.let { song ->
                    val likeIconSize = songManagerViewModel.likeIconSize(song)
                    IconButton(
                        onClick = { songManagerViewModel.toggleLike(song) },
                        modifier = Modifier.size((likeIconSize + 8).dp)
                    ) {
                        Icon(
                            imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (song.isLiked) Color(0xFFFF1493) else Color.White,
                            modifier = Modifier.size(likeIconSize.dp)
                        )
                    }
                }

                IconButton(onClick = { showMessages = true }) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Messages",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { showPlaylists = true }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Playlists",
                        tint = Color.White
                    )
                }
            }

            StoriesSection(
                authViewModel = authViewModel,
                songManagerViewModel = songManagerViewModel
            )

            Spacer(modifier = Modifier.height(24.dp))

            val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
            DiscoverRow(
                librarySongs = librarySongs,
                songManagerViewModel = songManagerViewModel,
                isAuthenticated = isAuthenticated
            )

            Spacer(modifier = Modifier.height(24.dp))
            TagsSection()

            Spacer(modifier = Modifier.height(24.dp))
            QuickPlaySection(librarySongs, songManagerViewModel)

            Spacer(modifier = Modifier.height(24.dp))
            MixesSection(librarySongs, songManagerViewModel)

            if (currentSong != null) {
                Spacer(modifier = Modifier.height(74.dp))
            }
        }
    }

    // Search is now handled via navigation

    if (showPlaylists) {
        PlaylistsScreen(
            onDismiss = { showPlaylists = false },
            songManagerViewModel = songManagerViewModel
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String = "home") {
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.95f),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Samples") },
            label = { Text("Samples") },
            selected = currentRoute == "samples",
            onClick = { navController.navigate("samples") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}
