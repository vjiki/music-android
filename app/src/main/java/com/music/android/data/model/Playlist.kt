package com.music.android.data.model

import com.google.gson.annotations.SerializedName

data class PlaylistResponse(
    val id: String,
    val name: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("coverUrl")
    val coverUrl: String?,
    @SerializedName("isDefaultLikes")
    val isDefaultLikes: Boolean = false,
    @SerializedName("isDefaultDislikes")
    val isDefaultDislikes: Boolean = false,
    val songs: List<PlaylistSong>? = null
)

data class PlaylistSong(
    @SerializedName("songId")
    val songId: String,
    @SerializedName("songTitle")
    val songTitle: String,
    @SerializedName("songArtist")
    val songArtist: String,
    @SerializedName("songCoverUrl")
    val songCoverUrl: String
)

enum class PlaylistKind {
    LIKED, DISLIKED
}

