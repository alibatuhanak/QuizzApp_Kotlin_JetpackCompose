package com.alibatuhanak.quizzapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun EndLevelScreen(
    correctCount: String?,
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp,
) {
    val currentUser = Firebase.auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(65.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            modifier = Modifier.size(200.dp),
            painter = painterResource(id = if (correctCount?.toInt()!! >= 5) R.drawable.congrats else R.drawable.sad),
            contentDescription = null
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                10.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Congrats ${currentUser?.displayName}",
                color = Color(0xFF066806),
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "You scored: $correctCount",
                color = Color(0xFF066806),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    15.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.star),
                    tint = Color.Unspecified,
                    contentDescription = null
                )
                Text(
                    text = "${correctCount.toInt()*10} points",
                    fontSize = 35.sp,
                    color = Color(0xFF066806),
                    fontWeight = FontWeight.ExtraBold
                )

            }


        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
           // EndLevelButton(navController,"Check leaderboard", Color(0xFF066806), Color.White)
            EndLevelButton(navController,"New quiz", Color.White, Color(0xFF066806))
        }
    }


}

@Preview
@Composable
fun Preview22() {
    }

@Composable
fun EndLevelButton(navController: NavHostController, goTo: String, color: Color, textColor: Color) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color(0xFF066806), shape = RoundedCornerShape(10)),
        shape = RoundedCornerShape(10),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
        ),
        onClick = {
            if (goTo == "New quiz")

                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(Screen.HomeScreen.route)
                }
            else {

             navController.navigate(Screen.LeaderboardScreen.route) {
                }
            }
        }) {
        Text(
            text = goTo,
            fontSize = 20.sp,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}