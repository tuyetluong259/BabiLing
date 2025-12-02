package com.example.babiling.data.model

import androidx.room.Embedded

data class TopicWithProgress(
    // @Embedded cho phép Room lồng các trường của TopicEntity vào đây
    @Embedded
    val topic: TopicEntity,

    // Trường này được tính toán bởi câu lệnh SQL của chúng ta
    val isCompleted: Boolean
)

