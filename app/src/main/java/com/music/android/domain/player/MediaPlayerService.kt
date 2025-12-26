package com.music.android.domain.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.android.data.model.Song

class MediaPlayerService(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    var currentSong: Song? = null
        private set
    
    var isPlaying: Boolean = false
        get() = player.isPlaying
    
    var currentPosition: Long = 0L
        get() = player.currentPosition
    
    var duration: Long = 0L
        get() = player.duration
    
    var playbackState: Int = Player.STATE_IDLE
        get() = player.playbackState
    
    fun loadSong(song: Song) {
        currentSong = song
        val mediaItem = MediaItem.fromUri(song.audioUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
    }
    
    fun play() {
        player.play()
    }
    
    fun pause() {
        player.pause()
    }
    
    fun seekTo(position: Long) {
        player.seekTo(position)
    }
    
    fun release() {
        player.release()
    }
    
    fun addListener(listener: Player.Listener) {
        player.addListener(listener)
    }
    
    fun removeListener(listener: Player.Listener) {
        player.removeListener(listener)
    }
    
    fun getPlayer(): ExoPlayer = player
}

