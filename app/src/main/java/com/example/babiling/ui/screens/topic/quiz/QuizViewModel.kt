package com.example.babiling.ui.screens.topic.quiz

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class QuestionType {
    MULTIPLE_CHOICE,
    FILL_IN_WORD
}

sealed class QuizQuestion(val type: QuestionType, val flashcard: FlashcardEntity) {
    data class MultipleChoice(
        val card: FlashcardEntity,
        val options: List<FlashcardEntity>
    ) : QuizQuestion(QuestionType.MULTIPLE_CHOICE, card)

    data class FillInWord(
        val card: FlashcardEntity,
        val scrambledChars: List<Char>
    ) : QuizQuestion(QuestionType.FILL_IN_WORD, card)
}

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val isLoading: Boolean = true
)

open class QuizViewModel(
    application: Application,
    private val repo: FlashcardRepository = ServiceLocator.provideRepository(application)) : AndroidViewModel(application)
{

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var player: ExoPlayer? = ExoPlayer.Builder(getApplication()).build()


    open fun load(topicId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val allCards = if (topicId.isNullOrEmpty()) {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Đang tải TẤT CẢ thẻ.")
                    repo.getAllCards()
                } else {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Đang tải thẻ cho chủ đề: $topicId.")
                    repo.getFlashcardsByTopic(topicId)
                }.shuffled().take(10)

                if (allCards.isEmpty()) {
                    Log.d("BabiLing_Debug", "[QuizViewModel] Không tìm thấy thẻ nào.")
                    _uiState.update { it.copy(questions = emptyList(), isLoading = false) }
                    return@launch
                }

                val quizQuestions = allCards.map { card ->
                    if (Random.nextBoolean() && allCards.size >= 4) {
                        val wrongOptions = allCards
                            .filter { it.id != card.id }
                            .shuffled()
                            .take(3)
                        val options = (wrongOptions + card).shuffled()
                        QuizQuestion.MultipleChoice(card, options)
                    } else {
                        val scrambled = card.name.toMutableList().shuffled()
                        QuizQuestion.FillInWord(card, scrambled)
                    }
                }

                _uiState.update {
                    it.copy(
                        questions = quizQuestions,
                        isLoading = false
                    )
                }
                Log.d("BabiLing_Debug", "[QuizViewModel] Đã tạo thành công ${quizQuestions.size} câu hỏi hỗn hợp.")

            } catch (e: Exception) {
                Log.e("BabiLing_Debug", "[QuizViewModel] LỖI khi tạo câu hỏi quiz!", e)
                _uiState.update { it.copy(questions = emptyList(), isLoading = false) }
            }
        }
    }

    open fun playSound(soundPath: String) {
        if (soundPath.isBlank()) {
            Log.w("BabiLing_Sound", "Sound path is empty, cannot play sound.")
            return
        }
        try {
            val assetPath = "file:///android_asset/$soundPath"
            val mediaItem = MediaItem.fromUri(Uri.parse(assetPath))
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
            Log.d("BabiLing_Sound", "Playing sound from: $assetPath")
        } catch (e: Exception) {
            Log.e("BabiLing_Sound", "Error playing sound from path: $soundPath", e)
        }
    }


    fun submitAnswer(card: FlashcardEntity, isCorrect: Boolean) {
        Log.d("BabiLing_Quiz", "Người dùng trả lời câu hỏi '${card.name}': ${if (isCorrect) "ĐÚNG" else "SAI"}")
        viewModelScope.launch {
            val newMasteryLevel = if (isCorrect) 1 else 0
            val newCorrectCountInRow = if (isCorrect) 1 else 0

            // ✨ SỬA LỖI Ở ĐÂY: Đổi tên hàm cho đúng với Repository ✨
            repo.recordSingleProgress(
                flashcardId = card.id,
                topicId = card.topicId,
                newMasteryLevel = newMasteryLevel,
                newCorrectCountInRow = newCorrectCountInRow
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
        Log.d("BabiLing_Sound", "ExoPlayer has been released.")
    }
}
