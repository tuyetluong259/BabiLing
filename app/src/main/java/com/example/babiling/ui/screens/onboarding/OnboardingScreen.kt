package com.example.babiling.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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

        // Skip
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
                color = Color(0xFF28BAEE),
                fontSize = 16.sp,
                fontFamily = BalooThambi2Family
            )
        }

        // Dấu chấm
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF0D47A1) else Color(0xFFE0E0E0)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color, shape = CircleShape)
                        .size(12.dp)
                )
            }
        }
    }
}
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

@Preview(showBackground = true, name = "Page 3 Content")
@Composable
fun Page3Preview() {
    BabiLingTheme {
        Page3Content()
    }
}