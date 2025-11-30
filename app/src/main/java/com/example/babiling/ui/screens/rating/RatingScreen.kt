package com.example.babiling.ui.screens.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family

@Composable
fun RatingScreen(
    points: Int = 10,
    navController: NavController
) {
    val BackgroundPink = Color(0xFFFFB3C6)
    val YellowHighlight = Color(0xFFFFC107)
    val DarkOrange = Color(0xFFFFB366)
    val CreamWhite = Color(0xFFFFFBE6)
    val ButtonYellow = Color(0xFFFFD54F)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.star_big),
                    contentDescription = "Star",
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.width(0.dp))

                Image(
                    painter = painterResource(id = R.drawable.star_big),
                    contentDescription = "Star",
                    modifier = Modifier
                        .size(80.dp)
                        .offset(y = (-8).dp)
                )

                Spacer(modifier = Modifier.width(0.dp))

                Image(
                    painter = painterResource(id = R.drawable.star_big),
                    contentDescription = "Star",
                    modifier = Modifier.size(56.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = CreamWhite,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chúc mừng!!",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = BalooThambi2Family,
                        color = YellowHighlight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bé đã nhận được",
                        fontSize = 18.sp,
                        color = DarkOrange,
                        fontFamily = BalooThambi2Family,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.candy),
                            contentDescription = "Candy",
                            modifier = Modifier.size(50.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = points.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BalooThambi2Family,
                            color = DarkOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            navController.navigate("topic_screen1") {
                                popUpTo("rating_screen") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonYellow
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "YAY, OK!",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = BalooThambi2Family,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.decor5),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 50.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatingScreenPreview() {
    RatingScreen(
        points = 10,
        navController = rememberNavController()
    )
}