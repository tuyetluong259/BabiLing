package com.example.babiling.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(// Bỏ hoàn toàn (autoGenerate = true)
    // Giờ đây Room sẽ hiểu rằng 'id' là một chuỗi String duy nhất do bạn cung cấp
    @PrimaryKey
    val id: String,

    val topicId: String,      // Chủ đề (animals, colors...)
    val name: String,         // Từ tiếng Anh
    val nameVi: String,       // Nghĩa tiếng Việt
    val imagePath: String,    // assets/images/xxx.png
    val soundPath: String     // assets/sounds/xxx.mp3
)
