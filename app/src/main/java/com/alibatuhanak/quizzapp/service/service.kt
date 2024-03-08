package com.alibatuhanak.quizzapp.service

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.view.HomeScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun authExist(currentUser: FirebaseUser?, navController: NavHostController?) {
    if (currentUser != null)
        navController?.navigate(Screen.HomeScreen.route) {
            popUpTo(0)
        }
}


fun signOut(currentUser: FirebaseUser?, navController: NavHostController?) {
        if (currentUser == null)
            navController?.navigate(Screen.LoginScreen.route) {
                popUpTo(0)
            }

}


val avatarList: List<Avatar> = listOf(
    Avatar(R.drawable.avatar1, 0, false),
    Avatar(R.drawable.avatar2, 1, false),
    Avatar(R.drawable.avatar3, 2, false),
    Avatar(R.drawable.avatar4, 3, false),
    Avatar(R.drawable.avatar5, 4, false),
    Avatar(R.drawable.avatar6, 5, false),
    Avatar(R.drawable.avatar7, 6, false),
    Avatar(R.drawable.avatar8, 7, false),


    )

data class Avatar(val avatarIcon: Int, val id: Int, var selected: Boolean)

val settingsList: List<Setting> = listOf(
    Setting(R.drawable.settings1, "Profile", "Manage your profile.") { n, p ->
        n.navigate(Screen.ProfileSettingsScreen.route) {
            popUpTo(Screen.SettingsScreen.route)
        }
    },
    Setting(R.drawable.settings2, "Notifications", "Change app notification settings.") { n, p -> },
    Setting(R.drawable.settings4, "Data Usage", "Limit data usage to wifi only.") { n, p -> },
    Setting(R.drawable.settings5, "Analytics", "See your analytics.") { n, p -> },
    Setting(R.drawable.settings3, "Dark Mode", "Turn on to see the magic.") { n, p -> },
    Setting(R.drawable.settings6, "Quit", "Log out.") { n, p ->
        signOut(n,p)
    },
)

fun signOut(n:NavHostController,p:SharedPreference){
    val auth = Firebase.auth
    auth.signOut()
    p.clearAllData()
    n.navigate(Screen.LoginScreen.route) {
        popUpTo(0)
    }
}

data class Setting(
    val settingIcon: Int,
    val title: String,
    val text: String,
    val option: (navController: NavHostController, preferencesManager: SharedPreference) -> Unit
)

@Composable
fun TextKey (input:String,onInputChange: (String)->Unit,typeOfField:String){

}