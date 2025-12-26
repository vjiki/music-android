package com.music.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.music.android.data.model.Song
import com.music.android.ui.component.*
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
    
    var showSearch by remember { mutableStateOf(false) }
    var showPlaylists by remember { mutableStateOf(false) }
    var showMessages by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar with actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { 
                    currentSong?.let { songManagerViewModel.toggleDislike(it) }
                }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Dislike",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { 
                    currentSong?.let { songManagerViewModel.toggleLike(it) }
                }) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { showMessages = true }) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = "Messages",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { showSearch = true }) {
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
            
            // Stories section
            StoriesSection(
                authViewModel = authViewModel,
                songManagerViewModel = songManagerViewModel
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Discover cards
            DiscoverRow(
                librarySongs = librarySongs,
                songManagerViewModel = songManagerViewModel
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tags
            TagsSection()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick Play
            QuickPlaySection(
                songs = librarySongs,
                songManagerViewModel = songManagerViewModel
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Mixes
            MixesSection(
                songs = librarySongs,
                songManagerViewModel = songManagerViewModel
            )
        }
        
        // Mini player
        if (currentSong != null) {
            MiniPlayer(
                song = currentSong!!,
                isPlaying = isPlaying,
                songManagerViewModel = songManagerViewModel,
                onExpand = { /* Navigate to full player */ }
            )
        }
    }
    
    // Search sheet
    if (showSearch) {
        SearchScreen(
            onDismiss = { showSearch = false },
            songManagerViewModel = songManagerViewModel
        )
    }
    
    // Playlists sheet
    if (showPlaylists) {
        PlaylistsScreen(
            onDismiss = { showPlaylists = false },
            songManagerViewModel = songManagerViewModel
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.95f),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Samples") },
            label = { Text("Samples") },
            selected = false,
            onClick = { navController.navigate("samples") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
    }
}

