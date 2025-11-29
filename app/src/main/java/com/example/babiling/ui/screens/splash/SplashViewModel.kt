package com.example.babiling.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.Screen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    // Một StateFlow để giữ route của màn hình tiếp theo.
    // Ban đầu là null, khi có giá trị, UI sẽ lắng nghe và điều hướng.
    private val _nextScreen = MutableStateFlow<String?>(null)
    val nextScreen = _nextScreen.asStateFlow()

    init {
        // Bắt đầu kiểm tra ngay khi ViewModel được tạo
        checkUserStatusAndDecideNextScreen()
    }

    private fun checkUserStatusAndDecideNextScreen() {
        // viewModelScope đảm bảo coroutine này chạy an toàn và tự hủy khi không cần thiết
        viewModelScope.launch {
            // 1. Chờ một khoảng thời gian ngắn để logo hiển thị đẹp mắt
            delay(2500L) // Có thể tăng/giảm tùy ý

            // 2. Kiểm tra trạng thái đăng nhập của người dùng từ Firebase
            val currentUser = Firebase.auth.currentUser

            // 3. Quyết định màn hình tiếp theo
            if (currentUser != null) {
                // Nếu người dùng đã đăng nhập, đi thẳng vào Home
                _nextScreen.value = Screen.Home.route
            } else {
                // Nếu chưa, đi đến màn hình giới thiệu
                _nextScreen.value = Screen.Onboarding.route
            }
        }
    }
}
