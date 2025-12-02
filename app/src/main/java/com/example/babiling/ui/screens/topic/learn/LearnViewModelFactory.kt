package com.example.babiling.ui.screens.topic.learn

import android.content.Context // ✨ 1. THÊM IMPORT NÀY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.babiling.ServiceLocator // ✨ 2. THÊM IMPORT NÀY

/**
 * Factory này chịu trách nhiệm tạo ra một instance của LearnViewModel.
 *
 * Nó lấy Context, dùng nó để lấy Repository, và sau đó "tiêm" (inject)
 * Repository cùng với topicId và lessonNumber vào LearnViewModel.
 */
// ✨ 3. THÊM `context` VÀO HÀM KHỞI TẠO CỦA FACTORY
class LearnViewModelFactory(
    private val context: Context,
    private val topicId: String,
    private val lessonNumber: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
            // ✨ 4. LẤY REPOSITORY TỪ SERVICE LOCATOR
            val repository = ServiceLocator.provideRepository(context.applicationContext)

            // ✨ 5. TẠO VIEWMODEL VỚI ĐẦY ĐỦ 3 THAM SỐ
            return LearnViewModel(repository, topicId, lessonNumber) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
