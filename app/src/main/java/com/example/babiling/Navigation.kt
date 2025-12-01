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
        // --- Hằng số cho Learn và Progress (không đổi) ---
        const val LearnRoute = "learn_screen/{topicId}"
        const val ProgressRoute = "progress_screen/{topicId}"

        fun learnWithTopic(topicId: String) = "learn_screen/$topicId"
        fun progressWithTopic(topicId: String) = "progress_screen/$topicId"


        // ✨ ================== SỬA ĐỔI PHẦN QUIZ ================== ✨

        // 1. Route CƠ SỞ cho Quiz
        private const val QUIZ_ROUTE_BASE = "quiz_screen"

        // 2. Route dùng để ĐỊNH NGHĨA trong NavHost, với tham số topicId là TÙY CHỌN
        const val QuizRoute = "$QUIZ_ROUTE_BASE?topicId={topicId}"

        // 3. Route dùng khi muốn "ÔN TẬP TẤT CẢ" (không có tham số)
        const val QuizRouteWithoutArgs = QUIZ_ROUTE_BASE

        /**
         * 4. Hàm trợ giúp để điều hướng đến Quiz CÓ chủ đề cụ thể.
         * Nó sẽ tạo ra route dạng: "quiz_screen?topicId=animals"
         */
        fun quizWithTopic(topicId: String) = "$QUIZ_ROUTE_BASE?topicId=$topicId"

        // ✨ ================== KẾT THÚC SỬA ĐỔI ================== ✨
    }
}
