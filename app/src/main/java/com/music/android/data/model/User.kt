package com.music.android.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String,
    val nickname: String? = null,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,
    @SerializedName("accessLevel")
    val accessLevel: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean = true,
    @SerializedName("isVerified")
    val isVerified: Boolean = false,
    @SerializedName("lastLoginAt")
    val lastLoginAt: String? = null,
    @SerializedName("createdAt")
    val createdAt: String
)

enum class AuthProvider {
    GUEST, GOOGLE, APPLE, EMAIL
}

data class AuthUser(
    val id: String,
    val email: String?,
    val name: String?,
    val nickname: String?,
    val avatarUrl: String?,
    val provider: AuthProvider
)

