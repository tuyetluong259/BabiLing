package com.example.babiling.ui.screens.auth

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ServiceLocator
import com.example.babiling.data.repository.AuthRepository
import com.example.babiling.ui.theme.*

// ===== Colors (Lấy từ giao diện cũ bạn thích) =====
private val ButtonRed = Color(0xFFED5B5B)
private val LightGrayBackground = Color(0xFFF0F0F0)
private val DarkText = Color(0xFF3A3A3A)

// =============================================================
//   Màn hình Đăng nhập (Logic mới + Giao diện cũ)
// =============================================================
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    googleSignInLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onAuthSuccess: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    // 1. Lấy State từ ViewModel
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 2. State cho các trường input
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // 3. Lắng nghe trạng thái đăng nhập thành công để điều hướng
    LaunchedEffect(key1 = uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful) {
            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
            onAuthSuccess()
            authViewModel.resetAllFlags()
        }
    }

    // 4. Giao diện
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bglogin)
    ) {
        DecorativeCircles() // Các vòng tròn trang trí

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Babiling",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tiếng Anh thật dễ – Cùng bé học\nngay hôm nay!",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            LoginFormCard(
                usernameValue = username,
                onUsernameChange = { username = it },
                passwordValue = password,
                onPasswordChange = { password = it },
                isPasswordVisible = isPasswordVisible,
                onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible },
                isLoading = uiState.isLoading,
                onLoginClick = {
                    authViewModel.signInWithUsernameOrEmail(username, password)
                },
                onGoogleLoginClick = {
                    val repo = ServiceLocator.provideAuthRepository(context)
                    val googleClient = repo.getGoogleSignInClient()
                    googleClient.signOut().addOnCompleteListener {
                        googleSignInLauncher.launch(googleClient.signInIntent)
                    }
                },
                onRegisterClick = onNavigateRegister
            )

            Spacer(modifier = Modifier.weight(1f)) // Đẩy ảnh xuống dưới

            Image(
                painter = painterResource(id = R.drawable.kids),
                contentDescription = "Kids learning",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 20.dp)
            )
        }

        // Dialog hiển thị lỗi từ ViewModel
        uiState.errorMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { authViewModel.clearMessages() },
                confirmButton = {
                    TextButton({ authViewModel.clearMessages() }) { Text("OK") }
                },
                title = { Text("Thông báo") },
                text = { Text(msg) }
            )
        }
    }
}

// ===== Card chứa Form (Giao diện cũ + Thêm trường Password) =====
@Composable
fun LoginFormCard(
    usernameValue: String,
    onUsernameChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)        ) {
            Text(
                "Đăng nhập",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = DarkText,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(Modifier.height(20.dp))

            // -- Trường Tên đăng nhập --
            OutlinedTextField(
                value = usernameValue,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Tên đăng nhập",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = ButtonRed
                )
            )
            Spacer(Modifier.height(12.dp))

            // -- TRƯỜNG MẬT KHẨU --
            OutlinedTextField(
                value = passwordValue,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Mật khẩu",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                },                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityChange) {
                        // SỬA LỖI: Dùng icon có sẵn của Material
                        val icon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = ButtonRed
                )
            )
            Spacer(Modifier.height(20.dp))

            // -- Nút Đăng nhập --
            Button(
                onClick = onLoginClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonRed),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(12.dp))

            // -- Nút Đăng nhập với Google --
            OutlinedButton(
                onClick = onGoogleLoginClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = LightGrayBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.gg),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Đăng nhập với Google", color = DarkText, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            // -- Link Đăng ký --
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản? ", color = Color.Gray)
                ClickableText(
                    text = AnnotatedString("Đăng ký"),
                    onClick = { onRegisterClick() },
                    style = TextStyle(
                        color = ButtonRed,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

// ===== Các vòng tròn trang trí =====
@Composable
fun DecorativeCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-120).dp)
                .size(250.dp)
                .clip(RoundedCornerShape(percent = 100))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (200).dp, y = (50).dp)
                .size(300.dp)
                .clip(RoundedCornerShape(percent = 100))
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-140).dp, y = (60).dp)
                .size(200.dp)
                .clip(RoundedCornerShape(percent = 100))
                .background(Color.White)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenFullPreview() {
    BabiLingTheme {
        // Tái tạo lại giao diện mà không cần ViewModel thật
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bglogin)
        ) {
            DecorativeCircles() // Các vòng tròn trang trí

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Babiling",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tiếng Anh thật dễ – Cùng bé học\nngay hôm nay!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Hiển thị LoginFormCard với dữ liệu giả
                LoginFormCard(
                    usernameValue = "",
                    onUsernameChange = {},
                    passwordValue = "",
                    onPasswordChange = {},
                    isPasswordVisible = false,
                    onPasswordVisibilityChange = {},
                    isLoading = false, // Xem ở trạng thái không tải
                    onLoginClick = {},
                    onGoogleLoginClick = {},
                    onRegisterClick = {}
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.kids),
                    contentDescription = "Kids learning",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

