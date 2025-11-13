package com.example.babiling.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResult
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// ===== Colors (match your design) =====
private val BackgroundPink = Color(0xFFEB7474)
private val LightGrayBackground = Color(0xFFF0F0F0)
private val DarkText = Color(0xFF333333)

// ===== Replace with your real web client id from google-services.json (OAuth 2.0 client ID) =====
private const val WEB_CLIENT_ID = "683013257609-de6tu6gqjh09oq2s1mh5hk9mia2pi5g9.apps.googleusercontent.com"

// =============================
//   Login Screen (Google only)
// =============================
@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit = { _, _ -> },
    onGoogleLogin: () -> Unit = {},
    onNavigateRegister: () -> Unit = {}
) {
    // 1) State ("3 biến state")
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // 2) Android + Google objects (must be INSIDE a @Composable)
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    // Build GSO + Client with remember so they are stable across recompositions
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val googleClient = remember { GoogleSignIn.getClient(context, gso) }

    // 3) ActivityResult launcher (Composable API)
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            isLoading = true
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    isLoading = false
                    // You can pass email back or simply signal success
                    onGoogleLogin()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    errorText = e.localizedMessage ?: "Đăng nhập Google thất bại"
                }
        } catch (e: ApiException) {
            errorText = e.localizedMessage ?: "Không thể lấy tài khoản Google"
        }
    }

    // 4) UI
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
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Babiling",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tiếng Anh thật dễ – Cùng bé học\nngay hôm nay!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            LoginFormCard(
                usernameValue = username,
                onUsernameChange = { username = it },
                isLoading = isLoading,
                onLoginClick = { onLogin(username, password) },
                onGoogleLoginClick = {
                    // Force account picker each time
                    isLoading = true
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                },
                onRegisterClick = onNavigateRegister
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.kids),
                contentDescription = "Kids learning",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        // Error dialog
        errorText?.let { msg ->
            AlertDialog(
                onDismissRequest = { errorText = null },
                confirmButton = {
                    TextButton({ errorText = null }) { Text("OK") }
                },
                title = { Text("Thông báo") },
                text = { Text(msg) }
            )
        }
    }
}

// ===== Decorative circles (unchanged) =====
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
                .offset(x = (250).dp, y = (0).dp)
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

// ===== Card =====
@Composable
fun LoginFormCard(
    usernameValue: String,
    onUsernameChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Đăng nhập",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = DarkText,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = usernameValue,
                onValueChange = onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Tên đăng nhập",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Tên đăng nhập",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = LightGrayBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = BackgroundPink
                )
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundPink),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        "Đăng nhập",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick,
                border = null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = LightGrayBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(id = R.drawable.gg),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Đăng nhập với Google",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    "Chưa có tài khoản? Đăng ký",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = DarkText.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    BabiLingTheme { LoginScreen() }
}
