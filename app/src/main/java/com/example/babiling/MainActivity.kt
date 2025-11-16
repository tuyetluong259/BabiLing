package com.example.babiling

import android.os.Bundle
import android.widget.Toast // Import
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Import
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.auth.LoginScreen
import com.example.babiling.ui.screens.auth.RegisterScreen
import com.example.babiling.ui.screens.choose.ChooseLangScreen
import com.example.babiling.ui.screens.choose.ChooseAgeScreen
import com.example.babiling.ui.screens.home.HomeScreen
import com.example.babiling.ui.theme.BabiLingTheme

// <-- THÊM 2 IMPORT CỦA MÀN HÌNH MỚI -->
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.study.GreetingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            BabiLingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current // Lấy context để dùng Toast

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { _, _ ->
                    navController.navigate(Screen.ChooseLang.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoogleLogin = {
                    navController.navigate(Screen.ChooseLang.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onNavigateToLang = {
                    navController.navigate(Screen.ChooseLang.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ChooseLang.route) {
            ChooseLangScreen(
                onNavigateToChooseAge = {
                    navController.navigate(Screen.ChooseAge.route)
                }
            )
        }

        composable(Screen.ChooseAge.route) {
            ChooseAgeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route)
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // <-- LỖI CỦA BẠN LÀ DO THIẾU 2 KHỐI NÀY -->

        // 1. Màn hình Chọn Chủ Đề (mà "topic_select_screen" trỏ tới)
        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen( // Dùng hàm từ TopicScreen1.kt
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTopicSelected = { topic ->
                    when (topic.title) {
                        "Greetings" -> {
                            navController.navigate(Screen.Greetings.route)
                        }
                        else -> {
                            Toast.makeText(context, "${topic.title} (Sắp có)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // 2. Màn hình học (Greetings)
        composable(Screen.Greetings.route) {
            GreetingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                },
                onItemSelected = { item -> // Đổi tên từ onFruitSelected
                    println("Đã nhấn vào: ${item.name}")
                }
            )
        }
    }
}