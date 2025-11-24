package com.example.babiling.ui.screens.topic.study

// Định nghĩa data class FlashcardItem MỚI và DUY NHẤT
data class FlashcardItem(
    val name: String,     // Tên/từ vựng tiếng Anh
    val nameVi: String,   // Dịch nghĩa tiếng Việt
    val imagePath: String, // Đường dẫn đến ảnh trong thư mục assets
    val soundPath: String
)