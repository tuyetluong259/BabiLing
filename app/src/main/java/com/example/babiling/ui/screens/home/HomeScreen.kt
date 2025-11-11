package com.example.babiling.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    // Sử dụng Box để căn giữa nội dung một cách đơn giản
    Box(
        modifier = Modifier.fillMaxSize(), // Chiếm toàn bộ không gian màn hình
        contentAlignment = Alignment.Center // Căn chỉnh mọi thứ bên trong vào giữa
    ) {
        Text(text = "Đây là Trang chủ (HomeScreen)")
    }
}

// Thêm preview để xem trước giao diện mà không cần chạy cả ứng dụng
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
