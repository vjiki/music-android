package com.music.android.data.model

import com.google.gson.annotations.SerializedName

data class ShortModel(
    val id: String,
    val title: String?,
    val artist: String?,
    val cover: String?,
    @SerializedName("audio_url")
    val audioUrl: String?,
    @SerializedName("video_url")
    val videoUrl: String?,
    val type: String, // "SONG" or "SHORT_VIDEO"
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val likesCount: Int = 0,
    val dislikesCount: Int = 0
)

