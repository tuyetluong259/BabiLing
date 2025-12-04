package com.example.babiling.ui.screens.choose

import android.widget.Toast // ✅ Cần import Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // ✅ Cần import LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import com.example.babiling.R
import com.example.babiling.Screen
import com.example.babiling.ui.theme.BalooThambiFamily
import com.example.babiling.ui.theme.BabiLingTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseLangScreen(
    onNavigateToChooseAge: () -> Unit = {}
) {
    // ✅ 1. Lấy Context để hiển thị Toast
    val context = LocalContext.current

    // ✅ 2. Định nghĩa hành động cho nút đang phát triển
    val onDevelopingClick = {
        Toast.makeText(context, "Chức năng Tiếng Nhật đang được phát triển!", Toast.LENGTH_SHORT).show()
    }

    val titleColor = Color(0xFF0D47A1)
    val titleColor2 = Color(0xFFE25939)
    val buttonColor = Color.White
    val buttonTextColor = Color(0xFFEF3349)
    val cardContainerColor = Color(0xFFC9ECF6)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        //BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.background_lang),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        //main
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Thêm padding ở đây
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1.0f))
            // ===================================

            Text(
                text = "Chọn ngôn ngữ học",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = titleColor2
            )
            Text(
                text = "Select your language",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = titleColor2
            )

            Spacer(modifier = Modifier.weight(0.8f))

            // choosen
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardContainerColor
                ),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ✅ TIẾNG NHẬT: Thay đổi onClick thành onDevelopingClick
                    LanguageCard(
                        imageRes = R.drawable.flag_japan,
                        text = "Tiếng Nhật",
                        onClick = onDevelopingClick
                    )
                    // TIẾNG ANH: Giữ nguyên, chuyển đến ChooseAgeScreen
                    LanguageCard(
                        imageRes = R.drawable.flag_my,
                        text = "Tiếng Anh",
                        onClick = onNavigateToChooseAge
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1.0f))
            // ============================================

            // NÚT BẮT ĐẦU
            // LƯU Ý: Nút "Bắt đầu" hiện đang gọi onNavigateToChooseAge.
            // Nếu không có ngôn ngữ nào được chọn, hành vi này có thể gây nhầm lẫn.
            // Trong luồng này, ta vẫn giữ nguyên chức năng này, nhưng trong thực tế,
            // bạn nên vô hiệu hóa nút "Bắt đầu" trừ khi một ngôn ngữ (vd: Tiếng Anh) đã được chọn.
            Button(
                onClick = onNavigateToChooseAge,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                )
            ) {
                Text(
                    text = "Bắt đầu",
                    fontFamily = BalooThambiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = buttonTextColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Hàm LanguageCard và Preview giữ nguyên
@Composable
fun LanguageCard(
    imageRes: Int,
    text: String,
    onClick: () -> Unit
) {
    val cardBorderColor = Color(0xFFB3E5FC)

    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, cardBorderColor)

    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = text,
                modifier = Modifier
                    .size(width = 90.dp, height = 60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = Color.Black
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFFF0ECEC),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF5E3F4E)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseLangScreenPreview() {
    BabiLingTheme {
        val fakeNavController = rememberNavController()
        ChooseLangScreen()
    }
}