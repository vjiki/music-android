package com.music.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.music.android.data.repository.AuthRepository
import com.music.android.domain.player.MediaPlayerService
import com.music.android.ui.screen.HomeScreen
import com.music.android.ui.screen.ProfileScreen
import com.music.android.ui.screen.SamplesScreen
import com.music.android.ui.screen.SearchScreen
import com.music.android.ui.screen.SettingsScreen
import com.music.android.ui.screen.MusicViewScreen
import com.music.android.ui.theme.MusicAndroidTheme
import com.music.android.ui.viewmodel.AuthViewModel
import com.music.android.ui.viewmodel.SongManagerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: AuthRepository
    private lateinit var mediaPlayerService: MediaPlayerService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        authRepository = AuthRepository(this, com.music.android.data.api.RetrofitClient.apiService)
        mediaPlayerService = MediaPlayerService(this)
        
        setContent {
            MusicAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicApp(authRepository, mediaPlayerService)
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerService.release()
    }
}

@Composable
fun MusicApp(
    authRepository: AuthRepository,
    mediaPlayerService: MediaPlayerService
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }
    val songManagerViewModel: SongManagerViewModel = viewModel { 
        SongManagerViewModel(authRepository, mediaPlayerService) 
    }
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                authViewModel = authViewModel,
                songManagerViewModel = songManagerViewModel
            )
        }
        composable("samples") {
            SamplesScreen(
                navController = navController,
                songManagerViewModel = songManagerViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                songManagerViewModel = songManagerViewModel
            )
        }
        composable("search") {
            SearchScreen(
                navController = navController,
                songManagerViewModel = songManagerViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel,
                songManagerViewModel = songManagerViewModel
            )
        }
        composable("music") {
            MusicViewScreen(
                navController = navController,
                songManagerViewModel = songManagerViewModel
            )
        }
    }
}

