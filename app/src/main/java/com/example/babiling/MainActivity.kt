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
import com.google.firebase.FirebaseApp
import com.example.babiling.ui.screens.onboarding.OnboardingScreen
import com.example.babiling.ui.screens.splash.SplashScreen
import com.example.babiling.ui.screens.auth.LoginScreen
import com.example.babiling.ui.screens.auth.RegisterScreen
import com.example.babiling.ui.screens.choose.ChooseLangScreen
import com.example.babiling.ui.screens.choose.ChooseAgeScreen
import com.example.babiling.ui.screens.home.HomeScreen
import com.example.babiling.ui.theme.BabiLingTheme

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
            HomeScreen()
        }
    }
}