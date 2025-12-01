package com.example.babiling

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.babiling.ui.screens.rating.RatingScreen
import com.example.babiling.ui.screens.settings.SettingsScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.topic.learn.LearnScreen
import com.example.babiling.ui.screens.topic.learn.LessonSelectionScreen
import com.example.babiling.ui.screens.topic.learn.LessonViewModel
import com.example.babiling.ui.screens.topic.progress.ProgressScreen
import com.example.babiling.ui.screens.topic.quiz.QuizScreen
import com.example.babiling.ui.theme.BabiLingTheme
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.collectAsState


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
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- Các màn hình từ Splash đến Home giữ nguyên ---
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { _, _ -> navController.navigate(Screen.ChooseLang.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onGoogleLogin = { navController.navigate(Screen.ChooseLang.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() },
                onNavigateToLang = { navController.navigate(Screen.ChooseLang.route) { popUpTo(Screen.Login.route) { inclusive = true } } }
            )
        }
        composable(Screen.ChooseLang.route) { ChooseLangScreen(onNavigateToChooseAge = { navController.navigate(Screen.ChooseAge.route) }) }
        composable(Screen.ChooseAge.route) {
            ChooseAgeScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✨ ================== SỬA LỖI CRASH KHI VÀO QUIZ ================== ✨
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTopicSelect = { navController.navigate(Screen.TopicSelect.route) },
                // Sửa lại cách điều hướng, luôn dùng route đầy đủ và an toàn
                onNavigateToQuiz = { navController.navigate(Screen.QuizRoute) },
                onNavigateToGame = { Toast.makeText(context, "Chức năng Trò chơi đang được phát triển!", Toast.LENGTH_SHORT).show() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToProfile = { Toast.makeText(context, "Chức năng Hồ sơ đang được phát triển!", Toast.LENGTH_SHORT).show() }
            )
        }
        // ✨ ======================= KẾT THÚC SỬA LỖI ======================= ✨

        // 1. SỬA ĐỔI TOPIC SELECTION SCREEN
        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { topic: Topic ->
                    navController.navigate(Screen.lessonSelectWithTopic(topic.id))
                }
            )
        }

        // ✨ ================== SỬ DỤNG VIEWMODEL CHO LESSON SELECTION ================== ✨
        composable(
            route = Screen.LessonSelectRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""

            // Bước 1: Khởi tạo ViewModel
            val lessonViewModel: LessonViewModel = viewModel()

            // Bước 2: Lấy dữ liệu từ ViewModel
            val lessons by lessonViewModel.lessons.collectAsState()
            val completedLessons by lessonViewModel.completedLessons.collectAsState()

            // Bước 3: Kích hoạt việc tải dữ liệu khi topicId thay đổi
            LaunchedEffect(topicId) {
                if (topicId.isNotEmpty()) {
                    lessonViewModel.loadLessons(topicId)
                }
            }

            LessonSelectionScreen(
                topicId = topicId,
                // Bước 4: Truyền dữ liệu thật vào màn hình
                lessons = lessons,
                completedLessons = completedLessons,
                onNavigateBack = { navController.popBackStack() },
                onLessonSelected = { selectedTopicId, lessonNumber ->
                    navController.navigate(Screen.learnWithLesson(selectedTopicId, lessonNumber))
                }
            )
        }
        // ✨ ========================= KẾT THÚC ========================= ✨


        //LEARN SCREEN
        composable(
            route = Screen.LearnRoute,    arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                // Quan trọng: lessonNumber trong route là String, cần get as String
                navArgument("lessonNumber") { type = NavType.StringType }
            )
        ) {
            // ViewModel sẽ tự động lấy topicId và lessonNumber từ arguments.
            // Chúng ta không cần lấy thủ công và truyền vào LearnScreen nữa.
            LearnScreen(
                onBack = { navController.popBackStack() },
                onLessonComplete = {
                    // Quay trở lại màn hình chọn bài học sau khi học xong
                    navController.popBackStack(Screen.LessonSelectRoute, false)
                }
            )
        }


        // --- Các màn hình còn lại ---
        composable(
            route = Screen.QuizRoute,
            arguments = listOf(navArgument("topicId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")
            QuizScreen(topicId = topicId, onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ProgressRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ProgressScreen(topicId = topicId, onBack = { navController.popBackStack() })
        }

        composable(Screen.Rating.route) {
            RatingScreen(points = 10, navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
