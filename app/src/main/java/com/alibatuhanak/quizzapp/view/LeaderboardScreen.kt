package com.alibatuhanak.quizzapp.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(
    navController: NavHostController,
    screenWidth: Dp,
    screenHeight: Dp,
) {

    val currentUser = Firebase.auth.currentUser

    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }
    val highRankList = remember {
        mutableStateListOf<Models.RankModel>()
    }
    val rank =
        remember { mutableIntStateOf(preferencesManager.getData("rank", 10)) }

    val rankList = remember {
        mutableStateListOf<Models.RankModel>()
    }


    val x = remember {
        mutableIntStateOf(0)
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (rank.intValue > 3) {
                listState.scrollToItem(index =rank.intValue-3)
            }
            getUsers(
                highRankList = highRankList,
                rankList = rankList,
                x = x,
                context = context,
                preferencesManager = preferencesManager
            )
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp, alignment = Alignment.Bottom)
    ) {
        LazyRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(
                18.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            itemsIndexed(items = highRankList) { index, item ->

                when (index) {
                    0 -> HighRankCard(
                        boxSize = 32.dp,
                        imageSize = 100.dp,
                        offset = 0,
                        width = screenWidth,
                        username = item.username, rank = 2,
                        imageUrl = item.avatarIcon,
                        point = item.point,
                        fontSize = 17.sp,
                        currentUser = currentUser,
                    )

                    1 -> HighRankCard(
                        boxSize = 38.dp,
                        imageSize = 125.dp,
                        offset = -35,
                        width = screenWidth,
                        username = item.username, rank = 1,
                        imageUrl = item.avatarIcon,
                        point = item.point,
                        fontSize = 20.sp,
                        currentUser = currentUser,
                    )

                    2 -> HighRankCard(
                        boxSize = 32.dp,
                        imageSize = 100.dp,
                        offset = 0,
                        width = screenWidth,
                        username = item.username, rank = 3,
                        imageUrl = item.avatarIcon,
                        point = item.point,
                        fontSize = 17.sp,
                        currentUser = currentUser,
                    )

                    else -> {

                    }
                }

            }
        }

        Card(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFB0E7B0)
            ),
            modifier = Modifier
                .size(width = screenWidth, height = screenHeight * 1 / 2)

        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp),
                contentPadding = PaddingValues(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = listState
            ) {

                itemsIndexed(rankList) { index, item ->
                    UserCard(
                        currentUser = currentUser,
                        width = screenWidth,
                        username = item.username,
                        rank = index + 4,
                        imageUrl = item.avatarIcon,
                        point = item.point
                    )


                }

            }
        }


    }
}

@Composable
fun UserCard(
    width: Dp,
    username: String,
    rank: Int,
    imageUrl: Number,
    point: Number,
    currentUser: FirebaseUser?,
) {
    var usernameText = username
    if (usernameText.length > 13) {
        usernameText = usernameText.substring(startIndex = 0, endIndex = 13).plus("...")
    }

    val borderMod = if (username == currentUser?.displayName) {
        Modifier.border(
            width = 3.dp,
            Color(0xFFB7410E),
            shape = RoundedCornerShape(10.dp)
        )
    } else {
        Modifier
    }

    val colorMode = if (username == currentUser?.displayName) {
        Color(0xFFB7410E)
    } else {
        Color.White
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF75CF75)
        ),
        modifier = Modifier
            .size(width = width * 9 / 10, height = 55.dp)
            .then(borderMod)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$rank",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorMode
                )
                Image(painter = painterResource(id = imageUrl.toInt()), contentDescription = null)
                Text(
                    text = usernameText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorMode
                )
            }
            Text(
                text = "$point points",
                modifier = Modifier.offset(x = (-8).dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = colorMode
            )
        }
    }
}

@Composable
fun HighRankCard(
    boxSize: Dp,
    imageSize: Dp,
    offset: Int,
    width: Dp,
    username: String,
    rank: Int,
    imageUrl: Number,
    point: Number,
    fontSize: TextUnit,
    currentUser: FirebaseUser?,
) {
    Column(
        modifier = Modifier.offset(y = (offset).dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var usernameText = username
        if (usernameText.length > 9) {
            usernameText = usernameText.substring(startIndex = 0, endIndex = 9).plus("...")
        }

        val colorMode = if (username == currentUser?.displayName) {
            Color(0xFFB7410E)
        } else {
            Color(0xFF067C06)
        }


        if (rank == 1)
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .offset(y = 8.dp)
                    .zIndex(1f),
                painter = painterResource(id = R.drawable.crown),
                contentDescription = null
            )
        Image(
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .border(
                    6.dp, colorMode,
                    CircleShape
                ),
            painter = painterResource(id = imageUrl.toInt()),
            contentDescription = null
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(boxSize)
                .offset(y = (-15).dp)
        ) {
            Canvas(modifier = Modifier.size(50.dp), onDraw = {
                drawCircle(
                    colorMode
                )
            })
            Text(
                modifier = Modifier,
                text = "$rank",
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Text(
            modifier = Modifier,
            text = usernameText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colorMode
        )
        Text(
            modifier = Modifier,
            text = "$point pts",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colorMode
        )
    }

}

private fun getUsers(
    highRankList: SnapshotStateList<Models.RankModel>,
    rankList: SnapshotStateList<Models.RankModel>,
    x: MutableIntState,
    context: Context,
    preferencesManager: SharedPreference
) {

    val db = Firebase.firestore
    val currentUser = Firebase.auth.currentUser
    val usersRef = db.collection("users")
    usersRef.orderBy("point", Query.Direction.DESCENDING).get().addOnSuccessListener {
        val docs = it.documents
        for ((index, doc) in docs.withIndex()) {

            val username = doc.get("username") as String
            val avatar = doc.get("avatarIcon") as Number
            val point = doc.get("point") as Number

            if (currentUser?.displayName == username) {
                val position = index + 1
                preferencesManager.saveNumbers("rank", position)
            }

            val rankModel = Models.RankModel(username, avatar, point)

            if (x.intValue <= 2) {
                highRankList.add(rankModel)
                x.intValue += 1
            } else {
                rankList.add(rankModel)
            }

            usersRef.document(doc.id).update("rank", index + 1).addOnSuccessListener {}

        }

        /*  highRankList[3] = highRankList[0]
          highRankList[0] = highRankList[1]
          highRankList[1] = highRankList[3]*/
        val high = highRankList[0]
        highRankList[0] = highRankList[1]
        highRankList[1] = high

    }.addOnFailureListener {
        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}