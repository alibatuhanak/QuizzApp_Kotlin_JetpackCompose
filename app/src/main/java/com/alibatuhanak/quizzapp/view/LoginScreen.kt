package com.alibatuhanak.quizzapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.ui.theme.QuizzAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore


@Composable
fun LoginScreen(padding: PaddingValues?, navController: NavHostController?) {
    val auth = Firebase.auth

    val email = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    val name =
        remember { mutableStateOf("") }

    val username =
        remember { mutableStateOf("") }


    val point: MutableState<Number> =
        remember { mutableStateOf(0) }

    val rank: MutableState<Number> =
        remember { mutableStateOf(0) }

    val documentID =
        remember { mutableStateOf("") }

    val selectedAvatar: MutableState<Number> =
        remember {
            mutableStateOf(
                R.drawable.avatar5
            )
        }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(44.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            10.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Image(
            modifier = Modifier.padding(bottom = 15.dp, top = 35.dp),
            bitmap = ImageBitmap.imageResource(id = R.drawable.login),
            contentDescription = "login_image"
        )
        TextInput(email.value, "email") {
            email.value = it.trim()
        }
        TextInput(password.value, "password") {
            password.value = it
        }
        SignButton("SIGN IN") { context, preferencesManager ->
            if (email.value.trim().isEmpty()) {
                Toast.makeText(
                    context,
                    "Email can't be empty.Try again!",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (password.value.length < 6) {
                Toast.makeText(
                    context,
                    "Password can't be less than 6.Try again!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {


                auth.signInWithEmailAndPassword(email.value.lowercase(), password.value)
                    .addOnSuccessListener { user ->

                        fetchUserData(
                            user,
                            context,
                            preferencesManager,
                            email,
                            username,
                            name,
                            rank,
                            point,
                            documentID,
                            selectedAvatar,
                            navController
                        )

                    }.addOnFailureListener {
                        Toast.makeText(
                            context,
                            it.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        Divider(
            color = Color.DarkGray.copy(alpha = .3f), thickness = 1.2.dp,
            modifier = Modifier.padding(
                top = 60.dp
            )
        )
        CheckRow(navController, false)
    }
}

fun fetchUserData(
    user: AuthResult,
    context: Context,
    preferencesManager: SharedPreference,
    email: MutableState<String>,
    username: MutableState<String>,
    name: MutableState<String>,
    rank: MutableState<Number>,
    point: MutableState<Number>,
    documentID: MutableState<String>,
    selectedAvatar: MutableState<Number>,
    navController: NavHostController?
) {
    val db = Firebase.firestore

    db.collection("users").whereEqualTo("username", user.user?.displayName).get()
        .addOnSuccessListener { mainUser ->


            if (mainUser.documents.isNotEmpty()) {

                for (d in mainUser.documents) {
                    if (email.value != d.get("email") as String) {
                        db.collection("users").document(d.id).update("email", email.value)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Email has changed successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    it.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    name.value = d.get("name") as String
                    username.value = d.get("username") as String
                    point.value = d.get("point") as Number
                    rank.value = d.get("rank") as Number
                    documentID.value = d.id
                    selectedAvatar.value = d.get("avatarIcon") as Number

                }
                val userModel = Models.Companion.UserModel(
                    name = name.value,
                    username = username.value,
                    email = email.value,
                    point = point.value,
                    rank = rank.value,
                    documentID = documentID.value,
                    avatarIcon = selectedAvatar.value
                )


                preferencesManager.saveData("name", userModel.name)
                preferencesManager.saveData("username", userModel.username)
                preferencesManager.saveData("email", userModel.email)
                preferencesManager.saveData("documentID", userModel.documentID)
                preferencesManager.saveNumbers("point", point.value.toInt())
                preferencesManager.saveNumbers("rank", rank.value.toInt())
                preferencesManager.saveNumbers("avatar", selectedAvatar.value.toInt())


                Toast.makeText(
                    context,
                    "Welcome, ${username.value}",
                    Toast.LENGTH_SHORT
                ).show()

                navController?.navigate(Screen.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(
                context,
                it.localizedMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
}


@Composable
fun SignButton(s: String, sign: (Context, SharedPreference) -> Unit) {
    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }

    Button(
        onClick = {
            sign.invoke(context, preferencesManager)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A334))
    ) {
        Text(text = s, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

    }

}


@Composable
fun CheckRow(navController: NavHostController?, b: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!b) {
            Text(text = "Don't have an account?", color = Color(0xFF067C06))
            //TextButton(onClick = { navController?.navigate(Screen.RegisterScreen.withArgs("ali")) }) {
            TextButton(onClick = {
                navController?.navigate(Screen.RegisterScreen.route)
            }) {
                Text(text = "SIGN UP", color = Color(0xB4AD1538))
            }
        } else {
            Text(text = "Already have an account?", color = Color(0xFF067C06))
            TextButton(onClick = {
                navController?.navigate(Screen.LoginScreen.route) {
                    popUpTo(0)
                }

            }
            ) {
                Text(text = "SIGN IN", color = Color(0xB4AD1538))
            }
        }
    }
}

@Composable
fun TextInput(input: String, typeOfField: String, onInputChange: (String) -> Unit) {
    var visualTransformation: VisualTransformation = VisualTransformation.None
    var imageVector: ImageVector = Icons.Default.Edit
    var textField: String = "username"
    when (typeOfField) {
        "username" -> {
            visualTransformation = VisualTransformation.None
            textField = "Enter username"
            imageVector = Icons.Default.Person
        }

        "email" -> {
            visualTransformation = VisualTransformation.None
            textField = "Enter email"
            imageVector = Icons.Default.Email
        }

        "password" -> {
            visualTransformation = PasswordVisualTransformation()
            textField = "Enter password"
            imageVector = Icons.Default.Lock
        }

        "repassword" -> {
            visualTransformation = PasswordVisualTransformation()
            textField = "Confirm password"
            imageVector = Icons.Default.Lock
        }

        else -> {
        }
    }
    OutlinedTextField(
        value = input,
        visualTransformation = visualTransformation,
        onValueChange = onInputChange,
        label = {
            Text(text = textField)
        }, textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ), trailingIcon = {
            Icon(imageVector = imageVector, contentDescription = null)
        }, colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFF067C06),
            focusedBorderColor = Color(0xFF067C06),
            unfocusedLabelColor = Color(0xFF067C06),
            focusedLabelColor = Color(0xFF067C06),
            unfocusedTrailingIconColor = Color(0xFF067C06),
            focusedTrailingIconColor = Color(0xFF067C06),
            cursorColor = Color(0xFF067C06),
            unfocusedTextColor = Color(0xED000000),
            focusedTextColor = Color(0xFF067C06),
            focusedSupportingTextColor = Color(0xFF067C06),
            selectionColors = TextSelectionColors(Color(0xFF067C06), Color(0xFF8EB88E))
        )
    )

}


@Preview
@Composable
fun Shows2() {
    QuizzAppTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(topBar = { TopBar(null) }) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background
            ) {
                LoginScreen(null, null)
            }
        }
    }
}

