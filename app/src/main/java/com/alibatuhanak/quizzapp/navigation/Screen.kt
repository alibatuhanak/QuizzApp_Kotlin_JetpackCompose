package com.alibatuhanak.quizzapp.navigation

import com.alibatuhanak.quizzapp.service.PreferenceDataStore
import io.grpc.android.BuildConfig

sealed class Screen(val route: String) {
    private var ali = BuildConfig.BUILD_TYPE.get(0)

    data object LoginScreen : Screen("login_screen")
    data object RegisterScreen : Screen("register_screen")
    data object HomeScreen : Screen("home_screen")
    data object QuizScreen : Screen("quiz_screen")
    data object EndLevelScreen : Screen("end_level_screen")
    data object LeaderboardScreen : Screen("leaderboard_screen")
    data object SettingsScreen : Screen("settings_screen")
    data object ProfileSettingsScreen : Screen("profile_settings_screen")
    data object PasswordSettingsScreen : Screen("password_settings_screen")
    data object EmailSettingsScreen : Screen("email_settings_screen")
    data object LoadingScreen : Screen("loading_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }

        }
    }


}
