package com.music.android.data.cache

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.security.MessageDigest

data class CachedAudioMetadata(
    val url: String,
    val title: String,
    val artist: String,
    val coverURL: String?
)

data class CachedImageMetadata(
    val url: String
)

data class CachedVideoMetadata(
    val url: String,
    val title: String,
    val artist: String,
    val coverURL: String?
)

data class CacheData(
    val totalSize: Double, // GB
    val categories: List<CacheCategory>
)

data class CacheCategory(
    val name: String,
    val size: Double, // GB
    val percentage: Double,
    val color: Long // Color as Long (ARGB)
)

class CacheService private constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: CacheService? = null
        
        fun getInstance(context: Context): CacheService {
            return INSTANCE ?: synchronized(this) {
                val instance = CacheService(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    private val cacheDir: File
    private val imagesCacheDir: File
    private val audioCacheDir: File
    private val videoCacheDir: File
    
    private val _totalCacheSize = MutableStateFlow(0.0)
    val totalCacheSize: StateFlow<Double> = _totalCacheSize.asStateFlow()
    
    private val _imagesCacheSize = MutableStateFlow(0.0)
    val imagesCacheSize: StateFlow<Double> = _imagesCacheSize.asStateFlow()
    
    private val _audioCacheSize = MutableStateFlow(0.0)
    val audioCacheSize: StateFlow<Double> = _audioCacheSize.asStateFlow()
    
    private val _videoCacheSize = MutableStateFlow(0.0)
    val videoCacheSize: StateFlow<Double> = _videoCacheSize.asStateFlow()
    
    init {
        cacheDir = File(context.cacheDir, "MusicAppCache")
        imagesCacheDir = File(cacheDir, "Images")
        audioCacheDir = File(cacheDir, "Audio")
        videoCacheDir = File(cacheDir, "Video")
        
        // Create directories
        cacheDir.mkdirs()
        imagesCacheDir.mkdirs()
        audioCacheDir.mkdirs()
        videoCacheDir.mkdirs()
        
        // Calculate initial cache size
        calculateCacheSize()
    }
    
    // MARK: - Image Caching
    fun cacheImage(url: String, data: ByteArray) {
        val fileName = url.md5() + ".jpg"
        val file = File(imagesCacheDir, fileName)
        file.writeBytes(data)
        calculateCacheSize()
    }
    
    fun getCachedImageFile(url: String): File? {
        val fileName = url.md5() + ".jpg"
        val file = File(imagesCacheDir, fileName)
        return if (file.exists()) file else null
    }
    
    fun hasCachedImage(url: String): Boolean {
        val fileName = url.md5() + ".jpg"
        return File(imagesCacheDir, fileName).exists()
    }
    
    // MARK: - Audio Caching
    fun cacheAudio(url: String, data: ByteArray, title: String? = null, artist: String? = null, coverURL: String? = null) {
        val fileName = url.md5() + ".mp3"
        val file = File(audioCacheDir, fileName)
        file.writeBytes(data)
        calculateCacheSize()
    }
    
    fun getCachedAudioFile(url: String): File? {
        val fileName = url.md5() + ".mp3"
        val file = File(audioCacheDir, fileName)
        return if (file.exists()) file else null
    }
    
    fun hasCachedAudio(url: String): Boolean {
        val fileName = url.md5() + ".mp3"
        return File(audioCacheDir, fileName).exists()
    }
    
    // MARK: - Video Caching
    fun cacheVideo(url: String, data: ByteArray, title: String? = null, artist: String? = null, coverURL: String? = null) {
        val fileName = url.md5() + ".mp4"
        val file = File(videoCacheDir, fileName)
        file.writeBytes(data)
        calculateCacheSize()
    }
    
    fun getCachedVideoFile(url: String): File? {
        val fileName = url.md5() + ".mp4"
        val file = File(videoCacheDir, fileName)
        return if (file.exists()) file else null
    }
    
    fun hasCachedVideo(url: String): Boolean {
        val fileName = url.md5() + ".mp4"
        return File(videoCacheDir, fileName).exists()
    }
    
    // MARK: - Cache Size Calculation
    fun calculateCacheSize() {
        val imagesSize = calculateDirectorySize(imagesCacheDir)
        val audioSize = calculateDirectorySize(audioCacheDir)
        val videoSize = calculateDirectorySize(videoCacheDir)
        
        _imagesCacheSize.value = imagesSize
        _audioCacheSize.value = audioSize
        _videoCacheSize.value = videoSize
        _totalCacheSize.value = imagesSize + audioSize + videoSize
    }
    
    private fun calculateDirectorySize(directory: File): Double {
        if (!directory.exists() || !directory.isDirectory) return 0.0
        
        var totalSize: Long = 0
        directory.walkTopDown().forEach { file ->
            if (file.isFile) {
                totalSize += file.length()
            }
        }
        
        // Convert bytes to GB
        return totalSize / (1024.0 * 1024.0 * 1024.0)
    }
    
    // MARK: - Clear Cache
    suspend fun clearAllCache() {
        clearDirectory(imagesCacheDir)
        clearDirectory(audioCacheDir)
        clearDirectory(videoCacheDir)
        calculateCacheSize()
    }
    
    suspend fun clearImagesCache() {
        clearDirectory(imagesCacheDir)
        calculateCacheSize()
    }
    
    suspend fun clearAudioCache() {
        clearDirectory(audioCacheDir)
        calculateCacheSize()
    }
    
    suspend fun clearVideoCache() {
        clearDirectory(videoCacheDir)
        calculateCacheSize()
    }
    
    suspend fun clearCachedImage(url: String) {
        val fileName = url.md5() + ".jpg"
        File(imagesCacheDir, fileName).delete()
        calculateCacheSize()
    }
    
    suspend fun clearCachedAudio(url: String) {
        val fileName = url.md5() + ".mp3"
        File(audioCacheDir, fileName).delete()
        calculateCacheSize()
    }
    
    suspend fun clearCachedVideo(url: String) {
        val fileName = url.md5() + ".mp4"
        File(videoCacheDir, fileName).delete()
        calculateCacheSize()
    }
    
    private suspend fun clearDirectory(directory: File) {
        if (!directory.exists() || !directory.isDirectory) return
        
        directory.walkTopDown().forEach { file ->
            if (file.isFile) {
                file.delete()
            }
        }
    }
    
    // MARK: - Get Cache Statistics
    fun getCacheStatistics(): CacheData {
        val totalSize = _totalCacheSize.value
        val imagesSize = _imagesCacheSize.value
        val audioSize = _audioCacheSize.value
        val videoSize = _videoCacheSize.value
        
        val imagesPercentage = if (totalSize > 0) (imagesSize / totalSize) * 100 else 0.0
        val audioPercentage = if (totalSize > 0) (audioSize / totalSize) * 100 else 0.0
        val videoPercentage = if (totalSize > 0) (videoSize / totalSize) * 100 else 0.0
        
        val categories = mutableListOf<CacheCategory>()
        
        if (imagesSize > 0) {
            categories.add(CacheCategory(
                name = "Photos",
                size = imagesSize,
                percentage = imagesPercentage,
                color = 0xFF00FFFF // Cyan
            ))
        }
        
        if (audioSize > 0) {
            categories.add(CacheCategory(
                name = "Music",
                size = audioSize,
                percentage = audioPercentage,
                color = 0xFFFF0000 // Red
            ))
        }
        
        if (videoSize > 0) {
            categories.add(CacheCategory(
                name = "Videos",
                size = videoSize,
                percentage = videoPercentage,
                color = 0xFFFF00FF // Purple
            ))
        }
        
        return CacheData(totalSize = totalSize, categories = categories)
    }
    
    // MARK: - Get Cached Items
    suspend fun getCachedImageMetadata(): List<CachedImageMetadata> {
        val metadata = mutableListOf<CachedImageMetadata>()
        imagesCacheDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".jpg")) {
                // Extract URL from filename (simplified - in production, store metadata separately)
                val url = file.name.removeSuffix(".jpg").removePrefix(file.name.substringBefore("_"))
                metadata.add(CachedImageMetadata(url = url))
            }
        }
        return metadata
    }
    
    suspend fun getCachedAudioMetadata(): List<CachedAudioMetadata> {
        val metadata = mutableListOf<CachedAudioMetadata>()
        audioCacheDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".mp3")) {
                // Extract URL from filename (simplified - in production, store metadata separately)
                val url = file.name.removeSuffix(".mp3").removePrefix(file.name.substringBefore("_"))
                metadata.add(CachedAudioMetadata(
                    url = url,
                    title = "Unknown Song",
                    artist = "Unknown Artist",
                    coverURL = null
                ))
            }
        }
        return metadata
    }
    
    suspend fun getCachedVideoMetadata(): List<CachedVideoMetadata> {
        val metadata = mutableListOf<CachedVideoMetadata>()
        videoCacheDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".mp4")) {
                // Extract URL from filename (simplified - in production, store metadata separately)
                val url = file.name.removeSuffix(".mp4").removePrefix(file.name.substringBefore("_"))
                metadata.add(CachedVideoMetadata(
                    url = url,
                    title = "Unknown Video",
                    artist = "Unknown Artist",
                    coverURL = null
                ))
            }
        }
        return metadata
    }
    
    // MARK: - MD5 Hash Extension
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val hash = md.digest(this.toByteArray())
        val hashString = hash.joinToString("") { "%02x".format(it) }
        
        // Create safe filename
        val safeString = this.replace(Regex("[/:?=&%]"), "_")
        return "${hashString}_${safeString.take(50)}"
    }
}

