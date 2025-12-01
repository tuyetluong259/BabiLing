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

        // ================== LUỒNG HỌC MỚI ==================

        // 1. Route cho màn hình CHỌN BÀI HỌC (Lesson Selection)
        const val LessonSelectRoute = "lesson_select_screen/{topicId}"
        fun lessonSelectWithTopic(topicId: String) = "lesson_select_screen/$topicId"

        // 2. Route cho màn hình HỌC (Learn)
        const val LearnRoute = "learn_screen/{topicId}/{lessonNumber}"
        fun learnWithLesson(topicId: String, lessonNumber: Int) = "learn_screen/$topicId/$lessonNumber"

        // 3. Route cho màn hình TIẾN ĐỘ (Progress)
        const val ProgressRoute = "progress_screen/{topicId}"
        fun progressWithTopic(topicId: String) = "progress_screen/$topicId"

        // ================== KẾT THÚC LUỒNG HỌC MỚI ==================


        // ✨ ================== SỬA LỖI CHO QUIZ ================== ✨

        /**
         * Route DUY NHẤT cho màn hình Quiz.
         * `topicId` là một tham số tùy chọn (optional parameter, có thể null).
         * - Nếu không có topicId -> Chế độ ôn tập tất cả.
         * - Nếu có topicId -> Chế độ ôn tập theo chủ đề.
         */
        const val QuizRoute = "quiz_screen?topicId={topicId}"

        /**
         * Hàm trợ giúp để điều hướng đến màn hình Quiz CÓ KÈM theo topicId.
         * Tạo ra route dạng: "quiz_screen?topicId=animals"
         */
        fun quizWithTopic(topicId: String) = "quiz_screen?topicId=$topicId"

        // `QuizRouteWithoutArgs` đã được XÓA BỎ để tránh nhầm lẫn.

        // ✨ =================== KẾT THÚC SỬA LỖI =================== ✨
    }
}
