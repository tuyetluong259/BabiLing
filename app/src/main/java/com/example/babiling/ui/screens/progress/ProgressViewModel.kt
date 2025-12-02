package com.example.babiling.ui.screens.progress

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.TopicEntity
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// LỚP 1: Định nghĩa dữ liệu mà UI sẽ sử dụng
data class TopicProgressInfo(
    val topic: TopicEntity,
    val learnedCards: Int,
    val totalCards: Int
) {
    // Tự động tính toán % tiến độ
    val progress: Float
        get() = if (totalCards > 0) learnedCards.toFloat() / totalCards.toFloat() else 0f
}

// LỚP 2: Định nghĩa trạng thái của toàn bộ màn hình
data class ProgressUiState(
    val topicsWithProgress: List<TopicProgressInfo> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel cho màn hình ProgressScreen.
 * ✨ PHIÊN BẢN HOÀN THIỆN: Đã sửa lại thứ tự đồng bộ để tránh race condition.
 */
class ProgressViewModel(
    private val repo: FlashcardRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Bắt đầu tải dữ liệu ngay khi ViewModel được tạo
        loadProgress()
    }

    /**
     * Hàm này thực hiện logic mới:
     * 1. Đồng bộ LÊN rồi mới đồng bộ XUỐNG để đảm bảo dữ liệu mới nhất.
     * 2. Lấy dữ liệu thô từ Room (danh sách topics và danh sách progress).
     * 3. ViewModel tự mình kết hợp và tính toán để tạo ra danh sách `TopicProgressInfo`.
     * 4. Cập nhật UI State.
     */
    fun loadProgress() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, topicsWithProgress = emptyList()) }
            Log.w("ProgressViewModel", "User chưa đăng nhập, không thể tải tiến trình.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // ✨ SỬA LỖI Ở ĐÂY: THAY ĐỔI THỨ TỰ ĐỒNG BỘ ✨

                // BƯỚC 1: ĐỒNG BỘ LÊN TRƯỚC
                Log.d("ProgressViewModel", "Bắt đầu đồng bộ dữ liệu LÊN (sync up)...")
                repo.syncProgressUp()

                // BƯỚC 2: ĐỒNG BỘ XUỐNG SAU
                Log.d("ProgressViewModel", "Bắt đầu đồng bộ dữ liệu XUỐNG (sync down)...")
                repo.syncProgressDown()

                Log.d("ProgressViewModel", "Đồng bộ hoàn tất.")

                // BƯỚC 3: LẤY DỮ LIỆU THÔ TỪ ROOM (giờ đã là mới nhất)
                val allTopics = repo.getAllTopicsStatic()
                val allUserProgress = repo.getAllProgressForUser(userId)

                // BƯỚC 4: VIEWMODEL TỰ TÍNH TOÁN
                val progressInfoList = allTopics.map { topic ->
                    // Với mỗi chủ đề, đếm số thẻ đã học của user
                    val learnedCount = allUserProgress.count { it.topicId == topic.id && it.masteryLevel > 0 }
                    // Lấy tổng số thẻ của chủ đề đó
                    val totalCount = repo.getCardCountForTopic(topic.id)

                    TopicProgressInfo(
                        topic = topic,
                        learnedCards = learnedCount,
                        totalCards = totalCount
                    )
                }

                // BƯỚC 5: CẬP NHẬT UI
                _uiState.update {
                    it.copy(isLoading = false, topicsWithProgress = progressInfoList)
                }
                Log.d("ProgressViewModel", "Đã tải và xử lý xong tiến trình cho ${progressInfoList.size} chủ đề.")

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e("ProgressViewModel", "Lỗi khi tải và xử lý tiến trình!", e)
            }
        }
    }
}
