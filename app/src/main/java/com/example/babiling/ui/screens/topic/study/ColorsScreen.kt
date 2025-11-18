package com.example.babiling.ui.screens.topic.study

import android.graphics.BitmapFactory
import java.io.IOException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambi2Family
import androidx.compose.material3.CenterAlignedTopAppBar

val colorList = listOf(
    FlashcardItem("BLACK", "color_flashcard/color_flashcard_black.jpg"),
    FlashcardItem("BLUE", "color_flashcard/color_flashcard_blue.jpeg"),
    FlashcardItem("BROWN", "color_flashcard/color_flashcard_brown.jpeg"),
    FlashcardItem("GOLD", "color_flashcard/color_flashcard_gold.jpeg"),
    FlashcardItem("GREEN", "color_flashcard/color_flashcard_green.jpeg"),
    FlashcardItem("GREY", "color_flashcard/color_flashcard_grey.jpeg"),
    FlashcardItem("LILAC", "color_flashcard/color_flashcard_lilac.jpeg"),
    FlashcardItem("NAVY", "color_flashcard/color_flashcard_navy.jpeg"),
    FlashcardItem("ORANGE", "color_flashcard/color_flashcard_orange.jpeg"),
    FlashcardItem("PINK", "color_flashcard/color_flashcard_pink.jpeg"),
    FlashcardItem("PURPLE", "color_flashcard/color_flashcard_purple.jpeg"),
    FlashcardItem("RED", "color_flashcard/color_flashcard_red.jpeg"),
    FlashcardItem("SILVER", "color_flashcard/color_flashcard_silver.jpeg"),
    FlashcardItem("WHITE", "color_flashcard/color_flashcard_white.jpeg"),
    FlashcardItem("YELLOW", "color_flashcard/color_flashcard_yellow.jpeg")
)

// --- HÀM VẼ CHỮ "MY FIRST COLORS" ---
@Composable
fun ColorfulTitleColors(
    text: String,
    fontSize: TextUnit = 36.sp
) {
    val colors = listOf(
        Color(0xFFD32F2F), // Đỏ (M)
        Color(0xFF1976D2), // Xanh dương (y)
        Color(0xFFFFC107), // Vàng (F)
        Color(0xFF43A047), // Xanh lá (i)
        Color(0xFF7E57C2), // Tím (r)
        Color(0xFFF06292), // Hồng (s)
        Color(0xFFEC407A), // Hồng đậm (t)
        Color(0xFF26C6DA), // Xanh lơ (C)
        Color(0xFFD32F2F), // Đỏ (o)
        Color(0xFF1976D2), // Xanh dương (l)
        Color(0xFFFFC107), // Vàng (o)
        Color(0xFF43A047), // Xanh lá (r)
        Color(0xFFF4511E)  // Cam (s)
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        // Chúng ta bỏ qua khoảng trắng khi đếm index màu để màu sắc liên tục hơn
        var colorIndex = 0

        text.forEach { char ->
            if (char == ' ') {
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                val textColor = colors[colorIndex % colors.size]
                colorIndex++

                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = char.toString(),
                        color = textColor,
                        fontSize = fontSize,
                        fontFamily = BalooThambi2Family,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ColorsPartCard(
    item: FlashcardItem,
    onClick: (FlashcardItem) -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(item.imagePath) {
        try {
            context.assets.open(item.imagePath).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { onClick(item) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi", fontSize = 10.sp, color = Color.Red)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorsScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    onNavigateForward: () -> Unit,
    onItemSelected: (FlashcardItem) -> Unit
) {
    val context = LocalContext.current

    val backIconBitmap = remember {
        try {
            context.assets.open("icons/ic_back_arrow.png").use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Colors",
                        fontFamily = BalooThambi2Family,
                        color = Color(0xFFEF993A),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        if (backIconBitmap != null) {
                            Image(
                                bitmap = backIconBitmap.asImageBitmap(),
                                contentDescription = "Quay lại",
                                colorFilter = ColorFilter.tint(Color(0xFFF57C00)),
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back_arrow),
                                contentDescription = "Quay lại",
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateForward) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Đi tới",
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                // Gọi hàm vẽ chữ nhiều màu
                ColorfulTitleColors(text = "My First Colors")
            }

            //DANH SÁCH ẢNH MÀU
            items(colorList) { item ->
                ColorsPartCard(
                    item = item,
                    onClick = { selectedItem ->
                        onItemSelected(selectedItem)
                    }
                )
            }

            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(40))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFCE68A),
                                    Color(0xFFFFD600)
                                )
                            )
                        )
                        .clickable { onFinish },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FINISH",
                        fontFamily = BalooThambi2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorsScreenPreview() {
    BabiLingTheme {
        ColorsScreen(
            onNavigateBack = {},
            onFinish = {},
            onNavigateForward = {},
            onItemSelected = {}
        )
    }
}