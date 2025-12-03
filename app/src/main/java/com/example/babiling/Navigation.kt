package com.example.babiling

sealed class Screen(val route: String) {
    // --- Các màn hình cơ bản ---
    object Splash : Screen("splash_screen")
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ChooseAge : Screen("choose_age_screen")
    object ChooseLang : Screen("choose_lang_screen")
    object TopicSelect : Screen("topic_select_screen")

    // --- Các màn hình CÀI ĐẶT & HỒ SƠ ---
    object EditProfile : Screen("edit_profile_screen")
    object Rating : Screen("rating_screen")
    object Support : Screen("support_screen")
    // ✨ ================= BỔ SUNG CÁC MÀN HÌNH CON CHO SETTINGS TẠI ĐÂY ================= ✨
    object Security : Screen("security_screen")
    object Notifications : Screen("notifications_screen")
    object ReportIssue : Screen("report_issue_screen")
    // ✨ ================================= KẾT THÚC ================================= ✨


    // --- Các màn hình chính cho BOTTOM NAV ---
    object Home : Screen("home_screen")
    object Progress : Screen("progress_screen")
    object ReviewHub : Screen("review_hub_screen")
    object Settings : Screen("settings_screen")
    object Profile : Screen("profile_screen")
    // --- CÁC ROUTE VÀ HÀM DÙNG CHUNG ---
    companion object {
        // (Phần companion object của bạn đã rất tốt và không cần thay đổi)
        const val LessonSelectRoute = "lesson_select_screen/{topicId}"
        fun lessonSelectWithTopic(topicId: String) = "lesson_select_screen/$topicId"

        const val LearnRoute = "learn_screen/{topicId}/{lessonNumber}"
        fun learnWithLesson(topicId: String, lessonNumber: Int) = "learn_screen/$topicId/$lessonNumber"

        const val ResultRoute = "result_screen/{topicId}"
        fun resultWithTopic(topicId: String) = "result_screen/$topicId"

        const val QuizRoute = "quiz_screen?topicId={topicId}"
        fun quizWithTopic(topicId: String) = "quiz_screen?topicId=$topicId"
    }
}
