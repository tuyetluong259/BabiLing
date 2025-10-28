package com.example.babiling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babiling.screens.OnboardingScreen
import com.example.babiling.screens.SplashScreen
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
    // 1. Tạo NavController
    val navController = rememberNavController()

    // 2. Tạo NavHost
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // Bắt đầu từ Splash
    ) {
        // 3. Định nghĩa tất cả các màn hình

        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }

    }
}