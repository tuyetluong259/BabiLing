package com.example.babiling.ui.screens.Choose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambiFamily
import com.example.babiling.ui.theme.BabiLingTheme
import androidx.compose.ui.unit.Dp

@Composable
fun ChooseAgeScreen(navController: NavController) {

    val backgroundColor = Color(0xFFA7E8BD)
    val titleColor = Color(0xFFE25939)
    val cardBorderColor = Color(0xFFF48FB1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Quay lại",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Hãy chọn độ tuổi của bé để bắt\nđầu học nhé!\uD83D\uDC4F",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = titleColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            AgeSelectionCard(
                imageRes = R.drawable.bbi1,
                text = "Dưới 4 tuổi",
                borderColor = cardBorderColor,
                onClick = {
                    // TODO: Điều hướng đến màn hình Home
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            AgeSelectionCard(
                imageRes = R.drawable.bbi2,
                text = "5-10 tuổi",
                borderColor = cardBorderColor,
                onClick = {
                    // TODO: Điều hướng đến màn hình Home
                }
            )
        }

        // Mặt trời 1
        SunIcon(
            modifier = Modifier
                .align(Alignment.TopEnd) // Vị trí
                .padding(top = 160.dp, end = 20.dp),
            size = 90.dp //
        )

        // Mặt trời 2
        SunIcon(
            modifier = Modifier
                .align(Alignment.CenterStart) // Vị trí
                .padding(start = 10.dp, top = 10.dp),
            size = 120.dp
        )
        // Mặt trời 4
        SunIcon(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 80.dp, start = 220.dp),
            size = 150.dp
        )
    }
}

@Composable
fun AgeSelectionCard(
    imageRes: Int,
    text: String,
    borderColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontFamily = BalooThambiFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.size(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = text,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SunIcon(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp //
) {
    Image(
        painter = painterResource(id = R.drawable.sun),
        contentDescription = "Trang trí mặt trời",
        modifier = modifier.size(size)
    )
}


@Preview(showBackground = true)
@Composable
fun ChooseAgeScreenPreview() {
    BabiLingTheme {
        val fakeNavController = rememberNavController()
        ChooseAgeScreen(navController = fakeNavController)
    }
}