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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambi2Family
import androidx.compose.material3.CenterAlignedTopAppBar


val fruitListData = listOf(
    FlashcardItem("APPLE", "Quả táo", "fruit_flashcard/fruit-flashcard-apple.jpeg"),
    FlashcardItem("AVOCADO", "Quả bơ", "fruit_flashcard/fruit-flashcard-avocado.jpeg"),
    FlashcardItem("BANANA", "Quả chuối", "fruit_flashcard/fruit-flashcard-banana.jpeg"),
    FlashcardItem("CHERRY", "Quả cherry", "fruit_flashcard/fruit-flashcard-cherry.jpeg"),
    FlashcardItem("COCONUT", "Quả dừa", "fruit_flashcard/fruit-flashcard-coconut.jpeg"),
    FlashcardItem("GRAPEFRUIT", "Quả bưởi", "fruit_flashcard/fruit-flashcard-grapefruit.jpeg"),
    FlashcardItem("GRAPES", "Quả nho", "fruit_flashcard/fruit-flashcard-grapes.jpeg"),
    FlashcardItem("KIWI", "Quả kiwi", "fruit_flashcard/fruit-flashcard-kiwi.jpeg"),
    FlashcardItem("LEMON", "Quả chanh vàng", "fruit_flashcard/fruit-flashcard-lemon.jpeg"),
    FlashcardItem("LIME", "Quả chanh xanh", "fruit_flashcard/fruit-flashcard-lime.jpeg"),
    FlashcardItem("MANGO", "Quả xoài", "fruit_flashcard/fruit-flashcard-mango.jpeg"),
    FlashcardItem("MELON", "Quả dưa lưới", "fruit_flashcard/fruit-flashcard-melon.jpeg"),
    FlashcardItem("ORANGE", "Quả cam", "fruit_flashcard/fruit-flashcard-orange.jpeg"),
    FlashcardItem("PAPAYA", "Quả đu đủ", "fruit_flashcard/fruit-flashcard-papaya.jpeg"),
    FlashcardItem("PEACH", "Quả đào", "fruit_flashcard/fruit-flashcard-peach.jpeg"),
    FlashcardItem("PEAR", "Quả lê", "fruit_flashcard/fruit-flashcard-pear.jpeg"),
    FlashcardItem("PINEAPPLE", "Quả dứa", "fruit_flashcard/fruit-flashcard-pineapple.jpeg"),
    FlashcardItem("PLUM", "Quả mận", "fruit_flashcard/fruit-flashcard-plum.jpeg"),
    FlashcardItem("RASPBERRY", "Quả mâm xôi", "fruit_flashcard/fruit-flashcard-raspberry.jpeg"),
    FlashcardItem("STRAWBERRY", "Quả dâu tây", "fruit_flashcard/fruit-flashcard-strawberry.jpeg"),
    FlashcardItem("WATERMELON", "Quả dưa hấu", "fruit_flashcard/fruit-flashcard-watermelon.jpeg")
)

@Composable
fun ColorfulTitleFruit(
    text: String,
    fontSize: TextUnit = 40.sp
) {
    val colors = listOf(
        Color(0xFFE53935),
        Color(0xFF43A047),
        Color(0xFFFFB300),
        Color(0xFF8E24AA),
        Color(0xFFFB8C00),
        Color(0xFF1E88E5)
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        text.forEachIndexed { index, char ->
            val textColor = colors[index % colors.size]

            if (char == ' ') {
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Box(contentAlignment = Alignment.Center) {
                    // Lớp viền đen
                    Text(
                        text = char.toString(),
                        color = Color.Black,
                        fontSize = fontSize,
                        fontFamily = BalooThambi2Family,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle.Default.copy(
                            drawStyle = Stroke(
                                miter = 10f,
                                width = 8f,
                                join = StrokeJoin.Round
                            )
                        )
                    )

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
fun FruitPartCard(
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
            verticalArrangement = Arrangement.Top
        ) {
            // Ảnh
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

            // Tiếng Việt
            Text(
                text = item.nameVi,
                fontSize = 14.sp,
                fontFamily = BalooThambi2Family,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FruitScreen(
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
                        text = "Fruits",
                        fontFamily = BalooThambi2Family,
                        color = Color(0xFF09BC18),
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
                ColorfulTitleFruit(text = "Nature's candy!")
            }

            items(fruitListData) { item ->
                FruitPartCard(
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
fun FruitScreenPreview() {
    BabiLingTheme {
        FruitScreen(
            onNavigateBack = {},
            onFinish = {},
            onNavigateForward = {},
            onItemSelected = {}
        )
    }
}