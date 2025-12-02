package com.example.babiling.ui.screens.topic.learn

import android.content.Context // ✨ 1. THÊM IMPORT NÀY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.babiling.ServiceLocator // ✨ 2. THÊM IMPORT NÀY
import com.example.babiling.data.repository.FlashcardRepository // ✨ 3. THÊM IMPORT NÀY

/**
 * Factory để tạo ra một instance của LessonViewModel.
 * ✨ HOÀN THIỆN: Lấy Context, dùng nó để lấy Repository, và "tiêm" vào ViewModel.
 */
// ✨ 4. THÊM `context` VÀO HÀM KHỞI TẠO CỦA FACTORY
class LessonViewModelFactory(
    private val context: Context,
    private val topicId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            // ✨ 5. LẤY REPOSITORY TỪ SERVICE LOCATOR
            val repository = ServiceLocator.provideRepository(context.applicationContext)

            // ✨ 6. TẠO VIEWMODEL VỚI ĐẦY ĐỦ 2 THAM SỐ
            return LessonViewModel(topicId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for LessonViewModelFactory")
    }
}
