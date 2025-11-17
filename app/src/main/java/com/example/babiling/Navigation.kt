package com.example.babiling

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ChooseAge : Screen("choose_age_screen")
    object ChooseLang : Screen("choose_lang_screen")
    object Home : Screen("home_screen")
    object Greetings : Screen("greetings_screen")
    object Body : Screen("body_screen") 
    object TopicSelect : Screen("topic_select_screen")
    object Learning : Screen("learning_screen")
    object Rating : Screen("rating_screen")
    object Progress : Screen("progress_screen")
    object Settings : Screen("settings_screen")
    object EditProfile : Screen("edit_profile_screen")

}
