package com.example.babiling.ui.screens.topic.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// ✨ BƯỚC 1: ĐỊNH NGHĨA CẤU TRÚC CHO CÁC LOẠI CÂU HỎI KHÁC NHAU

/**
 * Định nghĩa các loại câu hỏi có thể có trong quiz.
 */
enum class QuestionType {
    MULTIPLE_CHOICE, // Trắc nghiệm 4 lựa chọn
    FILL_IN_WORD     // Điền từ (sắp xếp chữ cái)
}

/**
 * Dùng `sealed class` để có thể chứa nhiều loại câu hỏi trong cùng một danh sách.
 * Mỗi câu hỏi đều dựa trên một `FlashcardEntity`.
 */
sealed class QuizQuestion(val type: QuestionType, val flashcard: FlashcardEntity) {
    /** Câu hỏi trắc nghiệm */
    data class MultipleChoice(
        val card: FlashcardEntity,
        val options: List<String>
    ) : QuizQuestion(QuestionType.MULTIPLE_CHOICE, card)

    /** Câu hỏi điền từ */
    data class FillInWord(
        val card: FlashcardEntity,
        val scrambledChars: List<Char>
    ) : QuizQuestion(QuestionType.FILL_IN_WORD, card)
}


// ✨ BƯỚC 2: CẬP NHẬT VIEWMODEL ĐỂ TẠO CÂU HỎI HỖN HỢP
class QuizViewModel(private val repo: FlashcardRepository = ServiceLocator.provideRepository()) : ViewModel() {

    // State mới chứa danh sách câu hỏi hỗn hợp, thay thế cho `_cards` cũ.
    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions = _questions.asStateFlow()

    /**
     * Tải và tạo một danh sách câu hỏi quiz hỗn hợp.
     *
     * @param topicId ID của chủ đề. Nếu là null hoặc rỗng, sẽ tải tất cả các thẻ để ôn tập.
     */
    fun load(topicId: String?) {
        // Reset state mỗi khi bắt đầu tải
        _questions.value = emptyList()

        viewModelScope.launch {
            try {
                // Lấy 10 thẻ ngẫu nhiên từ database
                val allCards = if (topicId.isNullOrEmpty()) {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Đang tải TẤT CẢ thẻ.")
                    repo.getAllCards()
                } else {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Đang tải thẻ cho chủ đề: $topicId.")
                    repo.getFlashcardsByTopic(topicId)
                }.shuffled().take(10)

                if (allCards.isEmpty()) {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Không tìm thấy thẻ nào.")
                    return@launch
                }

                // TẠO RA DANH SÁCH CÂU HỎI HỖN HỢP MỘT CÁCH NGẪU NHIÊN
                val quizQuestions = allCards.map { card ->
                    // Dùng Random.nextBoolean() để ngẫu nhiên chọn loại câu hỏi (tỉ lệ 50/50)
                    if (Random.nextBoolean()) {
                        // Tạo câu hỏi Trắc nghiệm
                        val wrongAnswers = allCards
                            .filter { it.id != card.id }
                            .shuffled()
                            .take(3)
                            .map { it.name }
                        val options = (wrongAnswers + card.name).shuffled()
                        QuizQuestion.MultipleChoice(card, options)
                    } else {
                        // Tạo câu hỏi Điền từ
                        val scrambled = card.name.toMutableList().shuffled()
                        QuizQuestion.FillInWord(card, scrambled)
                    }
                }

                _questions.value = quizQuestions
                Log.d("BabiLing_Debug", "[QuizViewModel] Đã tạo thành công ${_questions.value.size} câu hỏi hỗn hợp.")

            } catch (e: Exception) {
                Log.e("BabiLing_Debug", "[QuizViewModel] LỖI khi tạo câu hỏi quiz!", e)
                _questions.value = emptyList() // Đảm bảo state rỗng nếu có lỗi
            }
        }
    }

    /**
     * Ghi lại kết quả trả lời của người dùng.
     *
     * @param card Thẻ flashcard của câu hỏi.
     * @param isCorrect Người dùng trả lời đúng (true) hay sai (false).
     */
    fun submitAnswer(card: FlashcardEntity, isCorrect: Boolean) {
        Log.d("BabiLing_Quiz", "Người dùng trả lời câu hỏi '${card.name}': ${if (isCorrect) "ĐÚNG" else "SAI"}")
        viewModelScope.launch {
            val newMasteryLevel = if (isCorrect) 1 else 0
            val newCorrectCountInRow = if (isCorrect) 1 else 0
            repo.recordProgress(
                flashcardId = card.id,
                newMasteryLevel = newMasteryLevel,
                newCorrectCountInRow = newCorrectCountInRow
            )
        }
    }
}
