package com.music.android.data.api

import com.music.android.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    companion object {
        const val BASE_URL = "https://music-back-g2u6.onrender.com"
    }

    // Auth
    @POST("/api/v1/auth/authenticate")
    suspend fun authenticate(@Body request: AuthRequest): Response<AuthResponse>

    @GET("/api/v1/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    // Songs
    @GET("/api/v1/songs/{userId}")
    suspend fun getSongs(@Path("userId") userId: String): Response<List<Song>>

    // Stories
    @GET("/api/v1/stories/user/{userId}")
    suspend fun getStories(@Path("userId") userId: String): Response<List<StoryResponse>>

    @GET("/api/v1/followers/{userId}")
    suspend fun getFollowers(@Path("userId") userId: String): Response<List<FollowerResponse>>

    // Shorts
    @GET("/api/v1/shorts/{userId}")
    suspend fun getShorts(@Path("userId") userId: String): Response<List<ShortModel>>

    // Playlists
    @GET("/api/v1/playlists/{userId}")
    suspend fun getUserPlaylists(@Path("userId") userId: String): Response<List<PlaylistResponse>>

    @GET("/api/v1/playlists/{playlistId}")
    suspend fun getPlaylist(@Path("playlistId") playlistId: String): Response<PlaylistResponse>

    // Likes/Dislikes
    @POST("/api/v1/songs/{songId}/like")
    suspend fun likeSong(
        @Path("songId") songId: String,
        @Body request: LikeRequest
    ): Response<Unit>

    @POST("/api/v1/songs/{songId}/dislike")
    suspend fun dislikeSong(
        @Path("songId") songId: String,
        @Body request: LikeRequest
    ): Response<Unit>
}

data class AuthRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val authenticated: Boolean,
    val userId: String,
    val message: String
)

data class LikeRequest(
    val userId: String
)

