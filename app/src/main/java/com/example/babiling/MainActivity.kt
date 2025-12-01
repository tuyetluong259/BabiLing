package com.example.babiling

import android.os.Bundle
import android.widget.Toast // Thêm import này để hiển thị thông báo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Thêm import này
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
    // Lấy context để có thể hiển thị Toast (thông báo tạm thời)
    val context = LocalContext.current

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

        // ======================== PHẦN CẬP NHẬT ========================
        // Thay thế composable của HomeScreen
        composable(Screen.Home.route) {
            HomeScreen(
                // Cung cấp logic điều hướng thực sự cho các hàm lambda
                onNavigateToTopicSelect = {
                    navController.navigate(Screen.TopicSelect.route)
                },
                onNavigateToQuiz = {
                    // Màn hình Quiz cần một topicId, nhưng nút "Ôn tập" chung
                    // không có topicId cụ thể. Chúng ta sẽ điều hướng đến QuizScreen
                    // với một topicId rỗng để xử lý logic "ôn tập tất cả" sau này.
                    navController.navigate(Screen.quizWithTopic("")) // "" đại diện cho ôn tập tất cả
                },
                onNavigateToGame = {
                    // Tạm thời hiển thị thông báo vì chưa có màn hình Game
                    Toast.makeText(context, "Chức năng Trò chơi đang được phát triển!", Toast.LENGTH_SHORT).show()
                },
                onNavigateToSettings = {
                    // Tạm thời hiển thị thông báo
                    Toast.makeText(context, "Chức năng Cài đặt đang được phát triển!", Toast.LENGTH_SHORT).show()
                },
                onNavigateToProfile = {
                    // Tạm thời hiển thị thông báo
                    Toast.makeText(context, "Chức năng Hồ sơ đang được phát triển!", Toast.LENGTH_SHORT).show()
                }
            )
        }
        // ======================== KẾT THÚC CẬP NHẬT ========================

        // --- CÁC MÀN HÌNH CHỦ ĐỀ (Giữ nguyên) ---

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

        // 2. Màn hình HỌC (Dùng chung cho tất cả chủ đề)
        composable(
            route = Screen.LearnRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            LearnScreen(
                topicId = topicId,
                onBack = { navController.popBackStack() }
            )
        }

        // 3. Màn hình ÔN TẬP (Dùng chung cho tất cả chủ đề)
        composable(
            route = Screen.QuizRoute,
            // Đánh dấu topicId là có thể null để xử lý trường hợp ôn tập chung
            arguments = listOf(navArgument("topicId") { nullable = true; defaultValue = null; type = NavType.StringType })
        ) { backStackEntry ->
            // topicId có thể là null hoặc rỗng khi đến từ nút Ôn tập chung
            val topicId = backStackEntry.arguments?.getString("topicId")
            QuizScreen(
                topicId = topicId, // Truyền topicId có thể là null vào QuizScreen
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
