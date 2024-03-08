package com.alibatuhanak.quizzapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.rememberImeState
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun RegisterScreen(navController: NavHostController?, screen: Any, screenHeight: Dp) {
    val db = Firebase.firestore
    val auth: FirebaseAuth = Firebase.auth
    val usersRef = db.collection("users")

    val email = remember {
        mutableStateOf("")
    }
    val username = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val rePassword = remember {
        mutableStateOf("")
    }

    val sizeOfUsers = remember {
        mutableIntStateOf(0)
    }

    val scope = rememberCoroutineScope() // Create a coroutine scope

    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(imeState.value ){
        if(imeState.value){
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(44.dp).verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(
            17.dp, alignment = Alignment.Top
        ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(
                5.dp, alignment = Alignment.Top
            ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(5.dp)
                    .size(125.dp),
                bitmap = ImageBitmap.imageResource(id = R.drawable.cleaning),
                contentDescription = "login_image"
            )
            TextInput(input = email.value, typeOfField = "email") {
                email.value = it
            }

            TextInput(input = username.value, typeOfField = "username", onInputChange = {
                username.value = it
            })
            TextInput(input = password.value, typeOfField = "password", onInputChange = {
                password.value = it
            })
            TextInput(input = rePassword.value, typeOfField = "repassword", onInputChange = {
                rePassword.value = it
            })

            SignButton("REGISTER") { context, preferenceManager ->
                if (email.value.trim().isEmpty()) {
                    Toast.makeText(
                        context,
                        "Email can't be empty.Try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (username.value.trim().length < 6) {
                    Toast.makeText(
                        context,
                        "Username should be at least 6 characters. Try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (password.value != rePassword.value || rePassword.value.length < 6) {
                    Toast.makeText(
                        context,
                        "Passwords don't match. Try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {

                    scope.launch(Dispatchers.IO) {


                        usersRef.whereEqualTo("username", username.value).get()
                            .addOnSuccessListener {
                                if (it.documents.isEmpty()) {
                                    createUser(
                                        navController,
                                        auth,
                                        usersRef,
                                        context,
                                        email,
                                        password,
                                        username,
                                        preferenceManager,
                                        sizeOfUsers
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Username already have. Try again!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    it.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                }

            }
        }


        /*  if (screenHeight >= 850.dp) {
              DividerSection(modifier = Modifier.padding(top = 80.dp), navController)
          } else if (screenHeight >= 800.dp) {
              DividerSection(modifier = Modifier.padding(top = 50.dp), navController)
          } else if (screenHeight >= 750.dp) {
              DividerSection(modifier = Modifier.padding(top = 20.dp), navController)
          } else {
              DividerSection(modifier = Modifier, navController)
          }*/
        DividerSection(
            modifier = Modifier.padding(
                top = when {
                    screenHeight >= 850.dp -> 80.dp
                    screenHeight > 800.dp -> 50.dp
                    screenHeight > 750.dp -> 20.dp
                    else -> 0.dp
                }
            ), navController
        )


    }

}

fun createUser(
    navController: NavHostController?,
    auth: FirebaseAuth,
    usersRef: CollectionReference,
    context: Context,
    email: MutableState<String>,
    password: MutableState<String>,
    username: MutableState<String>,
    preferencesManager: SharedPreference,
    sizeOfUsers: MutableIntState
) {

    auth.createUserWithEmailAndPassword(email.value, password.value)
        .addOnSuccessListener {
            usersRef.get().addOnSuccessListener { all ->
                sizeOfUsers.intValue = all.documents.size + 1
                val userInforms: HashMap<String, Any> = hashMapOf()


                userInforms["email"] = email.value
                userInforms["username"] = username.value

                val userEmail = userInforms["email"] as String
                val userUsername = userInforms["username"] as String

                val user = Models.UserModel(
                    name = "",
                    username = userUsername,
                    email = userEmail.lowercase(),
                    point = 0,
                    score = hashMapOf("true" to 0,"false" to 0),
                    rank = sizeOfUsers.intValue,
                    avatarIcon = R.drawable.avatar1,
                    timestamp = Timestamp.now()
                )

                usersRef
                    .add(user)
                    .addOnSuccessListener {userDoc->
                        val profileUpdates = userProfileChangeRequest {
                            displayName = user.username
                        }

                        auth.currentUser?.updateProfile(profileUpdates)
                            ?.addOnSuccessListener {

                                preferencesManager.saveData("username", user.username)
                                preferencesManager.saveData("email", user.email)
                                preferencesManager.saveData("documentID", userDoc.id)
                                preferencesManager.saveNumbers("point", user.point.toInt())
                                preferencesManager.saveNumbers("rank", user.rank.toInt())
                                preferencesManager.saveNumbers("avatar", user.avatarIcon.toInt())

                                Toast.makeText(
                                    context,
                                    "Welcome, ${user.username}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController?.navigate(Screen.HomeScreen.route) {
                                    popUpTo(0)
                                }
                            }?.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    it.localizedMessage,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            it.localizedMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    it.localizedMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
}


@Composable
fun DividerSection(modifier: Modifier, navController: NavHostController?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {

        Divider(
            color = Color.DarkGray.copy(alpha = .3f), thickness = 1.2.dp,
            modifier = Modifier.padding(
                top = 25.dp,
                bottom = 5.dp
            )
        )
        CheckRow(navController = navController, b = true)
    }
}

