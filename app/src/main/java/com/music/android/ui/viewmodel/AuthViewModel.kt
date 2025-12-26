package com.music.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.data.model.AuthUser
import com.music.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser
    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authRepository.signInWithEmail(email, password)
                .onSuccess {
                    _isLoading.value = false
                }
                .onFailure {
                    _isLoading.value = false
                    _error.value = it.message ?: "Sign in failed"
                }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
    
    fun getEffectiveUser(): AuthUser {
        return authRepository.getEffectiveUser()
    }
}

