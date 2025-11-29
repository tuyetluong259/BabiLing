package com.example.babiling.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.Screen
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambi2Family

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = viewModel() // Khởi tạo ViewModel
) {
    // Lắng nghe giá trị của `nextScreen` từ ViewModel. Ban đầu nó là null.
    val nextScreen by splashViewModel.nextScreen.collectAsState()

    //chỉ chạy khi giá trị của `nextScreen` thay đổi (từ null sang một route cụ thể)
    LaunchedEffect(nextScreen) {
        // Chỉ điều hướng khi `nextScreen` có giá trị (không phải null)
        nextScreen?.let { route ->
            navController.navigate(route) {
                // Xóa SplashScreen khỏi chồng điều hướng để người dùng không thể quay lại nó
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        }
    }

    // Giao diện người dùng của bạn - giữ nguyên vì nó đã rất đẹp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "BabiLing Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Học tiếng thật dễ – Cùng bé học\nngay hôm nay!",
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFEF3349)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    BabiLingTheme {
        SplashScreen(navController = rememberNavController())
    }
}
