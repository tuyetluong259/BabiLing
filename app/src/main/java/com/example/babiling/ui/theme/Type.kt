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
val BalooThambi2Family = FontFamily(
    Font(R.font.baloothambi2_regular, FontWeight.Normal),
    Font(R.font.baloothambi2_medium, FontWeight.Medium),
    Font(R.font.baloothambi2_bold, FontWeight.Bold),
    Font(R.font.baloothambi2_extrabold, FontWeight.ExtraBold),
    Font(R.font.baloothambi2_semibold, FontWeight.SemiBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    ),
    // ... (các TextStyle khác)

    // Em có thể thêm một Style mới sử dụng font Baloo Thambi ở đây nếu muốn
    headlineMedium = TextStyle(
        fontFamily = BalooThambi2Family, // Áp dụng font Baloo Thambi
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle (
        fontFamily = BalooThambi2Family,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
// Dùng cho tiêu đề phụ
    titleMedium = TextStyle(
        fontFamily = BalooThambi2Family,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
// Dùng cho văn bản chính
    bodyLarge = TextStyle(
        fontFamily = BalooThambi2Family,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
// Dùng cho văn bản nhỏ
    bodySmall = TextStyle(
        fontFamily = BalooThambi2Family,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Dùng cho các nút (VD: "Đăng nhập")
    labelLarge = TextStyle(
        fontFamily = BalooThambi2Family,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

)