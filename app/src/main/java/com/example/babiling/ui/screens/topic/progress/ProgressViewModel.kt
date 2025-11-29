package com.example.babiling.ui.screens.topic.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        // Lấy userId hiện tại
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ProgressUiState(isLoading = false) // Không có user, không có tiến độ
            return
        }

        viewModelScope.launch {
            _uiState.value = ProgressUiState(isLoading = true)

            // Lấy tổng số thẻ trong chủ đề
            val cardsInTopic = repo.getFlashcardsByTopic(topicId)
            val total = cardsInTopic.size

            // ✨ LOGIC MỚI: Lấy tất cả tiến độ của user và lọc ra những thẻ thuộc chủ đề này
            // Cần thêm hàm getAllProgressForUser vào DAO và Repository
            val allUserProgress = repo.getAllProgressForUser(userId)

            // Lọc ra các thẻ đã học (masteryLevel > 0) và thuộc chủ đề đang xem
            val learned = allUserProgress.count { progress ->
                // Kiểm tra xem thẻ trong bản ghi tiến độ có thuộc danh sách thẻ của chủ đề này không
                cardsInTopic.any { card -> card.id.toString() == progress.flashcardId }
                        && progress.masteryLevel > 0 // Và có masteryLevel > 0
            }

            _uiState.value = ProgressUiState(
                totalCards = total,
                learnedCards = learned,
                isLoading = false
            )
        }
    }
}
