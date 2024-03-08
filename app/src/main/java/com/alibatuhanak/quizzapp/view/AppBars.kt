package com.alibatuhanak.quizzapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.navigation.listOfNavItems
import com.alibatuhanak.quizzapp.service.Avatar
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.avatarList
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController?) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val currentRoute = navController
        ?.currentBackStackEntryFlow
        ?.collectAsState(initial = navController.currentBackStackEntry)

    val currentRouteValue = currentRoute?.value?.destination?.route?.split("/")?.get(0)

    var showBackButton by remember {
        mutableStateOf(false)
    }
    var showProfile by remember {
        mutableStateOf(false)
    }
    var title by remember {
        mutableStateOf("Quizz App")
    }
    val preferencesManager = remember { SharedPreference(context) }





    showBackButton =
        when (currentRouteValue) {
            Screen.HomeScreen.route -> false
            Screen.LeaderboardScreen.route -> false
            Screen.SettingsScreen.route -> false
            Screen.QuizScreen.route -> false
            Screen.LoginScreen.route -> false
            Screen.LoadingScreen.route -> false
            Screen.EndLevelScreen.route -> false
            else -> true

        }
    showProfile =
        when (currentRouteValue) {
            Screen.HomeScreen.route -> true
            else -> false
        }

    title =
        when (currentRouteValue) {
            Screen.QuizScreen.route -> preferencesManager.getData("categoryName", "Quiz Game")
            Screen.LeaderboardScreen.route -> "Leaderboard"
            Screen.SettingsScreen.route -> "Settings"
            Screen.LoadingScreen.route -> ""
            Screen.ProfileSettingsScreen.route -> "Profile"
            Screen.EmailSettingsScreen.route -> "Email"
            Screen.PasswordSettingsScreen.route -> "Password"
            Screen.EndLevelScreen.route -> "End of Quizz"
            else -> "Quizz App"
        }

    CenterAlignedTopAppBar(modifier = Modifier,
        title = {
            if (showProfile) {
                return@CenterAlignedTopAppBar
            } else {
                Text(
                    text = title, color = Color.Black, fontSize = 29.sp, fontFamily = FontFamily(
                        Font(R.font.pacifico_regular, FontWeight.Bold)
                    ), modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }, navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = {
                    navController!!.popBackStack()
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else if (showProfile) {
                val username =
                    remember { mutableStateOf(preferencesManager.getData("username", "")) }
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                            )
                        ) {
                            append("Hello, ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 22.sp
                            )
                        ) {
                            append(
                                username.value
                            )
                        }
                    },
                    fontFamily = FontFamily(
                        Font(R.font.pacifico_regular)
                    ),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 5.dp, bottom = 10.dp)
                )
            }
        },
        actions = {
            if (showProfile) {
                val selectedAvatar: MutableState<Number> =
                    remember {
                        mutableStateOf(
                            preferencesManager.getData(
                                "avatar",
                                R.drawable.avatar5
                            )

                        )
                    }

                val dialogBoolean = remember { mutableStateOf(false) }


                if (dialogBoolean.value) DialogAvatar(
                    dialogBoolean,
                    screenWidth,
                    screenHeight,
                    selectedAvatar,
                    preferencesManager
                )
                IconButton(
                    onClick = { dialogBoolean.value = true },
                    modifier = Modifier.offset(x = (-10).dp)
                ) {
                    Image(
                        painter = painterResource(id = selectedAvatar.value as Int),
                        contentDescription = null,
                        modifier = Modifier

                    )
                }


            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFf5f5dc),
            titleContentColor = Color.Black,
        )
    )
}


@Composable
fun BotBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (currentDestination?.route != Screen.QuizScreen.route)
        NavigationBar(
            containerColor = Color(0xFFf5f5dc),
            modifier = Modifier.drawBehind {
                val strokeWidth = 10f
                val x = size.width - strokeWidth
                val y = size.height - strokeWidth

                drawLine(
                    color = Color(0xB981C784),
                    start = Offset(0f, 0f),
                    end = Offset(x, 0f),
                    strokeWidth = strokeWidth
                )
            },
        ) {

            listOfNavItems.forEach { navItem ->

                NavigationBarItem(selected = currentDestination?.hierarchy?.any {
                    it.route == navItem.route
                } == true, onClick = {

                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                        //todo bunlara chatgpt den tekrar bak ve profile settings todo bak
                    }
                }, icon = {
                    Icon(
                        imageVector =
                        if (currentDestination?.hierarchy?.any { it.route == navItem.route } == true)
                            if (navItem.selectedIcon is Int)
                                ImageVector.vectorResource(id = navItem.selectedIcon)
                            else
                                navItem.selectedIcon as ImageVector
                        else
                            if (navItem.unselectedIcon is Int)
                                ImageVector.vectorResource(id = navItem.unselectedIcon)
                            else
                                navItem.unselectedIcon as ImageVector,
                        contentDescription = null
                    )
                }, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF329932),
                    unselectedIconColor = Color.DarkGray,
                    indicatorColor = Color(0xBADCFEDC),
                )
                )
            }
        }
}


@Composable
fun DialogAvatar(
    dialogBoolean: MutableState<Boolean>,
    width: Dp,
    height: Dp,
    selectedAvatar: MutableState<Number>,
    preferencesManager: SharedPreference
) {

    val documentID = remember { mutableStateOf(preferencesManager.getData("documentID", "")) }

    val db = Firebase.firestore
    val usersRef = db.collection("users")

    val selectedAvatarList = remember {
        mutableStateListOf<Avatar>()
    }
    when {
        dialogBoolean.value -> {
            DialogWithAvatar(
                onDismissRequest = {
                    dialogBoolean.value = false
                    selectedAvatar.value = selectedAvatarList.single {
                        it.selected
                    }.avatarIcon

                    usersRef.document(documentID.value).update("avatarIcon", selectedAvatar.value)

                },
                onConfirmation = {
                    dialogBoolean.value = false
                },
                "",
                avatarList,
                selectedAvatarList,
                width,
                height,
                selectedAvatar,
                preferencesManager

            )
        }
    }
}


@Composable
fun DialogWithAvatar(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    imageDescription: String,
    avatarList: List<Avatar>,
    selectedAvatarList: SnapshotStateList<Avatar>,
    width: Dp,
    height: Dp,
    selectedAvatar: MutableState<Number>,
    preferencesManager: SharedPreference

) {
    Dialog(onDismissRequest = { onDismissRequest() }) {

        selectedAvatarList.removeAll(avatarList)
        selectedAvatarList.addAll(avatarList)


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * 2 / 3),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF3C853C)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Select your avatar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(),
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                    columns = GridCells.Adaptive(minSize = width / 3)
                ) {
                    items(selectedAvatarList) { avatar ->

                        if (selectedAvatar.value == avatar.avatarIcon) {
                            avatar.selected = true
                        }
                        Image(
                            painter = painterResource(id = avatar.avatarIcon),
                            contentDescription = imageDescription,
                            modifier = Modifier
                                .size(80.dp)
                                .offset(y = 5.dp)
                                .clickable {
                                    selectedAvatarList.map {
                                        it.selected = false

                                        if (it.id == avatar.id) {
                                            avatar.selected = !avatar.selected

                                            selectedAvatar.value = avatar.avatarIcon
                                            preferencesManager.saveNumbers(
                                                "avatar",
                                                avatar.avatarIcon
                                            )


                                        }


                                    }
                                    selectedAvatarList.add(Avatar(1, 1, false))
                                    selectedAvatarList.remove(Avatar(1, 1, false))
                                }
                        )
                        if (avatar.selected)

                            Image(
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset(y = (-10).dp),
                                painter = painterResource(id = R.drawable.crown),
                                contentDescription = null
                            )


                    }
                }
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(
                        text = "OK",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                }


            }


        }
    }
}