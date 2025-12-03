package com.example.babiling.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Hàm này chứa các vòng tròn trang trí cho màn hình Login và Register.
 * Bỏ `private` để có thể dùng chung ở cả hai nơi.
 */
@Composable
fun DecorativeCircles() {
    // Vòng tròn lớn ở góc trên bên trái
    Box(
        modifier = Modifier
            .offset(x = (-80).dp, y = (-80).dp)
            .size(200.dp)
            .background(Color.White, CircleShape)
    )
    // Vòng tròn nhỏ ở góc dưới bên phải
    Box(
        modifier = Modifier
            .offset(x = 280.dp, y = 650.dp) // Điều chỉnh vị trí nếu cần
            .size(150.dp)
            .background(Color.White, CircleShape)
    )
}

