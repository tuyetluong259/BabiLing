package com.example.babiling.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// AuthRepository cùng package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Trạng thái UI
data class AuthUiState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val needsOtpVerification: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isGoogleLoginSuccessful: Boolean = false, // TRẠNG THÁI MỚI CHO GOOGLE
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    // --- LOGIC CHUNG (Login Screen) ---

    fun onPhoneNumberChange(newNumber: String) {
        val filteredNumber = newNumber.filter { it.isDigit() }
        _uiState.update { it.copy(phoneNumber = filteredNumber, errorMessage = null) }
    }

    // Gửi mã OTP
    fun handleLogin() {
        val currentNumber = _uiState.value.phoneNumber
        if (currentNumber.length < 10) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đủ 10 số điện thoại.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.sendVerificationCode(currentNumber)
                _uiState.update { it.copy(isLoading = false, needsOtpVerification = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi gửi mã.") }
            }
        }
    }

    // Đăng nhập bằng Google
    fun handleGoogleLogin() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.signInWithGoogle()
                _uiState.update { it.copy(isLoading = false, isGoogleLoginSuccessful = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi Google Sign-In.") }
            }
        }
    }

    fun onOtpVerificationNavigated() {
        _uiState.update { it.copy(needsOtpVerification = false) }
    }

    // --- LOGIC CHO VERIFICATION SCREEN ---

    fun onOtpCodeChange(newCode: String) {
        val filteredCode = newCode.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(otpCode = filteredCode, errorMessage = null) }
    }

    // Xác minh mã OTP
    fun handleOtpVerification() {
        val currentOtp = _uiState.value.otpCode
        if (currentOtp.length != 6) {
            _uiState.update { it.copy(errorMessage = "Mã OTP phải có 6 chữ số.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.signInWithOtp(currentOtp)
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Xác minh OTP thất bại.") }
            }
        }
    }
}