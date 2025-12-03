package com.example.babiling

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.babiling.data.repository.FlashcardRepository
import com.example.babiling.ui.screens.auth.*
import com.example.babiling.ui.screens.choose.*
import com.example.babiling.ui.screens.home.*
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
import com.example.babiling.ui.screens.profile.EditProfileScreen
import com.example.babiling.ui.screens.profile.ProfileScreen
import com.example.babiling.ui.screens.progress.ProgressScreen
import com.example.babiling.ui.screens.progress.ProgressViewModelFactory
import com.example.babiling.ui.screens.settings.SettingsScreen
import com.example.babiling.ui.screens.settings.account.NotificationsScreen
import com.example.babiling.ui.screens.settings.account.SecurityScreen
import com.example.babiling.ui.screens.settings.actions.SupportScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.topic.learn.*
import com.example.babiling.ui.screens.topic.quiz.QuizScreen
import com.example.babiling.ui.screens.topic.result.ResultScreen
import com.example.babiling.ui.theme.BabiLingTheme
import kotlinx.coroutines.launch

// ------------------------------
// MARK: MainViewModel
// ------------------------------

class MainViewModel(private val repository: FlashcardRepository) : ViewModel() {
    fun syncDown() {
        viewModelScope.launch {
            try {
                Log.d("BabiLing_Sync", "[MainViewModel] Bắt đầu đồng bộ xuống...")
                repository.syncProgressDown()
                Log.d("BabiLing_Sync", "[MainViewModel] Đồng bộ xuống hoàn tất.")
            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "[MainViewModel] LỖI khi đồng bộ xuống!", e)
            }
        }
    }

    fun syncUp() {
        viewModelScope.launch {
            try {
                Log.d("BabiLing_Sync", "[MainViewModel] Bắt đầu đồng bộ lên...")
                repository.syncProgressUp()
                Log.d("BabiLing_Sync", "[MainViewModel] Đồng bộ lên hoàn tất.")
            } catch (e: Exception) {
                Log.e("BabiLing_Sync", "[MainViewModel] LỖI khi đồng bộ lên!", e)
            }
        }
    }
}

// ------------------------------
// MARK: MainActivity
// ------------------------------

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels {
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
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.syncDown()
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.syncUp()
    }
}

// ------------------------------
// MARK: AppNavigation
// ------------------------------

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authRepository = remember { ServiceLocator.provideAuthRepository(context) }
    // ✅ AuthViewModel được tạo bằng Factory (Cần thiết)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (data != null) {
            authViewModel.handleGoogleLogin(data)
        } else {
            Toast.makeText(context, "Đã hủy đăng nhập bằng Google.", Toast.LENGTH_SHORT).show()
        }
    }

    val startDestination = if (authRepository.getCurrentUser() != null) {
        Screen.Home.route
    } else {
        Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // --- Nhóm 1: Màn hình cơ bản (Splash, Onboarding, Auth) ---
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Onboarding.route) { OnboardingScreen(navController) }

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                googleSignInLauncher = googleSignInLauncher,
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // --- Nhóm 2: Màn hình chọn ngôn ngữ/tuổi ---
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
                        popUpTo(Screen.ChooseLang.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- Nhóm 3: Màn hình Chính (Container) ---
        composable(Screen.Home.route) { MainScreenContainer(navController) }

        // --- Nhóm 4: Màn hình Chủ đề/Bài học/Tiến trình ---
        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { topic -> navController.navigate(Screen.lessonSelectWithTopic(topic.id)) }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                paddingValues = PaddingValues()
            )
        }

        composable(
            route = Screen.LessonSelectRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
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
                onLessonComplete = { completedTopicId ->
                    navController.navigate(Screen.lessonSelectWithTopic(completedTopicId)) {
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
            val topicId = backStackEntry.arguments?.getString("topicId") ?: "all"
            QuizScreen(
                topicId = if (topicId == "all") null else topicId,
                onNavigateBack = {
                    if (topicId != "all" && topicId.isNotEmpty()) {
                        navController.navigate(Screen.resultWithTopic(topicId)) {
                            popUpTo(Screen.QuizRoute) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
        composable(
            route = Screen.ResultRoute,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            ResultScreen(
                topicId = topicId,
                onFinish = {
                    navController.navigate(Screen.lessonSelectWithTopic(topicId)) {
                        popUpTo(Screen.LessonSelectRoute) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- Nhóm 5: Màn hình Cài đặt & Hồ sơ ---
        composable(Screen.Settings.route) { SettingsScreen(navController) }

        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate(Screen.EditProfile.route) },
                onLogoutClick = {
                    authRepository.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ✅ SỬA LỖI: Truyền AuthViewModel vào EditProfileScreen
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                authViewModel = authViewModel, // 👈 ĐÃ THÊM VIEWMODEL
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    navController.popBackStack()
                },
                onDeleteAccount = {
                    authRepository.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                onLogout = { /* Hiện tại chưa dùng */ }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }

        composable(Screen.Security.route) {
            SecurityScreen(navController = navController)
        }

        composable(Screen.Support.route) {
            SupportScreen(navController = navController)
        }
    }
}

// ------------------------------
// MARK: MainScreenContainer
// ------------------------------

/**
 * Container cho các màn hình có BottomNavBar.
 */
@Composable
fun MainScreenContainer(navController: NavHostController) {
    val context = LocalContext.current
    var currentSubScreen by remember { mutableStateOf(HomeNavItems.Home) }

    HomeScreen(
        currentScreen = currentSubScreen,
        onNavigateToTopicSelect = { navController.navigate(Screen.TopicSelect.route) },
        onNavigateToQuiz = { topicId -> navController.navigate(Screen.quizWithTopic(topicId)) },
        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
        onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
        onNavigateToGame = {
            Toast.makeText(context, "Chức năng Trò chơi đang được phát triển!", Toast.LENGTH_SHORT).show()
        },
        onBottomNavItemSelected = { selectedScreen ->
            currentSubScreen = selectedScreen
        }
    )
}