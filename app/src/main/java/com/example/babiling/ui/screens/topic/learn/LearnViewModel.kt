package com.example.babiling.ui.screens.topic.learn

import android.content.Context // ✨ THÊM IMPORT NÀY ✨
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider // ✨ THÊM IMPORT NÀY ✨
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator // ✨ THÊM IMPORT NÀY ✨
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ✨ BƯỚC 1: HOÀN THIỆN LẠI VIEWMODELFACTORY ✨
 * Lớp này có nhiệm vụ "dạy" cho hệ thống cách tạo ra LearnViewModel
 * vì nó có các tham số đặc biệt (repo, topicId, lessonNumber) trong constructor.
 */

/**
 * ViewModel cho màn hình LearnScreen, quản lý logic học theo từng bài.
 * ✨ PHIÊN BẢN HOÀN HẢO - Không cần sửa thêm ✨
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
                // Bạn đã sử dụng hàm getFlashcardsForLesson trong các file trước đó
                // nên tôi sẽ dùng lại nó cho nhất quán. Nếu bạn đã đổi tên hàm trong repo
                // thành getFlashcardsByLesson thì hãy sửa lại ở đây.
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
     * Ghi nhận việc hoàn thành bài học vào Room và đồng bộ lên Firebase.
     * Logic này đã rất tốt, không cần thay đổi.
     */
    private fun recordLessonCompletionAndSync() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.w("BabiLing_Learn", "Người dùng chưa đăng nhập, không thể ghi tiến độ. Chỉ kết thúc bài học.")
                _isFinished.value = true
                return@launch
            }

            Log.d("BabiLing_Learn", "Bắt đầu ghi nhận tiến độ cho '${_cards.value.size}' thẻ vào Room DB.")
            try {
                // 1. Ghi đè tiến độ cho TẤT CẢ các thẻ trong bài học này vào Room
                repo.recordMultipleProgress(_cards.value)
                Log.d("BabiLing_Learn", "Ghi tiến độ hàng loạt vào Room thành công.")

                // 2. KÍCH HOẠT ĐỒNG BỘ LÊN FIREBASE
                Log.d("BabiLing_Sync", "Bắt đầu quá trình đồng bộ (sync up) sau khi học xong...")
                repo.syncProgressUp()
                Log.d("BabiLing_Sync", "Đồng bộ (sync up) hoàn tất.")

            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "LỖI khi ghi nhận hoặc đồng bộ tiến độ!", e)
            } finally {
                // 3. Báo hiệu cho UI biết là bài học đã kết thúc
                _isFinished.value = true
            }
        }
    }
}
