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
// ✅ Navigation State (Giữ nguyên)
// -------------------------------------------------------------
sealed class LoginNavigationState {
    object Idle : LoginNavigationState()
    object Loading : LoginNavigationState()
    data class Error(val message: String) : LoginNavigationState()
    object NavigateToHome : LoginNavigationState()
}

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance() // Giữ lại nếu cần truy vấn dữ liệu hồ sơ

    private val _navigationState =
        MutableStateFlow<LoginNavigationState>(LoginNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()


    // -------------------------------------------------------------
    // ✅ Login bằng Google (Giữ nguyên)
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
    // ✅ SỬA LỖI: Login bằng Username/Password sử dụng Firebase Auth
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

            // Bước 1: Chuyển đổi Username thành Email ảo
            val finalEmail = if ("@" in username) username else "$username@babiling.app"

            try {
                // Bước 2: SỬ DỤNG FIREBASE AUTH ĐỂ XÁC THỰC
                auth.signInWithEmailAndPassword(finalEmail, password).await()

                // Bước 3: Đăng nhập thành công, điều hướng Home
                _navigationState.update { LoginNavigationState.NavigateToHome }

            } catch (e: Exception) {
                // Xử lý các lỗi xác thực từ Firebase Auth
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Tên đăng nhập hoặc mật khẩu không đúng."
                    else -> e.message ?: "Đăng nhập thất bại."
                }

                _navigationState.update {
                    LoginNavigationState.Error(errorMessage)
                }
            }
        }
    }


    // -------------------------------------------------------------
    // ✅ Reset mỗi khi vào LoginScreen (Giữ nguyên)
    // -------------------------------------------------------------
    fun resetNavigationState() {
        _navigationState.update { LoginNavigationState.Idle }
    }
}