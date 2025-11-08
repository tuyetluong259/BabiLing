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
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.auth.LoginScreen
import com.example.babiling.ui.screens.auth.RegisterScreen
import com.example.babiling.ui.screens.auth.VerificationScreen
import com.example.babiling.ui.screens.choose.ChooseLangScreen
import com.example.babiling.ui.screens.choose.ChooseAgeScreen
// === THÊM IMPORT CHO HOME SCREEN ===
import com.example.babiling.ui.screens.home.HomeScreen
import com.example.babiling.ui.theme.BabiLingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        // Màn 1: Splash
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Màn 2: Onboarding
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

        // Màn 3: Login
        composable(route = "login_screen") {
            LoginScreen(
                onNavigateToVerification = { phoneNumber ->
                    navController.navigate("verification_screen/$phoneNumber")
                },
                onNavigateToRegister = {
                    navController.navigate("register_screen")
                },
                onNavigateToHome = {
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            )
        }

        // Màn 4: Register (Đăng ký)
        composable(route = "register_screen") {
            RegisterScreen()
        }

        // Màn 5: Verification (Xác minh OTP)
        composable(
            route = "verification_screen/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            VerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVerifyClick = { otp ->
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                },
                onResendClick = {
                    // TODO: Xử lý logic gửi lại mã OTP
                }
            )
        }

        // 6. Choose Language Screen
        composable(Screen.ChooseLang.route) {
            ChooseLangScreen(navController = navController)
        }

        // 7. Choose Age Screen
        composable(Screen.ChooseAge.route) {
            ChooseAgeScreen(navController = navController)
        }

        // === THÊM MÀN HÌNH HOME VÀO ĐÂY ===
        composable("home_screen") {
            HomeScreen(navController = navController)
          }
    }
}