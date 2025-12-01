package com.example.babiling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey
    val id: String,

    val topicId: String,      // Chủ đề (animals, colors...)
    val name: String,         // Từ tiếng Anh
    val nameVi: String,       // Nghĩa tiếng Việt
    val imagePath: String,    // assets/images/xxx.png
    val soundPath: String,    // assets/sounds/xxx.mp3

    // ✨ BƯỚC 1: THÊM TRƯỜNG MỚI ĐỂ PHÂN CHIA BÀI HỌC ✨
    /**
     * Số thứ tự của bài học mà thẻ này thuộc về (ví dụ: 1, 2, 3...).
     * Giúp chia một chủ đề lớn thành nhiều bài học nhỏ.
     */
    val lessonNumber: Int,

    // --- Các trường lưu tiến độ học ---

    /**
     * Mức độ thành thạo của thẻ (ví dụ: 0 = chưa học, 1 = đã biết, ...).
     * Giá trị mặc định là 0 khi một thẻ mới được tạo.
     */
    val masteryLevel: Int = 0,

    /**
     * Số lần trả lời đúng liên tiếp cho thẻ này.
     * Giá trị mặc định là 0.
     */
    val correctCountInRow: Int = 0
)
