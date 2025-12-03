package com.example.babiling.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility // ✨ THÊM IMPORT MỚI
import androidx.compose.material.icons.filled.VisibilityOff // ✨ THÊM IMPORT MỚI
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.*
import com.example.babiling.utils.DecorativeCircles

// Màu sắc từ các file khác của bạn
private val BackgroundPink = Color(0xFFEB7474)
private val LightGrayBackground = Color(0xFFF0F0F0)
private val DarkText = Color(0xFF333333)

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    // Lấy trạng thái từ ViewModel
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // State cho các trường nhập liệu
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State cho việc hiển thị mật khẩu
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Lắng nghe các thay đổi từ ViewModel để hiển thị thông báo và điều hướng
    LaunchedEffect(key1 = uiState) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
            if (uiState.isRegisterSuccessful) {
                authViewModel.resetAllFlags()
                onBackToLogin()
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
    ) {
        DecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Tạo tài khoản mới",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Text(
                        "Đăng ký",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = DarkText,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // TextField cho Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Tên đăng nhập",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                    // Bỏ fontWeight ở đây để dùng font từ theme
                                )
                            )
                        },
                        leadingIcon = { Icon(Icons.Filled.Person, "Tên đăng nhập", tint = Color.Gray) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGrayBackground,
                            unfocusedContainerColor = LightGrayBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = BackgroundPink
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = DarkText) // Thêm style cho text khi nhập
                    )

                    Spacer(Modifier.height(8.dp))

                    // TextField cho Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Mật khẩu", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray, fontSize = 16.sp)) },
                        leadingIcon = { Icon(Icons.Filled.Lock, "Mật khẩu", tint = Color.Gray) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        // ✨ SỬA LỖI ICON Ở ĐÂY ✨
                        trailingIcon = {
                            val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGrayBackground,
                            unfocusedContainerColor = LightGrayBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = BackgroundPink
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = DarkText) // Thêm style cho text khi nhập
                    )

                    Spacer(Modifier.height(8.dp))

                    // TextField cho Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Xác nhận mật khẩu", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray, fontSize = 16.sp)) },
                        leadingIcon = { Icon(Icons.Filled.Lock, "Xác nhận mật khẩu", tint = Color.Gray) },
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        // ✨ SỬA LỖI ICON Ở ĐÂY ✨
                        trailingIcon = {
                            val icon = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (isConfirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGrayBackground,
                            unfocusedContainerColor = LightGrayBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = BackgroundPink
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = DarkText) // Thêm style cho text khi nhập
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            authViewModel.registerWithUsername(username, password, confirmPassword)
                        },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BackgroundPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Đăng ký", style = MaterialTheme.typography.labelLarge.copy(color = Color.White))
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    TextButton(onClick = onBackToLogin) {
                        Text(
                            "Đã có tài khoản? Đăng nhập",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DarkText.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    // Hiển thị dialog lỗi từ ViewModel
    uiState.errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { authViewModel.clearMessages() },
            title = { Text("Thông báo", style = MaterialTheme.typography.titleLarge) },
            text = { Text(msg, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { authViewModel.clearMessages() }) { Text("OK", style = MaterialTheme.typography.labelLarge) }
            }
        )
    }
}
