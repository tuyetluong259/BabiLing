// Trong package: com.example.babiling (hoặc package gốc của em)
// Tên file: Navigation.kt

package com.example.babiling

// Đây là lớp niêm phong, định nghĩa tất cả các màn hình
// mà ứng dụng của chúng ta có thể điều hướng tới.
sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")       // Màn hình chờ
    object Onboarding : Screen("onboarding_screen") // Màn hình giới thiệu
    object Login : Screen("login_screen")
    object ChooseAge : Screen("choose_age_screen")
    object ChooseLang : Screen("choose_lang_screen")
    object Home : Screen("home_screen")
    object TopicSelect : Screen("topic_select_screen")
    object Learning : Screen("learning_screen")
    object Rating : Screen("rating_screen")
    object Progress : Screen("progress_screen")
    object Settings : Screen("settings_screen")
    object EditProfile : Screen("edit_profile_screen")
}