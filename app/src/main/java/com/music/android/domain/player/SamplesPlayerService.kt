package com.music.android.domain.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.android.data.cache.CacheService
import com.music.android.data.model.ShortModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class SamplesPlayerService(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var videoReadyListener: Player.Listener? = null
    private val cacheService = CacheService.getInstance(context)
    
    var currentShort: ShortModel? = null
        private set
    
    var isPlaying: Boolean = false
        get() = player.isPlaying
    
    var currentPosition: Long = 0L
        get() = player.currentPosition
    
    var duration: Long = 0L
        get() = player.duration
    
    var playbackState: Int = Player.STATE_IDLE
        get() = player.playbackState
    
    val isVideo: Boolean
        get() = currentShort?.type == "SHORT_VIDEO"
    
    init {
        // Loop video playback
        player.repeatMode = Player.REPEAT_MODE_ONE
    }
    
    suspend fun loadShort(short: ShortModel) {
        currentShort = short
        
        // Remove previous listener if exists
        videoReadyListener?.let { player.removeListener(it) }
        videoReadyListener = null
        
        val urlString = if (short.type == "SHORT_VIDEO" && !short.videoUrl.isNullOrEmpty()) {
            short.videoUrl
        } else if (!short.audioUrl.isNullOrEmpty()) {
            short.audioUrl
        } else {
            return
        }
        
        if (urlString == null || urlString.isEmpty()) return
        
        // Check cache first
        val cachedFile = if (short.type == "SHORT_VIDEO") {
            cacheService.getCachedVideoFile(urlString)
        } else {
            cacheService.getCachedAudioFile(urlString)
        }
        
        val mediaItem = if (cachedFile != null && cachedFile.exists()) {
            // Use cached file
            MediaItem.fromUri(Uri.fromFile(cachedFile))
        } else {
            // Use remote URL and cache in background
            MediaItem.fromUri(urlString)
        }
        
        player.setMediaItem(mediaItem)
        player.prepare()
        
        // Cache in background if not already cached
        if (cachedFile == null || !cachedFile.exists()) {
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection()
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    val data = inputStream.readBytes()
                    inputStream.close()
                    
                    if (short.type == "SHORT_VIDEO") {
                        cacheService.cacheVideo(
                            urlString,
                            data,
                            short.title,
                            short.artist,
                            short.cover
                        )
                    } else {
                        cacheService.cacheAudio(
                            urlString,
                            data,
                            short.title,
                            short.artist,
                            short.cover
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        
        // Auto-play video when ready
        if (short.type == "SHORT_VIDEO") {
            videoReadyListener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY && !player.isPlaying) {
                        player.play()
                    }
                }
            }
            player.addListener(videoReadyListener!!)
        }
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
    
    fun stop() {
        videoReadyListener?.let { player.removeListener(it) }
        videoReadyListener = null
        player.stop()
        player.clearMediaItems()
        currentShort = null
    }
    
    fun release() {
        videoReadyListener?.let { player.removeListener(it) }
        videoReadyListener = null
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

