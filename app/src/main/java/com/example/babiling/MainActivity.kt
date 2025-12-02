package com.example.babiling

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider // ✨ THÊM IMPORT QUAN TRỌNG NÀY ✨
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.babiling.data.repository.FlashcardRepository
import com.example.babiling.ui.screens.auth.*
import com.example.babiling.ui.screens.choose.*
import com.example.babiling.ui.screens.home.*
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
// import com.example.babiling.ui.screens.progress.ProgressScreen // Không cần import trực tiếp màn hình này nữa
import com.example.babiling.ui.screens.rating.RatingScreen
import com.example.babiling.ui.screens.settings.SettingsScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.topic.Topic
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.topic.learn.*
import com.example.babiling.ui.screens.topic.quiz.QuizScreen
import com.example.babiling.ui.screens.topic.result.ResultScreen
import com.example.babiling.ui.theme.BabiLingTheme
import kotlinx.coroutines.launch

// ViewModel riêng cho MainActivity để quản lý đồng bộ
class MainViewModel(private val repository: FlashcardRepository) : ViewModel() {
    fun syncDown() {
        viewModelScope.launch {
            try {
                Log.d("BabiLing_Sync", "[MainViewModel] Bắt đầu đồng bộ dữ liệu xuống (sync down)...")
                repository.syncProgressDown()
                Log.d("BabiLing_Sync", "[MainViewModel] Đồng bộ (sync down) hoàn tất.")
            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "[MainViewModel] LỖI khi đồng bộ (sync down)!", e)
            }
        }
    }

    fun syncUp() {
        viewModelScope.launch {
            try {
                Log.d("BabiLing_Sync", "[MainViewModel] Bắt đầu đồng bộ dữ liệu lên (sync up)...")
                repository.syncProgressUp()
                Log.d("BabiLing_Sync", "[MainViewModel] Đồng bộ (sync up) hoàn tất.")
            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "[MainViewModel] LỖI khi đồng bộ (sync up)!", e)
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    // Khởi tạo MainViewModel để quản lý việc đồng bộ
    private val mainViewModel: MainViewModel by viewModels {
        // ✨ SỬA LỖI: Kế thừa từ `ViewModelProvider.Factory` thay vì `ViewModel.Factory` ✨
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    val repository = ServiceLocator.provideRepository(applicationContext)
                    return MainViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabiLingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Giao diện chỉ nên làm nhiệm vụ hiển thị.
                    AppNavigation()
                }
            }
        }
    }

    // Gọi đồng bộ xuống khi người dùng quay lại ứng dụng
    override fun onResume() {
        super.onResume()
        mainViewModel.syncDown()
    }

    // Gọi đồng bộ lên khi người dùng tạm dừng ứng dụng
    override fun onStop() {
        super.onStop()
        mainViewModel.syncUp()
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Việc đồng bộ đã được chuyển vào vòng đời của MainActivity, đáng tin cậy hơn.
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // --- Code còn lại của bạn giữ nguyên, nó đã rất tốt ---
        // --- Nhóm 1: Các màn hình ban đầu (Auth, Onboarding) ---
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { _, _ -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onGoogleLogin = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() },
                onNavigateToLang = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } }
            )
        }
        composable(Screen.ChooseLang.route) { ChooseLangScreen(onNavigateToChooseAge = { navController.navigate(Screen.ChooseAge.route) }) }
        composable(Screen.ChooseAge.route) {
            ChooseAgeScreen(
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(navController.graph.startDestinationId) { inclusive = true } } },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Nhóm 2: Màn hình Home và Rank có chung Bottom Nav ---
        composable(Screen.Home.route) {
            MainScreenContainer(navController = navController, currentScreen = HomeNavItems.Home)
        }
        composable(Screen.ProgressDashboard.route) {
            MainScreenContainer(navController = navController, currentScreen = HomeNavItems.Rank)
        }

        // --- Nhóm 3: Các màn hình độc lập (không có Bottom Nav chung) ---
        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { topic: Topic ->
                    navController.navigate(Screen.lessonSelectWithTopic(topic.id))
                }
            )
        }

        composable(
            route = Screen.LessonSelectRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            LessonSelectionScreen(
                topicId = topicId,
                viewModel = viewModel(factory = LessonViewModelFactory(LocalContext.current, topicId)),
                onNavigateBack = { navController.popBackStack() },
                onLessonSelected = { selectedTopicId, lessonNumber ->
                    navController.navigate(Screen.learnWithLesson(selectedTopicId, lessonNumber))
                }
            )
        }

        composable(
            route = Screen.LearnRoute,
            arguments = listOf(
                navArgument("topicId") { type = NavType.StringType },
                navArgument("lessonNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            val lessonNumber = backStackEntry.arguments?.getInt("lessonNumber") ?: return@composable

            LearnScreen(
                topicId = topicId,
                lessonNumber = lessonNumber,
                onBack = { navController.popBackStack() },
                onLessonComplete = {
                    navController.navigate(Screen.lessonSelectWithTopic(topicId)) {
                        popUpTo(Screen.LearnRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.QuizRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            Scaffold { innerPadding ->
                QuizScreen(
                    paddingValues = innerPadding,
                    topicId = if (topicId == "all") null else topicId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(
            route = Screen.ResultRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ResultScreen(topicId = topicId, onBack = { navController.popBackStack() })
        }

        composable(Screen.Rating.route) {
            RatingScreen(points = 10, navController = navController)
        }
    }
}

/**
 * Container này bây giờ chỉ dành cho các màn hình có NỘI DUNG thay đổi BÊN TRONG HomeScreen.
 * (Chủ yếu là Home và Rank).
 */
@Composable
fun MainScreenContainer(
    navController: androidx.navigation.NavController,
    currentScreen: HomeNavItems
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    HomeScreen(
        currentScreen = currentScreen,
        onBottomNavItemSelected = { selectedScreen ->
            val newRoute = when (selectedScreen) {
                HomeNavItems.Home -> Screen.Home.route
                HomeNavItems.Rank -> Screen.ProgressDashboard.route
                HomeNavItems.Learn -> Screen.quizWithTopic("all")
                HomeNavItems.Settings -> Screen.Settings.route
            }

            if (currentRoute != newRoute) {
                navController.navigate(newRoute) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        // Các hàm điều hướng khác không thay đổi
        onNavigateToTopicSelect = { navController.navigate(Screen.TopicSelect.route) },
        onNavigateToQuiz = { topicId -> navController.navigate(Screen.quizWithTopic(topicId)) },
        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
        onNavigateToGame = { Toast.makeText(context, "Chức năng Trò chơi đang được phát triển!", Toast.LENGTH_SHORT).show() },
        onNavigateToProfile = { Toast.makeText(context, "Chức năng Hồ sơ đang được phát triển!", Toast.LENGTH_SHORT).show() }
    )
}
