package com.example.babiling.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -------------------------------------------------------------
// ✅ Navigation State
// -------------------------------------------------------------
sealed class LoginNavigationState {
    object Idle : LoginNavigationState()
    object Loading : LoginNavigationState()
    data class Error(val message: String) : LoginNavigationState()
    object NavigateToHome : LoginNavigationState()
}

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _navigationState =
        MutableStateFlow<LoginNavigationState>(LoginNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()


    // -------------------------------------------------------------
    // ✅ Login bằng Google
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
    // ✅ Login bằng username + password Firestore
    // -------------------------------------------------------------
    fun signInWithUsername(username: String, password: String) {

        if (username.isBlank() || password.isBlank()) {
            _navigationState.update {
                LoginNavigationState.Error("Vui lòng nhập đầy đủ thông tin")
            }
            return
        }

        viewModelScope.launch {
            _navigationState.update { LoginNavigationState.Loading }

            try {
                val querySnapshot = firestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()

                if (querySnapshot.isEmpty) {
                    _navigationState.update {
                        LoginNavigationState.Error("Tài khoản không tồn tại")
                    }
                    return@launch
                }

                val userDoc = querySnapshot.documents.first()
                val savedPassword = userDoc.getString("password")

                if (savedPassword != password) {
                    _navigationState.update {
                        LoginNavigationState.Error("Sai mật khẩu!")
                    }
                    return@launch
                }

                // ✅ Đúng → điều hướng Home
                _navigationState.update { LoginNavigationState.NavigateToHome }

            } catch (e: Exception) {
                _navigationState.update {
                    LoginNavigationState.Error(
                        e.message ?: "Đăng nhập thất bại."
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
