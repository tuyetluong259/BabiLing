package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.R
import com.example.babiling.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Điều hướng khi Google login thành công
    LaunchedEffect(uiState.isGoogleLoginSuccessful) {
        if (uiState.isGoogleLoginSuccessful) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bglogin)
    ) {

        // ================================
        //   CÁC HÌNH TRÒN TRANG TRÍ
        // ================================
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-200).dp, y = (-120).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (120).dp, y = (-150).dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-150).dp, y = (80).dp)
                .size(240.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (60).dp, y = (100).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        // ================================
        //   NỘI DUNG CHÍNH
        // ================================
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tiếng Anh thật dễ - Cùng bé học\nngay hôm nay!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.kids),
                contentDescription = "Kids Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Nút đăng nhập Google CHUYỂN XUỐNG DƯỚI
            Button(
                onClick = viewModel::handleGoogleLogin,
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Image(
                        painter = painterResource(id = R.drawable.gg),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(64.dp))

                    Text(
                        text = "Đăng nhập với Google",
                        color = DarkText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Loading
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(color = Color.White)
            }

            // Lỗi nếu có
            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = AccentRed,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BabiLingTheme {
        LoginScreen()
    }
}
