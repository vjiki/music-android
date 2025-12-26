package com.music.android.data.model

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("userAvatarUrl")
    val userAvatarUrl: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("previewUrl")
    val previewUrl: String?,
    @SerializedName("storyType")
    val storyType: String,
    @SerializedName("songId")
    val songId: String?,
    @SerializedName("songTitle")
    val songTitle: String?,
    @SerializedName("songArtist")
    val songArtist: String?,
    val caption: String?,
    val location: String?,
    @SerializedName("viewsCount")
    val viewsCount: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("isExpired")
    val isExpired: Boolean
)

data class FollowerResponse(
    @SerializedName("followerId")
    val followerId: String,
    @SerializedName("followerEmail")
    val followerEmail: String,
    @SerializedName("followerNickname")
    val followerNickname: String,
    @SerializedName("followerAvatarUrl")
    val followerAvatarUrl: String?,
    @SerializedName("followedAt")
    val followedAt: String
)

data class MusicStory(
    val id: String,
    val userId: String,
    val userName: String,
    val profileImageURL: String?,
    val storyImageURL: String?,
    val storyPreviewURL: String?,
    val song: Song,
    val isViewed: Boolean = false
)

