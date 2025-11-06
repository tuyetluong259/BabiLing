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
import com.example.babiling.ui.screens.Choose.ChooseLangScreen
import com.example.babiling.ui.screens.Choose.ChooseAgeScreen
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
                    // Tạm thời điều hướng đến "home_screen" (bạn sẽ tạo sau)
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
            )
        }

        // Màn 4: Register (Đăng ký)
        composable(route = "register_screen") {
            // SỬA LỖI: Hàm RegisterScreen gốc không có tham số 'onNavigateToLogin'.
            // Việc điều hướng sẽ được xử lý bên trong màn hình đó.
            RegisterScreen()
        }

        // Màn 5: Verification (Xác minh OTP)
        composable(
            route = "verification_screen/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            // Lấy số điện thoại đã được truyền qua
            // val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            // SỬA LỖI: Gọi hàm VerificationScreen với đúng các tham số mà nó yêu cầu.
            VerificationScreen(
                onBackClick = {
                    navController.popBackStack() // Quay lại màn hình trước đó (Login)
                },
                onVerifyClick = { otp ->
                    // TODO: Xử lý logic xác minh OTP
                    // Sau khi xác minh thành công, bạn có thể điều hướng đi
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
        // TODO: Thêm composable cho "home_screen" và các màn hình khác sau này
        // composable("home_screen") { HomeScreen() }

    }
}
