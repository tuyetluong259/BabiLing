package com.example.babiling.ui.screens.topic.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
// ✨ KHÔNG CẦN import UserProgressEntity ở đây nữa
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repo: FlashcardRepository = ServiceLocator.provideRepository()) : ViewModel() {

    private val _cards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val cards: StateFlow<List<FlashcardEntity>> = _cards

    fun load(topicId: String) {
        viewModelScope.launch {
            val all = repo.getFlashcardsByTopic(topicId)
            // Lấy 10 thẻ ngẫu nhiên để làm quiz, hoặc toàn bộ nếu có ít hơn 10
            _cards.value = all.shuffled().take(10)
        }
    }

    /**
     * ✨ HÀM QUAN TRỌNG ĐÃ ĐƯỢC HOÀN THIỆN ✨
     * Hàm này được gọi khi người dùng trả lời một câu hỏi trong quiz.
     *
     * @param card Thẻ flashcard của câu hỏi.
     * @param isCorrect Người dùng trả lời đúng (true) hay sai (false).
     */
    fun submitAnswer(card: FlashcardEntity, isCorrect: Boolean) {
        viewModelScope.launch {
            // Logic tính toán tiến độ tương tự như trong LearnViewModel.
            // Bạn có thể làm nó phức tạp hơn sau này.
            // Ví dụ:
            // - Nếu trả lời đúng, tăng masteryLevel lên 1 (tối đa là 5).
            // - Nếu trả lời sai, giảm masteryLevel về 1 (hoặc 0).

            // TODO: Lấy tiến độ cũ từ Room để tính toán level mới (nếu cần)
            // val oldProgress = repo.getProgressForCard(card.id.toString()) // Cần thêm hàm này vào repo nếu muốn

            val newMasteryLevel = if (isCorrect) {
                // (oldProgress?.masteryLevel ?: 0) + 1
                1 // Tạm thời để là 1
            } else {
                0 // Nếu sai thì về 0
            }.coerceIn(0, 5) // Giới hạn level trong khoảng 0-5

            val newCorrectCountInRow = if (isCorrect) {
                // (oldProgress?.correctCountInRow ?: 0) + 1
                1 // Tạm thời để là 1
            } else {
                0 // Nếu sai thì reset về 0
            }

            // Gọi hàm MỚI và ĐÚNG trong Repository để ghi lại tiến độ
            repo.recordProgress(
                flashcardId = card.id.toString(), // Chuyển đổi Int thành String
                newMasteryLevel = newMasteryLevel,
                newCorrectCountInRow = newCorrectCountInRow
            )

            // Ở màn Quiz, có thể bạn không muốn tự động chuyển câu hỏi,
            // mà chờ người dùng nhấn nút "Tiếp theo".
        }
    }
}
