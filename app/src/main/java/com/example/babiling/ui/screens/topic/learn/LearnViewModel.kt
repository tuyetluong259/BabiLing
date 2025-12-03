package com.example.babiling.ui.screens.topic.learn

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.UserProgressEntity // Quan trọng: import để sử dụng
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date // Quan trọng: import để sử dụng

/**
 * Factory này "dạy" cho hệ thống cách tạo ra LearnViewModel
 * vì nó có các tham số đặc biệt trong constructor.
 * Đây là một phần bắt buộc phải có để ứng dụng không bị crash.
 */
/**
 * ViewModel cho màn hình LearnScreen, quản lý logic học theo từng bài.
 */
class LearnViewModel(
    private val repo: FlashcardRepository,
    private val topicId: String,
    private val lessonNumber: Int
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --- StateFlows công khai cho UI quan sát ---
    private val _cards = MutableStateFlow<List<FlashcardEntity>>(emptyList())
    val cards = _cards.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index = _index.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    init {
        Log.d("BabiLing_Debug", "[LearnViewModel] Được khởi tạo với topicId='$topicId', lessonNumber=$lessonNumber")
        loadData()
    }

    private fun loadData() {
        _cards.value = emptyList()
        _index.value = 0
        _isFinished.value = false

        viewModelScope.launch {
            try {
                val result = repo.getFlashcardsForLesson(topicId, lessonNumber)
                Log.d("BabiLing_Debug", "[LearnViewModel] Repository trả về ${result.size} thẻ.")
                _cards.value = result
            } catch (e: Exception) {
                Log.e("BabiLing_Debug", "[LearnViewModel] LỖI khi lấy thẻ từ repo theo bài học!", e)
            }
        }
    }

    fun next() {
        if (_index.value < _cards.value.size - 1) {
            _index.value++
        } else {
            Log.d("BabiLing_Debug", "[LearnViewModel] Đã đến thẻ cuối cùng. Bắt đầu ghi nhận tiến độ.")
            recordLessonCompletionAndSync()
        }
    }

    /**
     * Ghi nhận việc hoàn thành bài học vào Room VÀ đánh dấu chúng là cần được đồng bộ.
     */
    private fun recordLessonCompletionAndSync() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.w("BabiLing_Learn", "Người dùng chưa đăng nhập, không thể ghi tiến độ. Chỉ kết thúc bài học.")
                _isFinished.value = true
                return@launch
            }

            Log.d("BabiLing_Learn", "Chuẩn bị ghi nhận tiến độ cho '${_cards.value.size}' thẻ vào Room DB.")
            try {
                // 1. Tạo danh sách tiến độ mới với `synced = false`
                val progressListToUpsert = _cards.value.map { card ->
                    UserProgressEntity(
                        userId = userId,
                        flashcardId = card.id,
                        topicId = card.topicId,
                        masteryLevel = 1, // Đặt mức thành thạo ban đầu là 1
                        correctCountInRow = 1,
                        lastReviewed = Date(),
                        synced = false // ✨ ĐÁNH DẤU LÀ "CẦN ĐỒNG BỘ" ✨
                    )
                }

                // 2. Ghi đè tiến độ vào Room bằng hàm upsert (cập nhật nếu đã có, chèn nếu chưa)
                repo.upsertMultipleProgress(progressListToUpsert)
                Log.d("BabiLing_Learn", "Ghi tiến độ hàng loạt vào Room thành công.")

                // 3. KÍCH HOẠT ĐỒNG BỘ LÊN FIREBASE
                Log.d("BabiLing_Sync", "Bắt đầu quá trình đồng bộ (sync up) sau khi học xong...")
                repo.syncProgressUp()
                Log.d("BabiLing_Sync", "Đồng bộ (sync up) hoàn tất.")

            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "LỖI khi ghi nhận hoặc đồng bộ tiến độ!", e)
            } finally {
                // 4. Báo hiệu cho UI biết là bài học đã kết thúc
                _isFinished.value = true
            }
        }
    }
}
