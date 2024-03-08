package com.alibatuhanak.quizzapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    category: String?,
    navController: NavHostController?, screenWidth: Dp?, screenHeight: Dp?
) {
    //val currentProgress by remember { mutableFloatStateOf(1f / 10) }

    val context = LocalContext.current
    val db = Firebase.firestore

    val usersRef = Firebase.firestore.collection("users")
    val currentUser = Firebase.auth.currentUser

    val preferencesManager = remember { SharedPreference(context) }

    val documentID =
        remember { mutableStateOf(preferencesManager.getData("documentID", "0")) }

    val questionList = remember {
        mutableStateListOf<HashMap<String, String>>()
    }

    val correctAnswerCount = remember {
        mutableIntStateOf(0)
    }

    val questionNumber = remember {
        mutableIntStateOf(0)
    }
    val currentProgress by animateFloatAsState(
        targetValue = (questionNumber.intValue + 1) / (questionList.size + 1).toFloat(),
        label = "questions"
    )
    val selectedOption = remember {
        mutableStateMapOf<String, Boolean>(
            "optionA" to false,
            "optionB" to false,
            "optionC" to false,
            "optionD" to false
        )
    }



    LaunchedEffect(Unit) {

        if (category != null) {

            db.collection("categories").document(category).get().addOnSuccessListener { d ->

                val questions = d.get("questions") as? List<HashMap<String, String>>

                if (questions != null) {
                    questionList.addAll(questions)
                }

            }.addOnFailureListener {
                println(it.localizedMessage)
            }
        }


    }
    if (questionList.isNotEmpty())
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
                .background(Color(0xFFf5f5dc)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp, alignment = Alignment.Top)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "${questionList.size} QUESTIONS",
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    color = Color(0xFF045A04),
                )
                Box(
                    modifier = Modifier
                        .size(80.dp, 40.dp)
                        .clip(RoundedCornerShape(20))
                        .background(Color(0x8B5A5F5A)),
                    contentAlignment = Alignment.Center
                ) {
                    val mode = "Hard"
                    Text(
                        text = mode,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color =
                        when (mode) {
                            "Easy" -> Color.Green
                            "Medium" -> Color.Magenta
                            "Hard" -> Color.Red
                            else -> {
                                Color.Green
                            }
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .size(80.dp, 40.dp)
                        .clip(RoundedCornerShape(20))
                        .background(Color(0x8B5A5F5A)),
                    contentAlignment = Alignment.Center
                ) {
                    CountdownTimerScreen(usersRef, documentID, correctAnswerCount, navController)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Question ${questionNumber.intValue + 1} of ${questionList.size}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF045A04),
                    fontSize = 20.sp
                )
                LinearDeterminateIndicator(currentProgress)
                /* Button(onClick = {
                     currentProgress += 1f / 10
                     println(currentProgress)
                 }) */
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(Color.White)
                    .border(1.dp, Color(0xFF047704), RoundedCornerShape(20.dp))
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp, alignment = Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(
                    text = "${questionList[questionNumber.intValue].get("question")}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF047704),
                    fontSize = 21.sp
                )

                val question = questionList[questionNumber.intValue]

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(15.dp, alignment = Alignment.Bottom),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OptionButton(
                        "optionA",
                        question["optionA"]!!,
                        question["correctOption"]!!,
                        selectedOption,
                        correctAnswerCount
                    )

                    OptionButton(
                        "optionB",
                        question["optionB"]!!,
                        question["correctOption"]!!,
                        selectedOption,
                        correctAnswerCount
                    )
                    OptionButton(
                        "optionC",
                        question["optionC"]!!,
                        question["correctOption"]!!,
                        selectedOption,
                        correctAnswerCount
                    )
                    OptionButton(
                        "optionD",
                        question["optionD"]!!,
                        question["correctOption"]!!,
                        selectedOption,
                        correctAnswerCount
                    )
                }
                //question
                //a /ption
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .border(1.dp, Color(0xFF066806), shape = RoundedCornerShape(10)),

                    shape = RoundedCornerShape(10),
                    enabled = (selectedOption["optionA"]!! || selectedOption["optionB"]!! || selectedOption["optionC"]!! || selectedOption["optionD"]!!),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF066806),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x28066806),
                        disabledContentColor = Color(0x9CFFFFFF),
                    ),
                    onClick = {
                        if (questionList.size - 1 != questionNumber.intValue) {
                            questionNumber.intValue++
                            selectedOption["optionA"] = false
                            selectedOption["optionB"] = false
                            selectedOption["optionC"] = false
                            selectedOption["optionD"] = false

                        } else
                            usersRef.document(documentID.value).update(
                                "point",
                                FieldValue.increment(correctAnswerCount.intValue * 10.toLong())
                            ).addOnSuccessListener {
                                navController?.navigate(Screen.EndLevelScreen.route + "/${correctAnswerCount.intValue}") {
                                    popUpTo(Screen.HomeScreen.route)
                                }
                            }.addOnFailureListener {
                                println(it.localizedMessage)
                            }


                    }) {
                    Text(
                        text = if (questionList.size - 1 != questionNumber.intValue) "Next" else "Finish",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                }

            }

        }
}

@Composable
fun LinearDeterminateIndicator(currentProgress: Float) {
    LinearProgressIndicator(
        progress = currentProgress,
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .clip(CircleShape),
        color = Color(0xFF75CF75),
        trackColor = Color.White
    )
}


@Composable
fun CountdownTimer(
    initialSeconds: Int,
    onFinish: () -> Unit,
    dispatcher: CoroutineDispatcher
) {
    var remainingSeconds by remember { mutableIntStateOf(initialSeconds) }
    val job = remember { Job() } // Remember the job to manage its lifecycle

    DisposableEffect(Dispatchers.IO) {
        val coroutineScope = CoroutineScope(dispatcher + job)

        coroutineScope.launch {
            repeat(initialSeconds) {
                println("job started")
                delay(1000)
                remainingSeconds--
            }
            onFinish()
        }

        onDispose {
            println("job canceled")
            job.cancel()
        }
    }
    if (dispatcher == Dispatchers.IO)
        Text(
            text = " ${(remainingSeconds / 60)}:${if (remainingSeconds % 60 <= 9) 0 else ""}${remainingSeconds % 60}",
            color = Color(0xFFFFFFFF),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
}

@Composable
fun CountdownTimerScreen(
    usersRef: CollectionReference?,
    documentID: MutableState<String>?,
    correctAnswerCount: MutableIntState?,
    navController: NavHostController?
) {
    var timerRunning by remember { mutableStateOf(true) }
    if (timerRunning) {
        CountdownTimer(
            initialSeconds = 90,
            onFinish = {
                timerRunning = false



                usersRef?.document(documentID!!.value)?.update(
                    "point",
                    FieldValue.increment(correctAnswerCount?.intValue!! * 10.toLong())
                )?.addOnSuccessListener {
                    navController?.navigate(Screen.EndLevelScreen.route + "/${correctAnswerCount.intValue}") {
                        popUpTo(Screen.HomeScreen.route)
                    }
                }?.addOnFailureListener {
                    println(it.localizedMessage)
                }
            },
            dispatcher = Dispatchers.IO
        )
    } else {
        Text(text = "end of the game")
    }
}


@Composable
fun OptionButton(
    optionString: String,
    option: String,
    correctOption: String,
    selectedOption: SnapshotStateMap<String, Boolean>,
    correctAnswerCount: MutableIntState
) {


    val answerTextMode = when {
        correctOption == optionString && selectedOption[optionString]!! -> Color.White
        correctOption != optionString && selectedOption[optionString]!! -> Color.White
        else -> Color(0xFF047704)
    }
    val answerMode = when {
        correctOption == optionString && selectedOption[optionString]!! -> Color(0xFF047704)
        correctOption != optionString && selectedOption[optionString]!! -> Color(0xFF770408)
        else -> Color.White
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(0.3.dp, Color(0xFF066806), shape = RoundedCornerShape(10)),
        shape = RoundedCornerShape(10),
        enabled = !selectedOption[correctOption]!!,
        colors = ButtonDefaults.buttonColors(
            containerColor = answerMode,
            disabledContainerColor = answerMode
        ),
        onClick = {
            if (optionString == correctOption) {
                correctAnswerCount.intValue++
            }
            selectedOption[optionString] = true
            selectedOption[correctOption] = true
        }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Start)
        ) {


            if (selectedOption[optionString]!! && optionString == correctOption)
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.checked),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            if (selectedOption[optionString]!! && optionString != correctOption)
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.cross),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

            Text(
                text = option,
                fontWeight = FontWeight.ExtraBold,
                color = answerTextMode,
                fontSize = 20.sp,
            )
        }
    }

}

@Preview
@Composable
fun Show() {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color(0xFF066806), shape = RoundedCornerShape(10)),
        shape = RoundedCornerShape(10),
        enabled = false,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            disabledContainerColor = Color.Red
        ),
        onClick = {

        }) {
        Text(text = "Next", fontWeight = FontWeight.Bold, fontSize = 19.sp)
    }

}