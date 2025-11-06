package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
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
import com.example.babiling.ui.theme.BabiLingTheme
import androidx.compose.runtime.collectAsState
// ===================================================================
// I. COMPONENT: LoginFormCard (Đã chuyển sang nhận AuthUiState)
// ===================================================================

@Composable
fun LoginFormCard(
    uiState: AuthUiState,
    onPhoneNumberChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.titleLarge.copy(color = DarkText),
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.phoneNumber, // Lấy giá trị từ ViewModel
                onValueChange = onPhoneNumberChange,
                label = { Text("Số điện thoại", style = MaterialTheme.typography.labelLarge) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                textStyle = MaterialTheme.typography.labelLarge.copy(color = DarkText),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PrimaryPink,
                    unfocusedIndicatorColor = Color.LightGray,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onLoginClick,
                // Kích hoạt nút khi không loading và số điện thoại đủ 10 số
                enabled = !uiState.isLoading && uiState.phoneNumber.length >= 10,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        "Đăng nhập",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick, // Gửi sự kiện Google
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.gg),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Đăng nhập với google",
                        color = DarkText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "Chưa có tài khoản? Đăng ký",
                    color = DarkText.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ===================================================================
// II. MÀN HÌNH CHÍNH (Route) - LoginScreen
// ===================================================================

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(), // SỬ DỤNG AUTHVIEWMODEL
    onNavigateToVerification: (phoneNumber: String) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // LOGIC ĐIỀU HƯỚNG CHUNG
    LaunchedEffect(uiState.needsOtpVerification, uiState.isLoginSuccessful, uiState.isGoogleLoginSuccessful) {
        if (uiState.needsOtpVerification) { // Chuyển sang màn OTP
            onNavigateToVerification(uiState.phoneNumber)
            viewModel.onOtpVerificationNavigated()
        } else if (uiState.isLoginSuccessful || uiState.isGoogleLoginSuccessful) { // Chuyển sang Home
            onNavigateToHome()
        }
    }

    // --- Bố cục UI chính ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPink)
    ) {
        // ... (Hình cung trắng trang trí, giữ nguyên) ...
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-200).dp, y = (-120).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        // ... (Các hình cung khác) ...

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
                Spacer(modifier = Modifier.height(60.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tiếng Anh thật dễ - Cùng bé học\nngay hôm nay!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))

                LoginFormCard(
                    uiState = uiState,
                    onPhoneNumberChange = viewModel::onPhoneNumberChange,
                    onLoginClick = viewModel::handleLogin, // Kích hoạt gửi OTP
                    onGoogleLoginClick = viewModel::handleGoogleLogin, // Kích hoạt Google Login
                    onRegisterClick = onNavigateToRegister
                )

                // Hiển thị thông báo lỗi
                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = AccentRed,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.kids),
                contentDescription = "Hình ảnh hai em bé đang học",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}