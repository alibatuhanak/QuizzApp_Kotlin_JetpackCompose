package com.alibatuhanak.quizzapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alibatuhanak.quizzapp.navigation.Navigation
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.ui.theme.QuizzAppTheme
import com.alibatuhanak.quizzapp.view.BotBar
import com.alibatuhanak.quizzapp.view.TopBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContent {

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            val currentDestination = navBackStackEntry?.destination

            val snackBarHostState = remember {
                SnackbarHostState()
            }

            QuizzAppTheme {

                // A surface container using the 'background' color from the theme
                Scaffold(snackbarHost = {
                    if (currentDestination?.route == Screen.PasswordSettingsScreen.route)
                        SnackbarHost(hostState = snackBarHostState)
                }, topBar = {
                    TopBar(navController)
                }, bottomBar = {
                    when (navController.currentDestination?.route?.split("/")?.get(0)) {
                        Screen.LoginScreen.route -> {
                        }

                        Screen.RegisterScreen.route -> {
                        }

                        Screen.LoadingScreen.route -> {
                        }

                        Screen.ProfileSettingsScreen.route -> {

                        }

                        Screen.PasswordSettingsScreen.route -> {

                        }
                        Screen.EmailSettingsScreen.route -> {

                        }
                        Screen.QuizScreen.route -> {
                        }
                        Screen.EndLevelScreen.route -> {
                        }

                        else -> {
                            BotBar(navController)
                        }

                    }


                }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        color = Color(0xFFf5f5dc)
                    ) {
                        Navigation(navController,snackBarHostState)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}