package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.babiling.R
import com.example.babiling.ui.theme.* import com.example.babiling.ui.theme.BabiLingTheme

// ===================================================================
// I. HÀM COMPOSABLE BỘ PHẬN (Components)
// ===================================================================

@Composable
fun RegisterFormCard(
    phoneNumber: String,
    password: String,
    onPhoneNumberChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    // THAY ĐỔI: Thêm màu nền trắng cho Card
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
                text = "Đăng ký",
                style = MaterialTheme.typography.titleLarge,
                color = DarkText,
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Input Số điện thoại
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Số điện thoại", style = MaterialTheme.typography.labelLarge) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                textStyle = MaterialTheme.typography.labelLarge.copy(color = DarkText),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = bglogin,
                    unfocusedIndicatorColor = Color.LightGray,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Input Mật khẩu
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Mật khẩu", style = MaterialTheme.typography.labelLarge) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                visualTransformation = PasswordVisualTransformation(), // Ẩn mật khẩu
                textStyle = MaterialTheme.typography.labelLarge.copy(color = DarkText),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = bglogin,
                    unfocusedIndicatorColor = Color.LightGray,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 3. Nút Đăng ký (Thay thế cho Đăng nhập)
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed), // Giả định dùng AccentRed cho nút chính
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Đăng ký", // TEXT ĐĂNG KÝ
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 4. Link Đã có tài khoản? Đăng nhập
            TextButton(onClick = onLoginClick) {
                Text(
                    text = "Đã có tài khoản? Đăng nhập",
                    color = DarkText.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ===================================================================
// II. MÀN HÌNH HOÀN CHỈNH (Screen)
// ===================================================================

@Composable
fun RegisterScreen() {
    // Trạng thái cục bộ cho màn hình đăng ký
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // THAY ĐỔI: Cấu trúc lại nền và các hình cung (Background tương tự Login)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPink) // Giả định dùng PrimaryPink cho nền
    ) {
        // Hình cung trắng ở trên cùng bên trái
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-200).dp, y = (-120).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        // Hình cung trắng ở dưới cùng bên phải
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (250).dp, y = (-30).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        // Hình cung trắng ở dưới cùng bên trái (Thêm vào để khớp với thiết kế)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-200).dp, y = (80).dp)
                .size(250.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        // Nội dung chính của màn hình
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phần nội dung trên (Logo, Slogan, Form)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(60.dp)) // Đẩy logo xuống

                Image(
                    painter = painterResource(id = R.drawable.logo), // Giả định R.drawable.logo
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tiếng Anh thật dễ - Cùng bé học\nngay hôm nay!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White // Chữ màu trắng
                )
                Spacer(modifier = Modifier.height(20.dp))

                RegisterFormCard(
                    phoneNumber = phoneNumber,
                    password = password,
                    onPhoneNumberChange = { phoneNumber = it },
                    onPasswordChange = { password = it },
                    onRegisterClick = { /* TODO: Xử lý đăng ký */ },
                    onLoginClick = { /* TODO: Chuyển sang Đăng nhập */ }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Phần ảnh kids ở dưới cùng
            Image(
                painter = painterResource(id = R.drawable.kids), // Giả định R.drawable.kids
                contentDescription = "Hình ảnh hai em bé đang học",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}


// ===================================================================
// III. PREVIEW
// ===================================================================

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun RegisterScreenPreview() {
    BabiLingTheme {
        RegisterScreen()
    }
}