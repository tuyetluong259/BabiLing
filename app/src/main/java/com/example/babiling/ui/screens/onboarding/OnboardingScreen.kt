package com.example.babiling.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.Screen
import com.example.babiling.ui.theme.BalooThambi2Family
import com.example.babiling.ui.theme.BabiLingTheme
import androidx.compose.ui.text.font.FontStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { 3 })
    // Màu sắc cho nút "Bắt đầu"
    val startButtonColor = Color(0xFFEF3349) // Màu đỏ/hồng tươi
    val indicatorActiveColor = Color(0xFF0D47A1) // Màu xanh đậm cho dấu chấm active
    val indicatorInactiveColor = Color(0xFFE0E0E0) // Màu xám nhạt cho dấu chấm inactive
    val skipTextColor = Color(0xFF28BAEE) // Màu xanh nhạt cho nút Skip

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> Page1Content()
                1 -> Page2Content()
                2 -> Page3Content()
            }
        }

        // --- Nút Skip ---
        TextButton(
            onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "Skip",
                color = skipTextColor,
                fontSize = 16.sp,
                fontFamily = BalooThambi2Family
            )
        }

        // --- Dấu chấm (Pager Indicator) và Nút "Bắt đầu" ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Dấu chấm - CHỈ HIỂN THỊ NẾU KHÔNG PHẢI TRANG CUỐI (currentPage != 2)
            if (pagerState.currentPage != 2) {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) indicatorActiveColor else indicatorInactiveColor
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .background(color, shape = CircleShape)
                                .size(12.dp)
                        )
                    }
                }
            }

            // Nút "Bắt đầu" - Chỉ hiển thị ở trang cuối cùng (index 2)
            if (pagerState.currentPage == 2) {
                // Sử dụng Spacer để tạo khoảng cách thống nhất với vị trí của dấu chấm ở các trang trước
                // Nếu dấu chấm bị ẩn, ta cần spacer để đẩy nút Bắt đầu lên một chút
                Spacer(modifier = Modifier.height(24.dp))

                StartButton(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    buttonColor = startButtonColor
                )
            }
        }
    }
}

// Composable cho nút "Bắt đầu"
@Composable
fun StartButton(onClick: () -> Unit, buttonColor: Color) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp) // Căn giữa và thêm padding
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = CircleShape
    ) {
        Text(
            text = "BẮT ĐẦU",
            fontFamily = BalooThambi2Family,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
// ------------------------------------------------------------------
// Các hàm Page1Content(), Page2Content(), Page3Content() và Preview giữ nguyên
// ------------------------------------------------------------------

@Composable
fun Page1Content() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(110.dp))

            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )) {
                        append("Khóa tiếng Anh nền\ntảng chuẩn cho\n")
                    }
                    withStyle(style = SpanStyle(
                        color = Color(0xFFEF3349),
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )) {
                        append("trẻ 3 - 10 tuổi")
                    }
                },
                fontFamily = BalooThambi2Family,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.page1),
                contentDescription = null,
                modifier = Modifier.size(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        Image(
            painter = painterResource(id = R.drawable.decor8),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp)
                .size(180.dp)
        )
    }
}
@Composable
fun Page2Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Image(
            painter = painterResource(id = R.drawable.page2),
            contentDescription = null,
            modifier = Modifier.size(400.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Vừa chơi vừa học",
            fontFamily = BalooThambi2Family,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF14A7DB),
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hình thành niềm yêu thích và hứng thú với học tập.",
            fontFamily = BalooThambi2Family,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color(0xFF737B82)
        )
    }
}

@Composable
fun Page3Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(112.dp))
        Image(
            painter = painterResource(id = R.drawable.page3),
            contentDescription = null,
            modifier = Modifier.size(400.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Lộ trình học theo từng cấp độ",
            fontFamily = BalooThambi2Family,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF14A7DB),
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nội dung bài học được sắp xếp\n  bài bản theo độ tuổi",
            fontFamily = BalooThambi2Family,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color(0xFF737B82)
        )
    }
}

// === PREVIEW ===

@Preview(showBackground = true, name = "Onboarding Screen (Full)")
@Composable
fun OnboardingScreenPreview() {
    BabiLingTheme {
        val fakeNavController = rememberNavController()
        OnboardingScreen(navController = fakeNavController)
    }
}

@Preview(showBackground = true, name = "Page 1 Content (Đã sửa)")
@Composable
fun Page1Preview() {
    BabiLingTheme {
        Page1Content()
    }
}

@Preview(showBackground = true, name = "Page 2 Content")
@Composable
fun Page2Preview() {
    BabiLingTheme {
        Page2Content()
    }
}

@Preview(showBackground = true, name = "Page 3 Content - Có nút Bắt đầu")
@Composable
fun Page3Preview() {
    BabiLingTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Page3Content()
            Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống dưới cùng
            // Dấu chấm đã bị xóa, chỉ còn nút BẮT ĐẦU
            Spacer(modifier = Modifier.height(24.dp))
            StartButton(onClick = {}, buttonColor = Color(0xFFEF3349))
            Spacer(modifier = Modifier.height(32.dp)) // Padding dưới cùng
        }
    }
}