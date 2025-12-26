package com.music.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.music.android.data.api.ApiService
import com.music.android.data.model.AuthProvider
import com.music.android.data.model.AuthRequest
import com.music.android.data.model.AuthUser
import com.music.android.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(
    private val context: Context,
    private val apiService: ApiService
) {
    private val USER_KEY = stringPreferencesKey("current_user")
    private val gson = Gson()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    val isAuthenticated: StateFlow<Boolean> = _currentUser.map { user ->
        user != null && user.provider != AuthProvider.GUEST
    }.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    init {
        loadUser()
    }
    
    private fun loadUser() {
        scope.launch {
            context.dataStore.data.collect { preferences ->
                val userJson = preferences[USER_KEY]
                val user = userJson?.let { json ->
                    try {
                        gson.fromJson(json, AuthUser::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                _currentUser.value = user
            }
        }
    }

    suspend fun getCurrentUser(): AuthUser? = currentUser.first()

    suspend fun getCurrentUserId(): String {
        val user = getCurrentUser()
        return user?.id ?: GUEST_USER_ID
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        return try {
            val authRequest = AuthRequest(email, password)
            val authResponse = apiService.authenticate(authRequest)
            
            if (authResponse.isSuccessful && authResponse.body()?.authenticated == true) {
                val userId = authResponse.body()!!.userId
                val userResponse = apiService.getUser(userId)
                
                if (userResponse.isSuccessful && userResponse.body() != null) {
                    val user = userResponse.body()!!
                    val authUser = AuthUser(
                        id = user.id,
                        email = user.email,
                        name = user.nickname ?: user.email.split("@").first(),
                        nickname = user.nickname,
                        avatarUrl = user.avatarUrl,
                        provider = AuthProvider.EMAIL
                    )
                    saveUser(authUser)
                    Result.success(authUser)
                } else {
                    Result.failure(Exception("Failed to get user info"))
                }
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(googleUser: AuthUser): Result<AuthUser> {
        return try {
            saveUser(googleUser)
            Result.success(googleUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }

    private suspend fun saveUser(user: AuthUser) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = gson.toJson(user)
        }
    }

    fun getEffectiveUser(): AuthUser {
        return AuthUser(
            id = GUEST_USER_ID,
            email = "guest@example.com",
            name = "Guest",
            nickname = "Guest",
            avatarUrl = null,
            provider = AuthProvider.GUEST
        )
    }

    companion object {
        const val GUEST_USER_ID = "3762deba-87a9-482e-b716-2111232148ca"
    }
}

