package com.example.babiling.ui.screens.topic.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

/**
 * Lớp dữ liệu trạng thái cho ResultScreen.
 * Giúp đóng gói tất cả thông tin cần thiết cho giao diện.
 */
data class ResultUiState(
    val totalCards: Int = 0,
    val learnedCards: Int = 0,
    val isLoading: Boolean = true
)

/**
 * ViewModel cho màn hình ResultScreen.
 * ✨ PHIÊN BẢN HOÀN THIỆN: Đã thêm logic đồng bộ để tránh race condition. ✨
 */
class ResultViewModel(
    private val repo: FlashcardRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    /**
     * Tải dữ liệu kết quả cho một chủ đề cụ thể.
     * @param topicId ID của chủ đề cần lấy kết quả.
     */
    fun loadResult(topicId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ResultUiState(isLoading = false)
            Log.w("ResultViewModel", "User chưa đăng nhập, không thể tải kết quả.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // ✨ SỬA LỖI Ở ĐÂY: THÊM LOGIC ĐỒNG BỘ ✨

                // 1. Đồng bộ LÊN TRƯỚC để đẩy tiến độ mới nhất (từ bài học/quiz vừa xong).
                Log.d("ResultViewModel", "Bắt đầu đồng bộ dữ liệu LÊN (sync up)...")
                repo.syncProgressUp()

                // 2. Đồng bộ XUỐNG SAU để lấy trạng thái cuối cùng từ server.
                Log.d("ResultViewModel", "Bắt đầu đồng bộ dữ liệu XUỐNG (sync down)...")
                repo.syncProgressDown()

                Log.d("ResultViewModel", "Đồng bộ hoàn tất.")

                // --- PHẦN TÍNH TOÁN (Giữ nguyên) ---

                // 3. Lấy tổng số thẻ trong chủ đề bằng hàm đã có sẵn.
                val total = repo.getCardCountForTopic(topicId)

                // 4. Lấy TẤT CẢ các bản ghi tiến độ của người dùng (giờ đã là mới nhất).
                val allUserProgress = repo.getAllProgressForUser(userId)

                // 5. ViewModel TỰ ĐẾM số thẻ đã học cho chủ đề này.
                val learned = allUserProgress.count { it.topicId == topicId && it.masteryLevel > 0 }

                // 6. Cập nhật UI với dữ liệu chính xác sau khi đã tính toán.
                _uiState.value = ResultUiState(
                    totalCards = total,
                    learnedCards = learned,
                    isLoading = false
                )
                Log.d("ResultViewModel", "Tải kết quả thành công cho topic $topicId: $learned/$total")

            } catch (e: Exception) {
                // Xử lý lỗi trong trường hợp không thể kết nối mạng hoặc có lỗi cơ sở dữ liệu.
                _uiState.update { it.copy(isLoading = false) }
                // Ghi log lỗi để debug.
                Log.e("ResultViewModel", "Lỗi khi tải kết quả cho topic $topicId", e)
            }
        }
    }
}
