package com.example.babiling.ui.screens.topic.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repo: FlashcardRepository = ServiceLocator.provideRepository()) : ViewModel() {

    private val _cards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val cards: StateFlow<List<FlashcardEntity>> = _cards

    /**
     * ✨ ĐÃ SỬA: Hàm load linh hoạt hơn.
     * Tải danh sách các thẻ flashcard cho quiz.
     *
     * @param topicId ID của chủ đề. Nếu là null hoặc rỗng, sẽ tải tất cả các thẻ để ôn tập.
     */
    fun load(topicId: String?) { // 1. Sửa tham số để chấp nhận String?
        viewModelScope.launch {
            val allCards = if (topicId.isNullOrEmpty()) {
                // 2. Nếu topicId là null/rỗng, gọi hàm lấy TẤT CẢ thẻ
                repo.getAllCards()
            } else {
                // 3. Nếu có topicId, chỉ lấy thẻ của chủ đề đó
                repo.getFlashcardsByTopic(topicId)
            }
            // Trộn và lấy 10 thẻ ngẫu nhiên, hoặc toàn bộ nếu có ít hơn
            _cards.value = allCards.shuffled().take(10)
        }
    }

    /**
     * Ghi lại kết quả trả lời của người dùng.
     *
     * @param card Thẻ flashcard của câu hỏi.
     * @param isCorrect Người dùng trả lời đúng (true) hay sai (false).
     */
    fun submitAnswer(card: FlashcardEntity, isCorrect: Boolean) {
        viewModelScope.launch {
            // Logic tính toán điểm/level có thể được mở rộng sau này.
            // Ví dụ: dựa vào tiến độ cũ để tăng/giảm level.
            val newMasteryLevel = if (isCorrect) 1 else 0
            val newCorrectCountInRow = if (isCorrect) 1 else 0

            // Gọi hàm trong Repository để ghi lại tiến độ.
            // `card.id` đã là String, không cần .toString() nữa.
            repo.recordProgress(
                flashcardId = card.id,
                newMasteryLevel = newMasteryLevel,
                newCorrectCountInRow = newCorrectCountInRow
            )
        }
    }
}
