package com.alibatuhanak.quizzapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.alibatuhanak.quizzapp.service.SharedPreference
import com.alibatuhanak.quizzapp.service.settingsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController?, screenWidth: Dp, screenHeight: Dp) {
    val context = LocalContext.current
    val darkMode = remember {
        mutableStateOf(false)
    }
    val dataUsage = remember {
        mutableStateOf(
            false
        )
    }
    val preferencesManager = remember { SharedPreference(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf5f5dc))
    ) {
        settingsList.forEachIndexed { index, setting ->
            Card(modifier = Modifier
                .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFf5f5dc)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = { setting.option(navController!!, preferencesManager) }) {
                if (index == 1 || index == 2 || index == 3 || index == 4) {

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, top = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        modifier = Modifier.size(45.dp),
                        tint = if (index == 1 || index == 2 || index == 3 || index == 4) Color.Gray else Color(
                            0xFF46A046
                        ),
                        painter = painterResource(id = setting.settingIcon),
                        contentDescription = null
                    )

                    Column(modifier = Modifier.padding(start = 15.dp)) {
                        Text(
                            text = setting.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black


                        )
                        Text(
                            text = setting.text,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                    if (index == 2) {
                        Checkbox(
                            modifier = Modifier.padding(start = 35.dp),
                            checked = darkMode.value,
                            onCheckedChange = {
                                darkMode.value = it
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF1B641B),
                                checkmarkColor = Color.White,
                                disabledCheckedColor = Color.White,
                                uncheckedColor = Color(0xFF1B641B),
                            )
                        )
                    }
                    if (index == 4) {
                        Switch(
                            modifier = Modifier.padding(start = 55.dp),
                            checked = dataUsage.value,
                            onCheckedChange = {
                                dataUsage.value = it
                            }, colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFf5f5dc),
                                checkedTrackColor = Color(0xFF1B641B),
                                uncheckedThumbColor = Color.DarkGray,
                                uncheckedTrackColor = Color.White,
                            )
                        )
                    }
                }

            }
            Divider(color = Color(0xFF1B641B))


        }

    }

}

@Preview
@Composable
fun ShowScreen() {
    SettingsScreen(null, 0.dp, 0.dp)
}