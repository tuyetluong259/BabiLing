package com.example.babiling.ui.screens.topic.learn

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel cho màn hình LearnScreen, quản lý logic học theo từng bài.
 * ✨ HOÀN THIỆN: Sử dụng SavedStateHandle để tự động nhận tham số từ Navigation.
 */
class LearnViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Lấy repository thông qua ServiceLocator
    private val repo by lazy { ServiceLocator.provideRepository() }

    // --- StateFlows công khai cho UI quan sát ---

    // Chứa danh sách các thẻ của BÀI HỌC hiện tại.
    private val _cards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val cards = _cards.asStateFlow()

    // Chứa chỉ số (index) của thẻ đang được hiển thị.
    private val _index = MutableStateFlow(0)
    val index = _index.asStateFlow()

    // Cờ báo hiệu người dùng đã học xong thẻ cuối cùng của bài.
    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    // ✨ HOÀN THIỆN: Tự động lấy topicId và lessonNumber từ arguments của navigation.
    private val topicId: String = checkNotNull(savedStateHandle["topicId"])
    private val lessonNumber: Int = savedStateHandle.get<String>("lessonNumber")?.toInt() ?: 0

    /**
     * Khối init sẽ được gọi ngay khi ViewModel được tạo.
     * Chúng ta sẽ gọi hàm tải dữ liệu ngay tại đây.
     */
    init {
        Log.d("BabiLing_Debug", "[ViewModel] Được khởi tạo với topicId='$topicId', lessonNumber=$lessonNumber")
        loadData()
    }

    /**
     * Tải dữ liệu cho một BÀI HỌC CỤ THỂ.
     * Hàm này giờ là private vì nó được gọi tự động từ khối init.
     */
    private fun loadData() {
        // Reset lại tất cả state mỗi khi tải bài mới để tránh hiển thị dữ liệu cũ
        _cards.value = emptyList()
        _index.value = 0
        _isFinished.value = false

        viewModelScope.launch {
            try {
                // Sử dụng hàm mới từ Repository
                val result = repo.getFlashcardsByLesson(topicId, lessonNumber)
                Log.d("BabiLing_Debug", "[ViewModel] Repository trả về ${result.size} thẻ.")

                _cards.value = result

            } catch (e: Exception) {
                Log.e("BabiLing_Debug", "[ViewModel] LỖI khi lấy thẻ từ repo theo bài học!", e)
            }
        }
    }

    /**
     * Chuyển đến thẻ tiếp theo.
     * Nếu đang ở thẻ cuối cùng, sẽ đánh dấu bài học đã hoàn thành.
     */
    fun next() {
        val currentIndex = _index.value
        val totalCards = _cards.value.size

        if (totalCards == 0) return

        if (currentIndex < totalCards - 1) {
            _index.update { it + 1 }
        } else {
            Log.d("BabiLing_Debug", "[ViewModel] Đã đến thẻ cuối cùng. Đánh dấu bài học hoàn thành.")
            _isFinished.value = true
        }
    }

    /**
     * Quay lại thẻ trước đó.
     */
    fun previous() {
        if (_index.value > 0) {
            _index.update { it - 1 }
        }
    }
}
