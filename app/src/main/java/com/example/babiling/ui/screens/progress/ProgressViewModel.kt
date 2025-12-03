package com.example.babiling.ui.screens.progress

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.babiling.ServiceLocator
import com.example.babiling.data.model.TopicEntity
import com.example.babiling.data.repository.FlashcardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.* // Import đầy đủ Flow API
import kotlinx.coroutines.launch

// LỚP 1 & 2 (Giữ nguyên)
data class TopicProgressInfo(
    val topic: TopicEntity,
    val learnedCards: Int,
    val totalCards: Int
) {
    val progress: Float
        get() = if (totalCards > 0) learnedCards.toFloat() / totalCards.toFloat() else 0f
}

data class ProgressUiState(
    val topicsWithProgress: List<TopicProgressInfo> = emptyList(),
    val isLoading: Boolean = true
)

class ProgressViewModel(
    private val repo: FlashcardRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    // ✨ TẠO FLOW ĐỂ THEO DÕI SỰ THAY ĐỔI CỦA DỮ LIỆU TĨNH ✨
    // Biến này sẽ kích hoạt việc tải lại dữ liệu sau khi sync xong hoặc khi loadProgress() được gọi.
    private val refreshSignal = MutableStateFlow(Unit)

    init {
        // Lắng nghe tín hiệu làm mới và tự động tải lại tiến trình
        refreshSignal
            .onStart { emit(Unit) } // Kích hoạt lần đầu tiên khi ViewModel được tạo
            .flatMapLatest {
                // Nếu người dùng không đăng nhập, trả về Flow rỗng
                val userId = auth.currentUser?.uid ?: return@flatMapLatest flowOf(emptyList())

                // Thay vì gọi các hàm suspend tĩnh, tạo một Flow mới từ các nguồn:
                combine(
                    repo.getAllTopicsFlow(), // Giả sử repo có hàm Flow này
                    repo.getAllProgressFlow(userId) // Giả sử repo có hàm Flow này
                ) { allTopics, allUserProgress ->
                    // Logic tính toán chạy mỗi khi topics HOẶC progress thay đổi
                    allTopics.map { topic ->
                        val learnedCount = allUserProgress.count {
                            it.topicId == topic.id && it.masteryLevel > 0
                        }
                        val totalCount = repo.getCardCountForTopic(topic.id) // Vẫn dùng suspend tĩnh cho tổng thẻ

                        TopicProgressInfo(
                            topic = topic,
                            learnedCards = learnedCount,
                            totalCards = totalCount
                        )
                    }
                }
            }
            .onEach { progressInfoList ->
                // Cập nhật UI State với dữ liệu mới
                _uiState.update {
                    it.copy(isLoading = false, topicsWithProgress = progressInfoList)
                }
                Log.d("ProgressViewModel", "UI Đã Tự Động Cập Nhật với ${progressInfoList.size} chủ đề.")
            }
            .catch { e ->
                Log.e("ProgressViewModel", "Lỗi khi lắng nghe tiến trình!", e)
                _uiState.update { it.copy(isLoading = false) }
            }
            .launchIn(viewModelScope)

        // Vẫn gọi hàm sync để kéo dữ liệu mới nhất từ server về lần đầu
        syncData()
    }


    /**
     * Tách biệt logic đồng bộ ra khỏi init để có thể gọi lại từ bên ngoài.
     * Hàm này được gọi khi ViewModel khởi tạo hoặc khi cần làm mới thủ công (từ UI).
     */
    fun syncData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, topicsWithProgress = emptyList()) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // BƯỚC 1: ĐỒNG BỘ LÊN TRƯỚC
                Log.d("ProgressViewModel", "Bắt đầu đồng bộ dữ liệu LÊN (sync up)...")
                repo.syncProgressUp()

                // BƯỚC 2: ĐỒNG BỘ XUỐNG SAU (Nếu Room thay đổi, Flow ở trên sẽ tự chạy lại)
                Log.d("ProgressViewModel", "Bắt đầu đồng bộ dữ liệu XUỐNG (sync down)...")
                repo.syncProgressDown()

                // BƯỚC 3: Kích hoạt tín hiệu làm mới sau khi sync xong
                refreshSignal.tryEmit(Unit) // Buộc Flow chạy lại lần nữa

                Log.d("ProgressViewModel", "Đồng bộ hoàn tất.")

            } catch (e: Exception) {
                // Không đặt isLoading = false ở đây nếu Flow vẫn đang chạy
                Log.e("ProgressViewModel", "Lỗi khi đồng bộ dữ liệu!", e)
            }
        }
    }
}
