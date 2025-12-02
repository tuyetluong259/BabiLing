package com.example.babiling.ui.screens.topic.learn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Data class đại diện cho một bài học trên UI
 */
data class LessonUiState(
    val number: Int,
    val isCompleted: Boolean,
    val totalCards: Int
)

/**
 * ViewModel cho màn hình LessonSelectionScreen.
 * ✨ HOÀN THIỆN: Sử dụng kiến trúc Flow để tự động cập nhật trạng thái "isCompleted".
 */
class LessonViewModel(
    private val topicId: String,
    private val repo: FlashcardRepository
) : ViewModel() {

    val lessonsUiState: StateFlow<List<LessonUiState>> =
        repo.getCompletedLessonsFlow(topicId)
            .map { completedLessonsSet ->
                Log.d("BabiLing_Debug", "[LessonViewModel] Dữ liệu thay đổi! Các bài đã hoàn thành: $completedLessonsSet")

                // ✨ SỬA LỖI: Đổi tên hàm cho đúng với hàm đã tạo trong Repository
                val allLessonNumbers = repo.getLessonNumbersForTopic(topicId)

                // Biến đổi danh sách tất cả số bài học thành danh sách LessonUiState
                allLessonNumbers.map { lessonNumber ->
                    LessonUiState(
                        number = lessonNumber,
                        // Một bài học được coi là hoàn thành nếu số của nó có trong Set đã hoàn thành
                        isCompleted = completedLessonsSet.contains(lessonNumber),
                        totalCards = 0 // Bạn có thể thêm logic để lấy số thẻ nếu cần
                    )
                }.sortedBy { it.number } // Sắp xếp lại cho đúng thứ tự
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
