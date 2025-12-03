package com.example.babiling.ui.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.data.repository.AuthRepository
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI state được tinh gọn cho các luồng xác thực chính
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AuthViewModel(
    // ✅ CHỈ NHẬN AuthRepository (Vì Factory chỉ truyền 1 tham số)
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // 💡 LẤY FirebaseAuth TỪ REPOSITORY ĐỂ DÙNG CHO LISTENER
    private val firebaseAuth: FirebaseAuth = repository.firebaseAuth

    // ✨ BƯỚC 2: TẠO NGUỒN CUNG CẤP THÔNG TIN NGƯỜI DÙNG ĐÁNG TIN CẬY ✨
    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        // Lắng nghe mọi sự thay đổi trạng thái đăng nhập (đăng nhập, đăng xuất)
        // và tự động cập nhật _currentUser.
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    // --- HÀM MỚI: LƯU HỒ SƠ NGƯỜI DÙNG ---
    /**
     * Lưu các thay đổi hồ sơ người dùng lên Firestore và Firebase Auth.
     */
    fun saveProfileChanges(newUsername: String, newAccountName: String) {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            _uiState.update { it.copy(errorMessage = "Người dùng chưa đăng nhập.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                // Gọi hàm Repository để đẩy dữ liệu lên Firestore và Auth
                repository.updateProfile(uid, newUsername, newAccountName)

                // Cập nhật lại _currentUser để UI thấy sự thay đổi ngay lập tức
                _currentUser.value = firebaseAuth.currentUser

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Hồ sơ đã được cập nhật thành công!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi cập nhật hồ sơ: ${e.message}"
                    )
                }
            }
        }
    }


    /**
     * Xử lý kết quả trả về từ màn hình đăng nhập của Google.
     */
    fun handleGoogleLogin(data: Intent?) {
        if (data == null) {
            _uiState.update { it.copy(errorMessage = "Đã hủy đăng nhập bằng Google.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                repository.handleGoogleSignIn(data)
                // Listener ở init sẽ tự động cập nhật currentUser
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is ApiException -> "Lỗi cấu hình Google (Code: ${e.statusCode})."
                    else -> e.message ?: "Lỗi đăng nhập Google không xác định."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
            }
        }
    }

    /**
     * Xử lý đăng nhập bằng Username hoặc Email.
     */
    fun signInWithUsernameOrEmail(usernameOrEmail: String, password: String) {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tên đăng nhập và mật khẩu không được để trống.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val finalEmail = if ("@" in usernameOrEmail) usernameOrEmail else "$usernameOrEmail@babiling.app"
            try {
                repository.signInWithEmailPassword(finalEmail, password)
                // Listener ở init sẽ tự động cập nhật currentUser
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = getFriendlyErrorMessage(e)) }
            }
        }
    }

    /**
     * Xử lý đăng ký bằng Username.
     */
    fun registerWithUsername(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tên đăng nhập và mật khẩu không được để trống.") }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu phải có ít nhất 6 ký tự.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val emailToRegister = "$username@babiling.app"
            try {
                repository.createUser(emailToRegister, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccessful = true,
                        successMessage = "Đăng ký thành công! Vui lòng quay lại và đăng nhập."
                    )
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Tên đăng nhập này đã tồn tại."
                    else -> getFriendlyErrorMessage(e)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    // ✨ BƯỚC 3: THÊM HÀM ĐĂNG XUẤT ĐỂ QUẢN LÝ TRẠNG THÁI TẬP TRUNG ✨
    fun signOut() {
        repository.signOut() // Gọi hàm signOut trong Repository
        // Listener ở init sẽ tự động cập nhật _currentUser thành null
        resetAllFlags() // Đặt lại trạng thái UI
    }

    private fun getFriendlyErrorMessage(e: Exception): String {
        return when (e) {
            is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Tên đăng nhập không tồn tại."
            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Mật khẩu không đúng. Vui lòng thử lại."
            else -> "Đã xảy ra lỗi không xác định. Vui lòng thử lại."
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun resetAllFlags() {
        _uiState.update { it.copy(isLoginSuccessful = false, isRegisterSuccessful = false) }
    }
}