package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField // ✅ Sửa: Import chính xác
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.ui.theme.* // Giữ lại import này


// ===================================================================
// I. HÀM COMPOSABLE BỘ PHẬN: Ô NHẬP OTP
// ===================================================================

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    modifier: Modifier = Modifier
) {
    // BasicTextField xử lý input ẩn và hiển thị các ô số
    BasicTextField(
        value = value,
        onValueChange = {
            // Chỉ cho phép nhập số và giới hạn chiều dài
            if (it.length <= length && it.all { char -> char.isDigit() }) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(length) { index ->
                    val char = value.getOrNull(index)
                    val isFocused = index == value.length && value.length != length

                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isFocused) DeepBlue else Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char != null) {
                            Text(
                                text = char.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = DarkText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

// ===================================================================
// II. MÀN HÌNH HOÀN CHỈNH
// ===================================================================

@OptIn(ExperimentalMaterial3Api::class) // ✅ Thêm OptIn cho TopAppBar
@Composable
fun VerificationScreen( // Đổi tên để rõ ràng hơn
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onResendClick: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Không có tiêu đề */ },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = DarkText
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF0F0F0)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Tiêu đề và mô tả
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Xác minh",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = DeepBlue,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Nhập mã xác minh gồm 6 chữ số đã được gửi đến bạn.",
                    style = MaterialTheme.typography.bodyLarge.copy(color = LightText),
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }


            // 3. Ô nhập OTP
            OtpInputField(
                value = otpValue,
                onValueChange = { otpValue = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 4. Nút Xác minh
            Button(
                onClick = { onVerifyClick(otpValue) },
                colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = otpValue.length == 6
            ) {
                Text(
                    "Xác minh",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 5. Nút Gửi lại mã
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Không nhận được mã? ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = LightText)
                )
                TextButton(
                    onClick = onResendClick,
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "Gửi lại",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DeepBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}


// ===================================================================
// III. PREVIEW
// ===================================================================

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun VerificationScreenPreview() {
    BabiLingTheme {
        VerificationScreen(
            onBackClick = {},
            onVerifyClick = {},
            onResendClick = {}
        )
    }
}
