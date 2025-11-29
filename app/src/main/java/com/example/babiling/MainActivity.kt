package com.example.babiling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.babiling.ui.screens.topic.Topic
import com.example.babiling.ui.screens.auth.LoginScreen
import com.example.babiling.ui.screens.auth.RegisterScreen
import com.example.babiling.ui.screens.choose.ChooseAgeScreen
import com.example.babiling.ui.screens.choose.ChooseLangScreen
import com.example.babiling.ui.screens.home.HomeScreen
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.topic.progress.ProgressScreen
import com.example.babiling.ui.screens.topic.quiz.QuizScreen
import com.example.babiling.ui.screens.topic.learn.LearnScreen
import com.example.babiling.ui.theme.BabiLingTheme
import com.google.firebase.FirebaseApp

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

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- CÁC MÀN HÌNH ĐIỀU HƯỚNG CƠ BẢN (Không thay đổi) ---
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
                onBackToLogin = { navController.popBackStack() },
                onNavigateToLang = {
                    navController.navigate(Screen.ChooseLang.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ChooseLang.route) {
            ChooseLangScreen(onNavigateToChooseAge = { navController.navigate(Screen.ChooseAge.route) })
        }
        composable(Screen.ChooseAge.route) {
            ChooseAgeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // popUpTo đầu tiên để xóa tất cả màn hình trước đó khỏi back stack
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // --- CÁC MÀN HÌNH CHỦ ĐỀ ĐÃ ĐƯỢC CẬP NHẬT ---

        // 1. Màn hình chọn chủ đề
        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { topic: Topic ->
                    // ✨ SỬ DỤNG HÀM TRỢ GIÚP ĐỂ ĐIỀU HƯỚNG AN TOÀN
                    navController.navigate(Screen.learnWithTopic(topic.id))
                }
            )
        }

        // ✨✨✨ PHẦN ĐÃ SỬA ✨✨✨
        // 2. Màn hình HỌC (Dùng chung cho tất cả chủ đề)
        composable(
            route = Screen.LearnRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            LearnScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() }
                // Đã xóa onOpenQuiz và onOpenProgress vì LearnScreen không còn cần chúng nữa
            )
        }

        // 3. Màn hình ÔN TẬP (Dùng chung cho tất cả chủ đề)
        composable(
            route = Screen.QuizRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            QuizScreen(
                topicId = topicId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Màn hình TIẾN ĐỘ (Dùng chung cho tất cả chủ đề)
        composable(
            route = Screen.ProgressRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ProgressScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
