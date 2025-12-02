package com.example.babiling.ui.screens.progress

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.babiling.ServiceLocator

/**
 * Factory này chịu trách nhiệm tạo ra một instance của ProgressViewModel.
 * Nó lấy Context, sử dụng ServiceLocator để lấy Repository,
 * và sau đó "tiêm" (inject) Repository đó vào ProgressViewModel.
 */
class ProgressViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem có đúng là đang yêu cầu tạo ProgressViewModel không
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            // Lấy Repository từ ServiceLocator bằng cách truyền Context vào
            // Sử dụng applicationContext để tránh rò rỉ bộ nhớ (memory leak)
            val repository = ServiceLocator.provideRepository(context.applicationContext)

            // Tạo và trả về một instance của ProgressViewModel với Repository đã được cung cấp
            return ProgressViewModel(repository) as T
        }
        // Nếu không đúng, ném ra một lỗi để báo cho lập trình viên biết
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
