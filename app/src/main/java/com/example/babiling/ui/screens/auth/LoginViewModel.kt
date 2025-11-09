package com.example.babiling.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -------------------------------------------------------------
// ✅ Navigation state — chỉ còn Home + Error + Idle + Loading
// -------------------------------------------------------------
sealed class LoginNavigationState {
    object Idle : LoginNavigationState()
    object Loading : LoginNavigationState()
    data class Error(val message: String) : LoginNavigationState()
    object NavigateToHome : LoginNavigationState()
}

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _navigationState =
        MutableStateFlow<LoginNavigationState>(LoginNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()

    // -------------------------------------------------------------
    // ✅ Google Login Only
    // -------------------------------------------------------------
    fun signInWithGoogleCredential(credential: AuthCredential) {
        viewModelScope.launch {

            _navigationState.update { LoginNavigationState.Loading }

            try {
                auth.signInWithCredential(credential).await()
                _navigationState.update { LoginNavigationState.NavigateToHome }

            } catch (e: Exception) {
                _navigationState.update {
                    LoginNavigationState.Error(
                        e.message ?: "Đăng nhập Google thất bại."
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // ✅ Reset mỗi khi vào LoginScreen
    // -------------------------------------------------------------
    fun resetNavigationState() {
        _navigationState.update { LoginNavigationState.Idle }
    }
}
