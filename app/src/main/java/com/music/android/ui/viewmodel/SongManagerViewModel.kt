package com.music.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.data.api.RetrofitClient
import com.music.android.data.model.Song
import com.music.android.data.repository.AuthRepository
import com.music.android.domain.player.MediaPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongManagerViewModel(
    val authRepository: AuthRepository,
    private val mediaPlayerService: MediaPlayerService
) : ViewModel() {
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
    
    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()
    
    private val _librarySongs = MutableStateFlow<List<Song>>(emptyList())
    val librarySongs: StateFlow<List<Song>> = _librarySongs.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _isShuffling = MutableStateFlow(false)
    val isShuffling: StateFlow<Boolean> = _isShuffling.asStateFlow()
    
    private var currentIndex: Int? = null
    private var shuffledPlaylist: List<Song> = emptyList()
    
    init {
        setupPlayerListener()
        loadSongs()
    }
    
    private fun setupPlayerListener() {
        mediaPlayerService.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            
            override fun onPositionDiscontinuity(
                oldPosition: androidx.media3.common.Player.PositionInfo,
                newPosition: androidx.media3.common.Player.PositionInfo,
                reason: Int
            ) {
                _currentPosition.value = mediaPlayerService.currentPosition
            }
        })
    }
    
    fun loadSongs() {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId
                val response = RetrofitClient.apiService.getSongs(userId)
                if (response.isSuccessful) {
                    _librarySongs.value = response.body() ?: emptyList()
                } else {
                    // If API fails, use empty list (iOS uses fallback songs, but we'll keep empty for now)
                    _librarySongs.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // On error, use empty list
                _librarySongs.value = emptyList()
            }
        }
    }
    
    fun playSong(song: Song, playlist: List<Song>? = null) {
        // Only play if song has audio URL
        if (song.audioUrl.isNullOrEmpty()) return
        
        val targetPlaylist = playlist ?: _playlist.value
        val finalPlaylist = if (targetPlaylist.isEmpty() || !targetPlaylist.contains(song)) {
            targetPlaylist + song
        } else {
            targetPlaylist
        }
        
        _playlist.value = finalPlaylist
        currentIndex = finalPlaylist.indexOf(song)
        _currentSong.value = song
        
        mediaPlayerService.loadSong(song)
        mediaPlayerService.play()
    }
    
    fun togglePlayPause() {
        if (_isPlaying.value) {
            mediaPlayerService.pause()
        } else {
            mediaPlayerService.play()
        }
    }
    
    fun playNext() {
        val currentPlaylist = if (_isShuffling.value) shuffledPlaylist else _playlist.value
        val index = currentIndex ?: return
        
        if (index < currentPlaylist.size - 1) {
            playSong(currentPlaylist[index + 1], currentPlaylist)
            currentIndex = index + 1
        }
    }
    
    fun playPrevious() {
        val currentPlaylist = if (_isShuffling.value) shuffledPlaylist else _playlist.value
        val index = currentIndex ?: return
        
        if (index > 0) {
            playSong(currentPlaylist[index - 1], currentPlaylist)
            currentIndex = index - 1
        }
    }
    
    fun seekTo(position: Long) {
        mediaPlayerService.seekTo(position)
    }
    
    fun toggleShuffle() {
        _isShuffling.value = !_isShuffling.value
        if (_isShuffling.value) {
            shuffledPlaylist = _playlist.value.shuffled()
        }
    }
    
    fun toggleLike(song: Song) {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId
                RetrofitClient.apiService.likeSong(
                    song.id,
                    com.music.android.data.api.SongLikeRequest(userId = userId, songId = song.id)
                )
                loadSongs() // Refresh to get updated counts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun toggleDislike(song: Song) {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId
                RetrofitClient.apiService.dislikeSong(
                    song.id,
                    com.music.android.data.api.SongLikeRequest(userId = userId, songId = song.id)
                )
                loadSongs() // Refresh to get updated counts
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    val likedSongs: List<Song>
        get() = _librarySongs.value.filter { it.isLiked }
    
    val dislikedSongs: List<Song>
        get() = _librarySongs.value.filter { it.isDisliked }
    
    override fun onCleared() {
        super.onCleared()
        mediaPlayerService.release()
    }
}

