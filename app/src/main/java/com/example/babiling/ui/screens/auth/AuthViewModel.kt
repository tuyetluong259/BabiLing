package com.example.babiling.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI state cho Google Login
data class AuthUiState(
    val isLoading: Boolean = false,
    val isGoogleLoginSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    //Login bằng Google
    fun handleGoogleLogin() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.signInWithGoogle()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isGoogleLoginSuccessful = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Lỗi đăng nhập Google."
                    )
                }
            }
        }
    }

    //Reset error sau khi hiển thị popup
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    //Reset trạng thái Google login nếu cần điều hướng lại
    fun resetGoogleLoginFlag() {
        _uiState.update { it.copy(isGoogleLoginSuccessful = false) }
    }
}
