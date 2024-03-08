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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.signOut
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailSettingsScreen(
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }
    val currentUser = Firebase.auth.currentUser

    val editableMap = remember {
        mutableStateMapOf<String, Boolean>()
    }

    val email = remember { mutableStateOf(preferencesManager.getData("email", "")) }
    val newEmail = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val timerRunning = remember { mutableStateOf(false) }
    val snackBar = remember {
        mutableStateOf(false)
    }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(snackBar.value) {
        editableMap["email"] = false
        editableMap["newEmail"] = true
        editableMap["password"] = true

        if (snackBar.value) {
            scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = "We sent mail to new email address for confirm. You will redirect to login page.",
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
            name = "Email",
            typeOfField = "email",
            nameState = email,
            editableMap = editableMap
        )
        TextLabel(
            name = "New Email",
            typeOfField = "newEmail",
            nameState = newEmail,
            editableMap = editableMap
        )

        TextLabel(
            name = "Password",
            typeOfField = "password",
            nameState = password,
            editableMap = editableMap
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .height(45.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10),
            onClick = {
                keyboard?.hide()
                changeEmail(email, newEmail, context, password, currentUser,preferencesManager,snackBar,timerRunning)
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

}              /*     preferencesManager.saveData(
                                         "email",
                                         user["email"] as String
                                     )*/

private fun changeEmail(
    email: MutableState<String>,
    newEmail: MutableState<String>,
    context: Context,
    password: MutableState<String>,
    currentUser: FirebaseUser?,
    preferencesManager: SharedPreference,
    snackBar: MutableState<Boolean>,
    timerRunning: MutableState<Boolean>
) {
    val credential = EmailAuthProvider.getCredential(email.value, password.value)
    if ((email.value != newEmail.value))
        currentUser?.reauthenticate(credential)?.addOnSuccessListener {

            currentUser.verifyBeforeUpdateEmail(newEmail.value)
                .addOnSuccessListener {
                    //database de login ypatıktan sonra güncelle
                    snackBar.value = true
                    timerRunning.value = true

                }.addOnFailureListener {

                    Toast.makeText(
                        context,
                        it.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }?.addOnFailureListener {
            Toast.makeText(
                context,
                it.localizedMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    else Toast.makeText(
        context,
        "Does not change anything!",
        Toast.LENGTH_SHORT
    ).show()
}

