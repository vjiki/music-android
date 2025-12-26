package com.music.android.data.model

import com.google.gson.annotations.SerializedName

data class Song(
    val id: String,
    val artist: String,
    @SerializedName("audio_url")
    val audioUrl: String,
    val cover: String,
    val title: String,
    @SerializedName("video_url")
    val videoUrl: String? = null,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val likesCount: Int = 0,
    val dislikesCount: Int = 0
)

