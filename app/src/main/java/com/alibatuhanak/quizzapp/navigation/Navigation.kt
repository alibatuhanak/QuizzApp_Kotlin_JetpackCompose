package com.alibatuhanak.quizzapp.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.alibatuhanak.quizzapp.view.EmailSettingsScreen
import com.alibatuhanak.quizzapp.view.EndLevelScreen
import com.alibatuhanak.quizzapp.view.HomeScreen
import com.alibatuhanak.quizzapp.view.LeaderboardScreen
import com.alibatuhanak.quizzapp.view.LoadingScreen
import com.alibatuhanak.quizzapp.view.LoginScreen
import com.alibatuhanak.quizzapp.view.PasswordSettingsScreen
import com.alibatuhanak.quizzapp.view.ProfileSettingsScreen
import com.alibatuhanak.quizzapp.view.QuizScreen
import com.alibatuhanak.quizzapp.view.RegisterScreen
import com.alibatuhanak.quizzapp.view.SettingsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun Navigation(navController: NavHostController, snackBarHostState: SnackbarHostState) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val auth = Firebase.auth
    val currentUser = auth.currentUser


    var startDestination by remember { mutableStateOf(Screen.LoadingScreen.route) }

    LaunchedEffect(Unit) {
        startDestination =
            if (currentUser == null) Screen.LoginScreen.route
            else Screen.HomeScreen.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.LoginScreen.route) {
            if (currentUser == null)
                LoginScreen(padding = null, navController)
            else goToHome(navController)

        }
        composable(Screen.RegisterScreen.route) { entry ->
            //RegisterScreen(name = entry.arguments?.getString("name"),navController)
            if (currentUser == null)
                RegisterScreen(navController, screenWidth, screenHeight)
            else goToHome(navController)
        }
        composable(Screen.LoadingScreen.route) {
            LoadingScreen()
        }

        composable(Screen.HomeScreen.route) {
             if (currentUser != null)
                HomeScreen(navController, screenWidth, screenHeight)
            else goToLogin(navController)
        }
        composable(Screen.QuizScreen.route + "/{categoryID}", arguments = listOf(
            navArgument(name = "categoryID"){
                type = NavType.StringType
                defaultValue="categoryID"
                nullable = true
            }
        )) { entry->
            if (currentUser != null)
                QuizScreen(category = entry.arguments?.getString("categoryID"),navController, screenWidth, screenHeight)
            else goToLogin(navController)
        }

        composable(Screen.EndLevelScreen.route+"/{correctCount}",arguments = listOf(
            navArgument(name = "correctCount"){
                type = NavType.StringType
                defaultValue="0"
                nullable = true
            }
        )){entry->
            if (currentUser != null)
                EndLevelScreen(correctCount = entry.arguments?.getString("correctCount"),navController, screenWidth, screenHeight)
            else goToLogin(navController)
        }

        composable(Screen.LeaderboardScreen.route) {
            if (currentUser != null)
                LeaderboardScreen(navController, screenWidth, screenHeight)
            else goToLogin(navController)
        }
        navigation(startDestination = Screen.SettingsScreen.route, route = "settings") {
            composable(Screen.SettingsScreen.route) {
                if (currentUser != null)
                    SettingsScreen(navController, screenWidth, screenHeight)
                else goToLogin(navController)
            }
            composable(Screen.ProfileSettingsScreen.route) {
                if (currentUser != null)
                    ProfileSettingsScreen(navController, screenWidth, screenHeight)
                else goToLogin(navController)
            }
            composable(Screen.EmailSettingsScreen.route) {
                if (currentUser != null)
                    EmailSettingsScreen(navController, screenWidth, screenHeight,snackBarHostState)
                else goToLogin(navController)
            }
            composable(Screen.PasswordSettingsScreen.route) {
                if (currentUser != null)
                    PasswordSettingsScreen(navController, screenWidth, screenHeight,snackBarHostState)
                else goToLogin(navController)
            }
        }


    }


}

private fun goToLogin(navController: NavHostController) {
    navController.navigate(Screen.LoginScreen.route)
}

private fun goToHome(navController: NavHostController) {
    navController.navigate(Screen.HomeScreen.route)
}
