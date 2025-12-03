package com.example.babiling.ui.screens.topic.learn // Gói này có thể cần đổi tên thành ...topic.lessonselect

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

class LessonViewModel(
    private val topicId: String,
    private val repo: FlashcardRepository
) : ViewModel() {

    // `lessonsUiState` sẽ tự động cập nhật mỗi khi tiến độ học thay đổi trong Room.
    val lessonsUiState: StateFlow<List<LessonUiState>> =
        // 1. Lắng nghe luồng dữ liệu về các bài học đã hoàn thành.
        repo.getCompletedLessonsFlow(topicId)
            // 2. Sử dụng flatMapLatest để xử lý các hàm suspend bên trong.
            .flatMapLatest { completedLessonsSet ->
                Log.d("BabiLing_Debug", "[LessonViewModel] Dữ liệu thay đổi! Các bài đã hoàn thành: $completedLessonsSet")

                // 3. Lấy danh sách TẤT CẢ các bài học một lần (dưới dạng suspend).
                val allLessonNumbers = repo.getLessonNumbersForTopic(topicId)
                val cardCountPerLesson = repo.getCardCountPerLesson(topicId) // Lấy số thẻ mỗi bài

                // 4. Biến đổi kết quả thành danh sách LessonUiState.
                val uiStateList = allLessonNumbers.map { lessonNumber ->
                    LessonUiState(
                        number = lessonNumber,
                        isCompleted = completedLessonsSet.contains(lessonNumber),
                        totalCards = cardCountPerLesson[lessonNumber] ?: 0
                    )
                }.sortedBy { it.number }

                // 5. Phát ra danh sách UI state mới.
                kotlinx.coroutines.flow.flowOf(uiStateList)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList() // Giá trị ban đầu khi chưa có dữ liệu
            )
}

