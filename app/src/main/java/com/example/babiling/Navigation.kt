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
    object EditProfile : Screen("edit_profile_screen")
    object Rating : Screen("rating_screen")

    // --- CÁC ROUTE VÀ HÀM DÙNG CHUNG ---
    companion object {
        // Hằng số chứa route gốc với placeholder
        const val LearnRoute = "learn_screen/{topicId}"
        const val QuizRoute = "quiz_screen/{topicId}"
        const val ProgressRoute = "progress_screen/{topicId}"

        /**
         * Các hàm này giúp tạo ra route hoàn chỉnh một cách an toàn và có thể gọi trực tiếp từ lớp Screen.
         * Ví dụ: thay vì viết "learn_screen/animals", bạn sẽ gọi Screen.learnWithTopic("animals").
         * Chúng được đặt trong companion object để hoạt động giống như các hàm tĩnh (static) trong Java. [2, 3]
         */
        fun learnWithTopic(topicId: String) = "learn_screen/$topicId"
        fun quizWithTopic(topicId: String) = "quiz_screen/$topicId"
        fun progressWithTopic(topicId: String) = "progress_screen/$topicId"
    }
}
