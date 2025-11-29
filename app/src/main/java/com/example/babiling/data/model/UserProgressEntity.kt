package com.example.babiling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    // ID của thẻ học, ví dụ "animal_001". Dùng làm khóa chính.
    @PrimaryKey val flashcardId: String,

    // ID của người dùng sở hữu tiến độ này.
    val userId: String,

    // Mức độ thành thạo, ví dụ từ 0 (chưa biết) đến 5 (đã thành thạo).
    val masteryLevel: Int = 0,

    // Thời gian học gần nhất.
    val lastReviewed: Date = Date(),

    // Số lần trả lời đúng liên tiếp. Hữu ích cho thuật toán ôn tập.
    val correctCountInRow: Int = 0,

    // Cờ này CHỈ TỒN TẠI TRONG ROOM. Không được gửi lên Firebase.
    // true: dữ liệu đã được đồng bộ. false: cần được đẩy lên server.
    val isSynced: Boolean = false
)
