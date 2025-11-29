package com.example.babiling.ui.screens.topic.learn

import android.util.Log // <-- THÊM IMPORT NÀY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LearnViewModel() : ViewModel() {

    // Lấy repository một cách "lười biếng" (lazy) để tránh race condition khi khởi tạo
    private val repo by lazy { ServiceLocator.provideRepository() }

    private val _cards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val cards: StateFlow<List<FlashcardEntity>> = _cards

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index

    fun load(topicId: String) {
        // ✨ LOG 1: Kiểm tra topicId màn hình LearnScreen truyền vào là gì ✨
        Log.d("BabiLing_Debug", "[ViewModel] Bắt đầu load dữ liệu cho topicId = '$topicId'")

        viewModelScope.launch {
            try {
                val result = repo.getFlashcardsByTopic(topicId)
                // ✨ LOG 2: Kiểm tra Repository trả về bao nhiêu thẻ với topicId đó ✨
                Log.d("BabiLing_Debug", "[ViewModel] Repository trả về ${result.size} thẻ cho topicId = '$topicId'")

                _cards.value = result
                _index.value = 0
            } catch (e: Exception) {
                // ✨ LOG 3: Bắt lỗi nếu có sự cố bất ngờ ✨
                Log.e("BabiLing_Debug", "[ViewModel] LỖI khi lấy thẻ từ repo!", e)
            }
        }
    }

    fun next() {
        // Chỉ tăng index nếu nó chưa phải là thẻ cuối cùng
        if (_index.value < _cards.value.size - 1) {
            _index.value++
        }
    }

    fun previous() {
        // Chỉ giảm index nếu nó không phải là thẻ đầu tiên
        if (_index.value > 0) {
            _index.value--
        }
    }

    // Hàm onCardReviewed không còn cần thiết cho màn hình học đơn giản nữa.
    /*
    fun onCardReviewed(card: FlashcardEntity, wasCorrect: Boolean) {
        viewModelScope.launch {
            repo.recordProgress(
                flashcardId = card.id.toString(),
                newMasteryLevel = if (wasCorrect) 1 else 0,
                newCorrectCountInRow = if (wasCorrect) 1 else 0
            )
            next()
        }
    }
    */
}
