package com.example.babiling

sealed class Screen(val route: String) {
    // --- Các màn hình cơ bản ---
    object Splash : Screen("splash_screen")
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ChooseAge : Screen("choose_age_screen")
    object ChooseLang : Screen("choose_lang_screen")
    object Home : Screen("home_screen")
    object TopicSelect : Screen("topic_select_screen")

    // --- Các màn hình CÀI ĐẶT & HỒ SƠ ---
    object Settings : Screen("settings_screen")
    // CẬP NHẬT: EditProfile sử dụng route phân cấp
    object EditProfile : Screen("settings/account/edit_profile")
    object Rating : Screen("rating_screen")

    // 🔒 CÁC MÀN HÌNH CÀI ĐẶT CON (THÊM MỚI)
    // Các route này tương ứng với các mục trong SettingsScreen.kt
    object Security : Screen("settings/account/security")
    object Notifications : Screen("settings/account/notifications")
    object ChangePassword : Screen("settings/account/change_password")

    // ⚙️ CÁC MÀN HÌNH ACTIONS (THÊM MỚI)
    object ReportIssue : Screen("settings/actions/report_issue")
    object AddAccount : Screen("settings/actions/add_account")


    // --- CÁC ROUTE VÀ HÀM DÙNG CHUNG ---
    companion object {
        const val LearnRoute = "learn_screen/{topicId}"
        const val QuizRoute = "quiz_screen/{topicId}"
        const val ProgressRoute = "progress_screen/{topicId}"

        fun learnWithTopic(topicId: String) = "learn_screen/$topicId"
        fun quizWithTopic(topicId: String) = "quiz_screen/$topicId"
        fun progressWithTopic(topicId: String) = "progress_screen/$topicId"
    }
}