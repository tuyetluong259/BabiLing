// app/src/main/java/com/example/babiling/ui/theme/Type.kt

package com.example.babiling.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.babiling.R // Import R của project

// Định nghĩa FontFamily cho Baloo Thambi
val BalooThambiFamily = FontFamily(
    Font(R.font.baloo_thambi_regular)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // ... (các TextStyle khác)

    // Em có thể thêm một Style mới sử dụng font Baloo Thambi ở đây nếu muốn
    headlineMedium = TextStyle(
        fontFamily = BalooThambiFamily, // Áp dụng font Baloo Thambi
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
)