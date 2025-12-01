package com.example.babiling.ui.screens.topic.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Cần import thư viện này

// Lớp dữ liệu để đóng gói kết quả, giúp UI dễ xử lý hơn
data class ProgressUiState(
    val totalCards: Int = 0,
    val learnedCards: Int = 0,
    val isLoading: Boolean = true
)

class ProgressViewModel(
    private val repo: FlashcardRepository = ServiceLocator.provideRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState

    fun loadProgress(topicId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ProgressUiState(isLoading = false) // Không có user, không có tiến độ
            return
        }

        viewModelScope.launch {
            _uiState.value = ProgressUiState(isLoading = true)

            try {
                // ✨ LOGIC ĐÃ SỬA LẠI: Đơn giản, hiệu quả và chính xác ✨

                // 1. Lấy tổng số thẻ trong chủ đề (Cách làm của bạn đã đúng)
                val total = repo.getFlashcardsByTopic(topicId).size

                // 2. Lấy số thẻ ĐÃ HỌC trong chủ đề này của ĐÚNG người dùng này
                //    Hàm này cần được thêm vào Repository và DAO/Firestore service.
                val learned = repo.getLearnedCardsCount(userId, topicId)

                // 3. Cập nhật UI với dữ liệu chính xác
                _uiState.value = ProgressUiState(
                    totalCards = total,
                    learnedCards = learned,
                    isLoading = false
                )
            } catch (e: Exception) {
                // Xử lý lỗi nếu có sự cố khi lấy dữ liệu
                _uiState.value = _uiState.value.copy(isLoading = false)
                // Bạn có thể thêm logic để hiển thị thông báo lỗi cho người dùng ở đây
            }
        }
    }
}
