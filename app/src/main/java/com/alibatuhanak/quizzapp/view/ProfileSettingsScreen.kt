package com.alibatuhanak.quizzapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.rememberImeState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore


@Composable
fun ProfileSettingsScreen(navController: NavHostController, screenWidth: Dp, screenHeight: Dp) {
    val db = Firebase.firestore
    val usersRef = db.collection("users")
    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }

    val documentID = remember { mutableStateOf(preferencesManager.getData("documentID", "")) }

    val editableMap = remember {
        mutableStateMapOf<String, Boolean>()
    }
    val imeState = rememberImeState()
    val scrollState = rememberScrollState()

    LaunchedEffect(imeState.value) {
        if (imeState.value) {
            scrollState.animateScrollTo(scrollState.maxValue, tween(300))
        }
    }


    val name = remember { mutableStateOf(preferencesManager.getData("name", "")) }
    val username = remember { mutableStateOf(preferencesManager.getData("username", "")) }
    val email = remember { mutableStateOf(preferencesManager.getData("email", "")) }
    val avatarIcon: MutableState<Number> =
        remember { mutableStateOf(preferencesManager.getData("avatar", R.drawable.avatar1)) }


    val checkUser = remember {
        mutableStateMapOf<String, Any>()
    }

    val user = remember {
        mutableStateMapOf<String, Any>()
    }


    LaunchedEffect(Unit) {
        editableMap["name"] = false
        editableMap["username"] = false
        checkUser["name"] = name.value
        checkUser["username"] = username.value

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(
            when {
                screenHeight >= 850.dp -> 60.dp
                screenHeight >= 800.dp -> 30.dp
                screenHeight >= 750.dp -> 20.dp
                screenHeight >= 700.dp -> 15.dp
                else -> 10.dp
            }, alignment = Alignment.Top
        ),
        horizontalAlignment = Alignment.Start
    ) {

        AvatarSection(avatarIcon, preferencesManager, screenWidth, screenHeight)
        editableMap["name"]?.let {
            EditLabels(
                name = name,
                username = username,
                email = email,
                editableMap = editableMap,
                documentID = documentID,
                context = context,
                navController = navController,
                checkUser = checkUser,
                user = user,
                preferencesManager = preferencesManager,
            )
        }

    }


}


@Composable
fun EditLabels(
    name: MutableState<String>,
    username: MutableState<String>,
    email: MutableState<String>,
    editableMap: SnapshotStateMap<String, Boolean>,
    documentID: MutableState<String>,
    context: Context,
    navController: NavHostController,
    checkUser: SnapshotStateMap<String, Any>,
    user: SnapshotStateMap<String, Any>,
    preferencesManager: SharedPreference,

    ) {
    val db = Firebase.firestore
    val usersRef = db.collection("users")

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            15.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.Start
    ) {
        SetAccountInform(
            navController = navController,
            Screen.EmailSettingsScreen.route,
            email.value,
            "Email"
        )
        SetAccountInform(
            navController = navController,
            Screen.PasswordSettingsScreen.route,
            "Change your password",
            "Password"
        )

        EditTextLabel(
            name = "Name",
            typeOfField = "name",
            nameState = name,
            editableMap = editableMap,
            context = context,
            preferencesManager = preferencesManager,
            checkUser = checkUser,
            user = user,
            usersRef = usersRef,
            documentID = documentID
        )
        EditTextLabel(
            name = "Username",
            typeOfField = "username",
            nameState = username,
            editableMap = editableMap,
            context = context,
            preferencesManager = preferencesManager,
            checkUser = checkUser,
            user = user,
            usersRef = usersRef,
            documentID = documentID
        )
        Spacer(modifier = Modifier)
        DeleteAccount()
    }
}

@Composable
fun DeleteAccount() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            modifier = Modifier
                .border(1.dp, Color.Red, RoundedCornerShape(50))
                .padding(bottom = 3.dp),
            onClick = { }) {
            Text(
                text = "Delete your account",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Red
            )

        }
    }
}

@Composable
fun EditButton(
    editableMap: SnapshotStateMap<String, Boolean>,
    preferencesManager: SharedPreference,
    context: Context,
    user: SnapshotStateMap<String, Any>,
    checkUser: SnapshotStateMap<String, Any>,
    nameState: MutableState<String>,
    typeOfField: String,
    usersRef: CollectionReference,
    documentID: MutableState<String>,
) {
    val currentUser = Firebase.auth.currentUser


    if (!editableMap[typeOfField]!!)
        TextButton(
            modifier = Modifier,
            /*   colors = ButtonDefaults.buttonColors(
                   containerColor = Color(0xFF067C06),
                   contentColor = Color.White
               ), shape = RoundedCornerShape(15),*/
            onClick = {
                editableMap[typeOfField] = !editableMap[typeOfField]!!
            }) {
            Text(
                text = "Edit",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.Edit,
                tint = Color.Black,
                contentDescription = null
            )
        }
    else TextButton(
        modifier = Modifier,
        /*            colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF067C06),
                        contentColor = Color.White
                    ), shape = RoundedCornerShape(15),*/
        onClick = {


            user[typeOfField] = nameState.value

            if ((checkUser[typeOfField] != user[typeOfField]))
                usersRef.whereEqualTo(typeOfField, nameState.value).get().addOnSuccessListener {
                    if (it.documents.isEmpty()) {
                        usersRef.document(documentID.value).update(user)
                            .addOnSuccessListener {
                                checkUser[typeOfField] = user[typeOfField] as String
                                preferencesManager.saveData(
                                    typeOfField,
                                    user[typeOfField] as String
                                )
                                if (typeOfField == "username") {

                                    val profileUpdates = userProfileChangeRequest {
                                        displayName = user[typeOfField] as String
                                    }
                                    currentUser?.updateProfile(profileUpdates)
                                }
                                Toast.makeText(
                                    context,
                                    "Successfully saved!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                editableMap[typeOfField] = !editableMap[typeOfField]!!
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    e.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else Toast.makeText(
                        context,
                        "$typeOfField already have!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            else {
                editableMap[typeOfField] = !editableMap[typeOfField]!!
                Toast.makeText(
                    context,
                    "Does not change anything!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }) {
        Text(
            text = "Save Changes",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(imageVector = Icons.Filled.Done, tint = Color.Black, contentDescription = null)
    }

}


@Composable
fun AvatarSection(
    avatarIcon: MutableState<Number>,
    preferencesManager: SharedPreference,
    screenWidth: Dp,
    screenHeight: Dp
) {
 /*   val selectedAvatar: MutableState<Number> =
        remember {
            mutableStateOf(
                preferencesManager.getData(
                    "avatar",
                    R.drawable.avatar5
                )

            )
        }*/
    val dialogBoolean = remember { mutableStateOf(false) }


    if (dialogBoolean.value)
        DialogAvatar(
            dialogBoolean,
            screenWidth,
            screenHeight,
            avatarIcon,
            preferencesManager
        )

    Row {

        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = avatarIcon.value as Int),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(11.dp))
        Column {
            Text(
                text = "Update your Avatar",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            /*       Text(
                       text = "Upload a photo under 1 MB",
                       color = Color.DarkGray,
                       fontWeight = FontWeight.Normal,
                       fontSize = 15.sp
                   )*/
            Spacer(modifier = Modifier.height(12.dp))
            Row {

                Button(
                    modifier = Modifier,
                    onClick = {
                        dialogBoolean.value = true
                    },
                    shape = RoundedCornerShape(10),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF067C06),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.upload),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Update",
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditTextLabel(
    name: String, typeOfField: String, nameState: MutableState<String>,
    editableMap: SnapshotStateMap<String, Boolean>,
    context: Context,
    preferencesManager: SharedPreference,
    checkUser: SnapshotStateMap<String, Any>,
    user: SnapshotStateMap<String, Any>,
    usersRef: CollectionReference,
    documentID: MutableState<String>,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            EditButton(
                editableMap = editableMap,
                preferencesManager = preferencesManager,
                context = context,
                user = user,
                checkUser = checkUser,
                nameState = nameState,
                typeOfField = typeOfField,
                usersRef = usersRef,
                documentID = documentID
            )


        }
        TextLabel(
            name = null,
            typeOfField = typeOfField,
            nameState = nameState,
            editableMap = editableMap,
        )
    }
}

@Composable
fun TextLabel(
    name: String?,
    typeOfField: String,
    nameState: MutableState<String>,
    editableMap: SnapshotStateMap<String, Boolean>?,
) {
    val visualTransformation = if (typeOfField != "password") {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        name?.let {
            Text(text = name, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))
        editableMap?.get(typeOfField)?.let { editable ->
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = nameState.value,
                onValueChange = { nameState.value = it },
                enabled = editable,
                visualTransformation = visualTransformation,
                trailingIcon = {
                    when (typeOfField) {
                        "username" -> Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )

                        "name" -> Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null
                        )

                        "email" -> Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )

                        "newEmail" -> Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )

                        "password" -> Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )

                    }
                }, textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ), colors = OutlinedTextFieldDefaults.colors(
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
                    disabledBorderColor = Color(0x60067C06),
                    disabledTextColor = Color(0x60067C06),
                    disabledTrailingIconColor = Color(0x60067C06),
                    selectionColors = TextSelectionColors(Color(0xFF067C06), Color(0xFF8EB88E))
                )
            )
        }
    }

}

@Composable
fun SetAccountInform(
    navController: NavHostController,
    route: String,
    informText: String,
    typeOfInform: String
) {
    Text(text = typeOfInform, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Button(
        modifier = Modifier
            .height(45.dp)
            .fillMaxWidth(),
        onClick = {
            navController.navigate(route)
        }, shape = RoundedCornerShape(15),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF067C06),
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = informText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            Icon(
                modifier = Modifier.size(25.dp),
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}


@Preview
@Composable
fun ShowSettings() {

    // ProfileSettingsScreen(null, 1.dp, 2.dp)


}
