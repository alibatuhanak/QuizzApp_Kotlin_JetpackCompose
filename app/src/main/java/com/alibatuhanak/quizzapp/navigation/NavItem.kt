package com.alibatuhanak.quizzapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.alibatuhanak.quizzapp.R

data class NavItem(
    val label: String,
    val selectedIcon: Any,
    val unselectedIcon: Any,
    val route: String
)


val listOfNavItems = listOf<NavItem>(
    NavItem(label="Home",selectedIcon= Icons.Default.Home,unselectedIcon= Icons.Outlined.Home,route = Screen.HomeScreen.route),
    NavItem(label="Leaderboard",selectedIcon= R.drawable.baseline_leaderboard_24,unselectedIcon= R.drawable.baseline_leaderboard_24,route = Screen.LeaderboardScreen.route),
    NavItem(label="Settings",selectedIcon= Icons.Default.Settings,unselectedIcon= Icons.Outlined.Settings,route = "settings")
)