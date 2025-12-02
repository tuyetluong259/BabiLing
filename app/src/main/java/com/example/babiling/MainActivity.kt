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
import com.example.babiling.ui.screens.rating.RatingScreen
import com.example.babiling.ui.screens.settings.SettingsScreen

// THÊM IMPORTS CHO CÁC MÀN HÌNH CÀI ĐẶT CON
import com.example.babiling.ui.screens.settings.account.SecurityScreen
import com.example.babiling.ui.screens.settings.account.NotificationsScreen
// import com.example.babiling.ui.screens.settings.account.ChangePasswordScreen // Giả định
 import com.example.babiling.ui.screens.settings.actions.ReportIssueScreen
// import com.example.babiling.ui.screens.settings.actions.AddAccountScreen // Giả định

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
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onTopicSelected = { topic: Topic ->
                    navController.navigate(Screen.learnWithTopic(topic.id))
                }
            )
        }

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

        composable(Screen.Rating.route) {
            RatingScreen(
                points = 10,
                navController = navController
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // =======================================================
        // THÊM CÁC MÀN HÌNH CÀI ĐẶT CON (SETUP SCREENS)
        // =======================================================

        // Màn hình Bảo vệ (SecurityScreen)
        composable(Screen.Security.route) {
            SecurityScreen(navController = navController)
        }

        // Màn hình Thông báo (NotificationsScreen)
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
        // Màn hình Báo cáo sự cố (ReportIssueScreen)
        composable(Screen.ReportIssue.route) {
            ReportIssueScreen(navController = navController)
        }
    }
}