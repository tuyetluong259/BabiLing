package com.example.babiling.ui.screens.topic.learn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel cho màn hình LessonSelectionScreen.
 * Nhiệm vụ chính là lấy danh sách các bài học có sẵn cho một chủ đề.
 */
class LessonViewModel : ViewModel() {

    // Lấy repository thông qua ServiceLocator
    private val repository: FlashcardRepository by lazy { ServiceLocator.provideRepository() }

    // StateFlow chứa danh sách các số thứ tự của bài học (ví dụ: [1, 2, 3])
    private val _lessons = MutableStateFlow<List<Int>>(emptyList())
    val lessons = _lessons.asStateFlow()

    // StateFlow chứa danh sách các bài học đã hoàn thành (ví dụ: [1, 3])
    // TODO: Tạm thời hardcode, sẽ lấy từ DB trong tương lai
    private val _completedLessons = MutableStateFlow<Set<Int>>(setOf(1, 3)) // Dữ liệu giả
    val completedLessons = _completedLessons.asStateFlow()

    /**
     * Tải danh sách các số bài học cho một chủ đề cụ thể.
     * @param topicId ID của chủ đề cần lấy bài học (ví dụ: "animals").
     */
    fun loadLessons(topicId: String) {
        // Reset lại danh sách mỗi khi tải chủ đề mới
        _lessons.value = emptyList()

        viewModelScope.launch {
            try {
                // Gọi hàm đã tạo trong Repository để lấy danh sách các số lessonNumber
                val lessonNumbers = repository.getLessonNumbersForTopic(topicId)
                _lessons.value = lessonNumbers

                Log.d("BabiLing_Debug", "[LessonViewModel] Đã tải ${lessonNumbers.size} bài học cho chủ đề '$topicId'")

                // TODO: Ở đây, trong tương lai bạn sẽ gọi một hàm khác từ repository
                // để lấy tiến độ học của người dùng cho chủ đề này và cập nhật _completedLessons.
                // Ví dụ: val completed = repository.getCompletedLessonsForTopic(userId, topicId)
                // _completedLessons.value = completed

            } catch (e: Exception) {
                Log.e("BabiLing_Debug", "[LessonViewModel] LỖI khi tải danh sách bài học!", e)
            }
        }
    }
}
