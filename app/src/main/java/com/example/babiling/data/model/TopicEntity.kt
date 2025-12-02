package com.example.babiling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Đại diện cho bảng 'topics' trong cơ sở dữ liệu Room.
 * Mỗi một Topic (chủ đề) như "Greetings", "Animals" sẽ là một hàng trong bảng này.
 */
@Entity(tableName = "topics")
data class TopicEntity(
    // id của chủ đề, ví dụ: "greetings"
    @PrimaryKey
    val id: String,

    // Tên hiển thị của chủ đề, ví dụ: "Chào hỏi"
    val name: String,

    // Mô tả ngắn về chủ đề
    val description: String,

    // Cột này rất quan trọng, dùng để lưu tổng số bài học (lessons) có trong chủ đề này.
    // Nó được dùng để so sánh và xác định xem chủ đề đã hoàn thành hay chưa.
    val lessonCount: Int
)
