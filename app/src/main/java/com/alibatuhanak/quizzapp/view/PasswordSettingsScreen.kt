package com.alibatuhanak.quizzapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.signOut
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordSettingsScreen(
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp,
    snackBarHostState: SnackbarHostState
) {
    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }


    val password = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val reNewPassword = remember { mutableStateOf("") }


    val editableMap = remember {
        mutableStateMapOf<String, Boolean>()
    }


    val scope = rememberCoroutineScope()
    val timerRunning = remember { mutableStateOf(false) }
    val snackBar = remember {
        mutableStateOf(false)
    }

    val keyboard = LocalSoftwareKeyboardController.current


    LaunchedEffect(snackBar.value) {
        editableMap["password"] = true

        if (snackBar.value) {

            scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = "Password successfully changed. You will redirect to login page.",
                    actionLabel = "Yes",
                    withDismissAction = true
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        signOut(navController, preferencesManager)
                    }

                    SnackbarResult.Dismissed -> {}
                    else -> {}
                }
            }
        }
    }

    if (timerRunning.value) {
        CountdownTimer(
            initialSeconds = 3,
            onFinish = {
                signOut(navController, preferencesManager)
            },
            dispatcher = Dispatchers.Main
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        TextLabel(
            name = "Old Password",
            typeOfField = "password",
            nameState = password,
            editableMap = editableMap
        )
        TextLabel(
            name = "New Password",
            typeOfField = "password",
            nameState = newPassword,
            editableMap = editableMap
        )
        TextLabel(
            name = "Re-enter New Password",
            typeOfField = "password",
            nameState = reNewPassword,
            editableMap = editableMap
        )
        Spacer(modifier = Modifier)
        Text(
            text = "*Password should be at least 6 characters.",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF06776D)
        )
        Spacer(modifier = Modifier.height(20.dp))
        SaveButton(
            currentUser = currentUser,
            context = context,
            preferencesManager = preferencesManager,
            navController = navController,
            password = password,
            newPassword = newPassword,
            reNewPassword = reNewPassword,
            timerRunning = timerRunning,
            snackBar = snackBar,
            scope = scope,
            snackBarHostState = snackBarHostState,
            keyboard = keyboard
        )

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SaveButton(
    currentUser: FirebaseUser?,
    context: Context,
    preferencesManager: SharedPreference,
    password: MutableState<String>,
    newPassword: MutableState<String>,
    reNewPassword: MutableState<String>,
    navController: NavHostController,
    timerRunning: MutableState<Boolean>,
    snackBar: MutableState<Boolean>,
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    keyboard: SoftwareKeyboardController?

) {
    Button(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10),
        onClick = {
            keyboard?.hide()
            changePassword(
                currentUser = currentUser,
                context = context,
                preferencesManager = preferencesManager,
                navController = navController,
                password = password,
                newPassword = newPassword,
                reNewPassword = reNewPassword,
                timerRunning = timerRunning,
                snackBar = snackBar
            )

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF067C06),
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Save changes",

            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
        )
    }
}


fun changePassword(
    currentUser: FirebaseUser?,
    context: Context,
    password: MutableState<String>,
    newPassword: MutableState<String>,
    reNewPassword: MutableState<String>,
    preferencesManager: SharedPreference,
    navController: NavHostController,
    timerRunning: MutableState<Boolean>,
    snackBar: MutableState<Boolean>

) {
    currentUser?.email?.let { email ->
        val credential = EmailAuthProvider.getCredential(email, password.value)
        currentUser.reauthenticate(credential).addOnSuccessListener {
            if (newPassword.value == reNewPassword.value) {
                if (password.value == newPassword.value) {
                    Toast.makeText(
                        context,
                        "New Password shouldn't be same the old password!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    currentUser.updatePassword(newPassword.value).addOnSuccessListener {
                        snackBar.value = true
                        timerRunning.value = true

                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            it.localizedMessage?.split(".")?.get(1) ?: it.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else
                Toast.makeText(context, "Passwords does not match. Try again!", Toast.LENGTH_SHORT)
                    .show()


        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
        }

    } ?: Toast.makeText(
        context,
        "We can not loading user information.Sorry try again!",
        Toast.LENGTH_LONG
    ).show()


}