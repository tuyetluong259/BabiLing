package com.example.babiling

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.babiling.ui.screens.topic.TopicSelectionScreen
import com.example.babiling.ui.screens.topic.study.GreetingsScreen
import com.example.babiling.ui.screens.topic.study.BodyScreen
import com.example.babiling.ui.screens.topic.study.ColorsScreen
import com.example.babiling.ui.screens.topic.study.FruitScreen
import com.example.babiling.ui.screens.topic.study.AnimalsScreen
import com.example.babiling.ui.screens.topic.study.ToysScreen

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
        // --- CÁC MÀN HÌNH ĐIỀU HƯỚNG CƠ BẢN (Giữ nguyên) ---
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
                        popUpTo(Screen.ChooseLang.route) { inclusive = true }
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
            HomeScreen(navController = navController)
        }

        composable(Screen.TopicSelect.route) {
            TopicSelectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTopicSelected = { topic ->
                    when (topic.title) {
                        "Greetings" -> {
                            navController.navigate(Screen.Greetings.route)
                        }
                        "Body" -> {
                            navController.navigate(Screen.Body.route)
                        }
                        "Colors" -> {
                            navController.navigate(Screen.Colors.route)
                        }
                        "Fruit" -> {
                            navController.navigate(Screen.Fruit.route)
                        }
                        "Animals" -> {
                            navController.navigate(Screen.Animals.route)
                        }
                        "Toys" -> {
                            navController.navigate(Screen.Toys.route)
                        }
                        else -> {
                            Toast.makeText(context, "${topic.title} (Sắp có)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        composable(Screen.Greetings.route) {
            GreetingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào: ${item.name}")
                }
            )
        }

        composable(Screen.Body.route) {
            BodyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào: ${item.name}")
                }
            )
        }

        composable(Screen.Colors.route) {
            ColorsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                    // TODO: Xử lý đi tới
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào: ${item.name}")
                }
            )
        }

        composable(Screen.Fruit.route) {
            FruitScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                    // TODO: Xử lý đi tới
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào: ${item.name}")
                }
            )
        }

        composable(Screen.Animals.route) {
            AnimalsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                    // TODO: Xử lý đi tới
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào Animals: ${item.name}")
                }
            )
        }

        composable(Screen.Toys.route) {
            ToysScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFinish = {
                    navController.popBackStack()
                },
                onNavigateForward = {
                    // TODO: Xử lý đi tới
                },
                onItemSelected = { item ->
                    println("Đã nhấn vào Toy: ${item.name}")
                }
            )
        }

    }
}