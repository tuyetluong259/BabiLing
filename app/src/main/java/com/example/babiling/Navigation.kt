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


    // ✨ ================= BỔ SUNG CÁC MÀN HÌNH CHÍNH CHO BOTTOM NAV ================= ✨

    // 1. Màn hình chính
    object Home : Screen("home_screen")

    // 2. Màn hình TỔNG QUAN TIẾN ĐỘ (dành cho nút thứ 2)
    object ProgressDashboard : Screen("progress_dashboard_screen")

    // 3. Màn hình ÔN TẬP (dành cho nút thứ 3)
    //    Thực chất nó sẽ trỏ đến `QuizRoute` ở dưới, nhưng việc có object này giúp code rõ ràng hơn.
    object ReviewHub : Screen("review_hub_screen")

    // 4. Màn hình CÀI ĐẶT (dành cho nút thứ 4)
    object Settings : Screen("settings_screen")

    // ✨ ============================== KẾT THÚC BỔ SUNG ============================== ✨


    // --- CÁC ROUTE VÀ HÀM DÙNG CHUNG ---
    companion object {

        // ================== LUỒNG HỌC MỚI ==================

        // 1. Route cho màn hình CHỌN BÀI HỌC (Lesson Selection)
        const val LessonSelectRoute = "lesson_select_screen/{topicId}"
        fun lessonSelectWithTopic(topicId: String) = "lesson_select_screen/$topicId"

        // 2. Route cho màn hình HỌC (Learn)
        const val LearnRoute = "learn_screen/{topicId}/{lessonNumber}"
        fun learnWithLesson(topicId: String, lessonNumber: Int) = "learn_screen/$topicId/$lessonNumber"

        // ✨ 3. ĐỔI TÊN ROUTE TIẾN ĐỘ THÀNH KẾT QUẢ ✨
        // Route cho màn hình KẾT QUẢ THEO CHỦ ĐỀ (Result)
        const val ResultRoute = "result_screen/{topicId}" // Đổi tên từ ProgressRoute
        fun resultWithTopic(topicId: String) = "result_screen/$topicId" // Đổi tên từ progressWithTopic

        // ================== KẾT THÚC LUỒNG HỌC MỚI ==================


        // ✨ ================== ROUTE CHO ÔN TẬP / QUIZ ================== ✨

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

        // ✨ =================== KẾT THÚC SỬA LỖI =================== ✨
    }
}
