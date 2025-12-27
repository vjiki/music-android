package com.music.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.data.api.RetrofitClient
import com.music.android.data.model.Song
import com.music.android.data.repository.AuthRepository
import com.music.android.domain.player.MediaPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
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
    
    // Reference to samples player service for mutual exclusivity
    private var samplesPlayerService: com.music.android.domain.player.SamplesPlayerService? = null
    
    fun setSamplesPlayerService(service: com.music.android.domain.player.SamplesPlayerService?) {
        samplesPlayerService = service
    }
    
    init {
        setupPlayerListener()
        loadSongs()
    }
    
    private var positionUpdateJob: Job? = null
    
    private fun setupPlayerListener() {
        mediaPlayerService.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
                updatePositionPeriodically(isPlaying)
            }
            
            override fun onPositionDiscontinuity(
                oldPosition: androidx.media3.common.Player.PositionInfo,
                newPosition: androidx.media3.common.Player.PositionInfo,
                reason: Int
            ) {
                _currentPosition.value = mediaPlayerService.currentPosition
                _duration.value = mediaPlayerService.duration
            }
            
            override fun onPlaybackStateChanged(state: Int) {
                if (state == androidx.media3.common.Player.STATE_READY) {
                    _duration.value = mediaPlayerService.duration
                }
            }
        })
        
        // Start position updates if already playing
        updatePositionPeriodically(_isPlaying.value)
    }
    
    private fun updatePositionPeriodically(isPlaying: Boolean) {
        positionUpdateJob?.cancel()
        if (isPlaying) {
            positionUpdateJob = viewModelScope.launch {
                while (isActive && _isPlaying.value) {
                    _currentPosition.value = mediaPlayerService.currentPosition
                    delay(200) // Update every 200ms
                }
            }
        }
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
        
        // Pause samples player if it's playing
        samplesPlayerService?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        
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
            // Pause samples player if it's playing
            samplesPlayerService?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
            mediaPlayerService.play()
        }
    }
    
    fun pause() {
        if (_isPlaying.value) {
            mediaPlayerService.pause()
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
    
    // Calculate icon size based on like/dislike count (matching iOS behavior)
    fun iconSize(count: Long, baseSize: Float = 24f): Float {
        return when {
            count >= 50 -> baseSize * 1.4f  // Max size at 50+
            count >= 30 -> baseSize * 1.3f  // Bigger at 30+
            count >= 20 -> baseSize * 1.2f  // Bigger at 20+
            count >= 10 -> baseSize * 1.1f  // Slightly bigger at 10+
            else -> baseSize  // Default size
        }
    }
    
    fun likeIconSize(song: Song): Float = iconSize(song.likesCount)
    fun dislikeIconSize(song: Song): Float = iconSize(song.dislikesCount)
    
    override fun onCleared() {
        super.onCleared()
        positionUpdateJob?.cancel()
        mediaPlayerService.release()
    }
}

