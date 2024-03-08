package com.alibatuhanak.quizzapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.alibatuhanak.quizzapp.R
import com.alibatuhanak.quizzapp.model.Models
import com.alibatuhanak.quizzapp.navigation.Screen
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

@Composable
fun HomeScreen(navController: NavHostController, screenWidth: Dp, screenHeight: Dp) {
    val auth = Firebase.auth



    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }


    val categoryList = remember {
        mutableStateListOf<Models.CategoryModel>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp, alignment = Alignment.Top)
    ) {

        InformCard(screenWidth, screenHeight)
        CategoryCard(screenWidth, screenHeight, navController, context, categoryList,preferencesManager)
    }
}

@Composable
fun CategoryCard(
    width: Dp,
    height: Dp,
    navController: NavHostController?,
    context: Context,
    categoryList: SnapshotStateList<Models.CategoryModel>,
    preferencesManager: SharedPreference
) {

    LaunchedEffect(Unit) {

        val db = Firebase.firestore
        val categoriesRef = db.collection("categories")

        categoriesRef.get().addOnSuccessListener {
            for (d in it.documents) {
                val categoryName = d.get("category_name") as String
                val categoryIcon = d.get("category_icon") as String
                val questions = d.get("questions") as List<HashMap<String, String>>
                val categoryID = d.id

                val category = Models.CategoryModel(
                    categoryID = categoryID,
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    questions = questions
                )
                categoryList.add(category)
            }

        }.addOnFailureListener {
            Toast.makeText(
                context,
                "We have issue about categories. Try again!",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    Text(
        text = "Categories",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        color = Color.Black
    )

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Adaptive(minSize = width / 3),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {

        items(items = categoryList, key = { item: Models.CategoryModel ->
            item.categoryID
        }) { category ->
            CategoryModel(category, navController, preferencesManager)
        }
    }

}

@Composable
fun CategoryModel(categoryModel: Models.CategoryModel, navController: NavHostController?, preferencesManager: SharedPreference) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF75CF75)
        ),
        modifier = Modifier
            .size(width = 100.dp, height = 210.dp)
            .clickable {
                preferencesManager.saveData("categoryName", categoryModel.categoryName)
                navController?.navigate(Screen.QuizScreen.route+"/${categoryModel.categoryID}") {
                    popUpTo(Screen.HomeScreen.route)
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                5.dp,
                alignment = Alignment.CenterVertically
            )
        ) {

            SubcomposeAsyncImage(modifier = Modifier.fillMaxSize(.5f),
                model = categoryModel.categoryIcon,
                contentDescription = categoryModel.categoryName,
                loading = {
                    IndeterminateCircularIndicator()
                }
            )
            Text(text = categoryModel.categoryName, color = Color.Black, fontWeight = FontWeight.Black)
            Text(text = "${categoryModel.questions.size} questions", color = Color.Black, fontWeight = FontWeight.Normal)

        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun InformCard(width: Dp, height: Dp) {

    val context = LocalContext.current
    val preferencesManager = remember { SharedPreference(context) }

    val auth = Firebase.auth
    val currentUser = auth.currentUser

    val point =
        remember { mutableIntStateOf(preferencesManager.getData("point", 0)) }

    val rank =
        remember { mutableIntStateOf(preferencesManager.getData("rank", -1)) }

    LaunchedEffect(Unit){
        val db = Firebase.firestore
        val usersRef = db.collection("users")
        usersRef.orderBy("point", Query.Direction.DESCENDING).get().addOnSuccessListener {
            val docs = it.documents
            for ((index, doc) in docs.withIndex()) {
                val username = doc.get("username") as String

                if (currentUser?.displayName == username) {
                    val position = index + 1
                    preferencesManager.saveNumbers("rank", position)
                    rank.intValue = position
                    point.intValue = (doc.get("point") as Number).toInt()
                    preferencesManager.saveNumbers("point",  point.intValue)
                }
                usersRef.document(doc.id).update("rank", index + 1).addOnSuccessListener {}
            }

        }.addOnFailureListener {
            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF75CF75)
        ),
        modifier = Modifier
            .size(width = width, height = 80.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InformRow(R.drawable.star, point.intValue, "points")
                Divider(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .width(2.dp), color = Color(0xFFf5f5dc)
                )
                InformRow(R.drawable.medal, rank.intValue, "ranking")
            }

        }

    }

}

@Composable
fun InformRow(icon: Int, point: Number, postPoint: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "null",
            tint = Color.Unspecified,
            modifier = Modifier.fillMaxHeight(0.4f)
        )
        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 22.sp
                    )
                ) {
                    append("$point")
                }
                append("\n$postPoint")
            },
            fontFamily = FontFamily(
                Font(R.font.pacifico_regular)
            ),
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 5.dp, bottom = 0.dp)
        )
    }
}

@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
