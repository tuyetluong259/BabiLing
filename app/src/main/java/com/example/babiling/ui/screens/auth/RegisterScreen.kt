package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.babiling.R
import com.example.babiling.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit = {},
    onNavigateToLang: () -> Unit = {}
) {
    val firestore = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ================================================================
    // ✅ Register function
    // ================================================================
    fun registerUser() {

        if (username.isBlank()) {
            errorMessage = "Tên đăng nhập không được để trống."
            return
        }

        if (password.length < 6) {
            errorMessage = "Mật khẩu phải từ 6 ký tự."
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Xác nhận mật khẩu không trùng khớp."
            return
        }

        isLoading = true

        firestore.collection("users")
            .document(username)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    isLoading = false
                    errorMessage = "Tên đăng nhập đã tồn tại."
                } else {
                    val userData = mapOf(
                        "username" to username,
                        "password" to password,
                        "createdAt" to System.currentTimeMillis()
                    )

                    firestore.collection("users")
                        .document(username)
                        .set(userData)
                        .addOnSuccessListener {
                            isLoading = false
                            onBackToLogin()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            errorMessage = e.message ?: "Đăng ký thất bại."
                        }
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.message ?: "Đăng ký thất bại."
            }
    }

    // ================================================================
    // ✅ UI
    // ================================================================
    Box(
        modifier = Modifier.fillMaxSize().background(PrimaryPink)
    ) {

        // ✅ decorative
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset((-200).dp, (-120).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(250.dp, (-30).dp)
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

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

                Spacer(Modifier.height(60.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Tiếng Anh thật dễ – Cùng bé học ngay hôm nay!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
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
                            color = DarkText
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Tên đăng nhập") },
                            leadingIcon = { Icon(Icons.Filled.Person, null) },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        // ✅ Password with eye
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Mật khẩu") },
                            leadingIcon = { Icon(Icons.Filled.Lock, null) },
                            visualTransformation =
                                if (isPasswordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isPasswordVisible = !isPasswordVisible
                                }) {
                                    Icon(
                                        painter = if (isPasswordVisible)
                                            painterResource(id = R.drawable.ic_eye_open)
                                        else painterResource(id = R.drawable.ic_eye_closed),
                                        contentDescription = null
                                    )
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        // ✅ Confirm password with eye
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Xác nhận mật khẩu") },
                            leadingIcon = { Icon(Icons.Filled.Lock, null) },
                            visualTransformation =
                                if (isConfirmPasswordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    isConfirmPasswordVisible =
                                        !isConfirmPasswordVisible
                                }) {
                                    Icon(
                                        painter = if (isConfirmPasswordVisible)
                                            painterResource(id = R.drawable.ic_eye_open)
                                        else painterResource(id = R.drawable.ic_eye_closed),
                                        contentDescription = null
                                    )
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = { registerUser() },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("Đăng ký", color = Color.White)
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        TextButton(onClick = onNavigateToLang) {
                            Text(
                                "Đã có tài khoản? Đăng nhập",
                                color = DarkText.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.kids),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }

    // ================================================================
    // ✅ Error dialog
    // ================================================================
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Thông báo") },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = { errorMessage = null }) { Text("OK") }
            }
        )
    }
}
