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
import com.example.babiling.utils.SoundPlayer


val toysListData = listOf(
    FlashcardItem("BABY DOLL", "Búp bê em bé", "toys_flashcard/baby_doll.png", "sound/toys/baby_doll.mp3"),
    FlashcardItem("BALL", "Bóng", "toys_flashcard/ball.png", "sound/toys/ball.mp3"),
    FlashcardItem("BICYCLE", "Xe đạp", "toys_flashcard/bicycle.png", "sound/toys/bicycle.mp3"),
    FlashcardItem("BLOCK GAME", "Trò chơi khối", "toys_flashcard/block_game.png", "sound/toys/block_game.mp3"),
    FlashcardItem("BLOCKS", "Khối đồ chơi", "toys_flashcard/blocks.png", "sound/toys/blocks.mp3"),
    FlashcardItem("BOARD GAME", "Trò chơi cờ bàn", "toys_flashcard/board_game.png", "sound/toys/board_game.mp3"),
    FlashcardItem("BOUNCING TOY", "Đồ chơi nảy", "toys_flashcard/bouncing_toy.png", "sound/toys/bouncing_toy.mp3"),
    FlashcardItem("BUILDING BLOCKS", "Khối xếp hình", "toys_flashcard/building_blocks.png", "sound/toys/building_blocks.mp3"),
    FlashcardItem("CARD GAME", "Trò chơi bài", "toys_flashcard/card_game.png", "sound/toys/card_game.mp3"),
    FlashcardItem("DOLL", "Búp bê", "toys_flashcard/doll.png", "sound/toys/doll.mp3"),
    FlashcardItem("FIGURINE", "Mô hình nhỏ", "toys_flashcard/figurine.png", "sound/toys/figurine.mp3"),
    FlashcardItem("JUMP ROPE", "Dây nhảy", "toys_flashcard/jump_rope.png", "sound/toys/jump_rope.mp3"),
    FlashcardItem("PUZZLE", "Trò xếp hình", "toys_flashcard/puzzle.png", "sound/toys/puzzle.mp3"),
    FlashcardItem("RC CAR", "Xe đồ chơi", "toys_flashcard/RC_car.png", "sound/toys/RC_car.mp3"),
    FlashcardItem("ROBOT", "Robot", "toys_flashcard/robot.png", "sound/toys/robot.mp3"),
    FlashcardItem("SCOOTER", "Xe scooter", "toys_flashcard/scooter.png", "sound/toys/scooter.mp3"),
    FlashcardItem("TABLET", "Máy tính bảng", "toys_flashcard/tablet.png", "sound/toys/tablet.mp3"),
    FlashcardItem("TEDDY BEAR", "Gấu bông Teddy", "toys_flashcard/teddy_bear.png", "sound/toys/teddy_bear.mp3"),
    FlashcardItem("TOY CAR", "Xe ô tô đồ chơi", "toys_flashcard/toy_car.png", "sound/toys/toy_car.mp3"),
    FlashcardItem("TOY TRUCK", "Xe tải đồ chơi", "toys_flashcard/toy_truck.png", "sound/toys/toy_truck.mp3"),
    FlashcardItem("VIDEOGAME", "Trò chơi điện tử", "toys_flashcard/videogame.png", "sound/toys/videogame.mp3")
)

@Composable
fun ColorfulTitleToys(
    text: String,
    fontSize: TextUnit = 40.sp
) {
    val colors = listOf(
        Color(0xFFFDD835), // Vàng
        Color(0xFF03A9F4), // Xanh dương
        Color(0xFFFF5722), // Cam
        Color(0xFF4CAF50), // Xanh lá
        Color(0xFF9C27B0), // Tím
        Color(0xFFE91E63)  // Hồng
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
    ) {
        var colorIndex = 0
        text.forEach { char ->
            if (char == ' ') {
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                val textColor = colors[colorIndex % colors.size]
                colorIndex++

                Box(contentAlignment = Alignment.Center) {
                    // Lớp 1: Viền đen
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
                    // Lớp 2: Màu chính
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
fun ToysFlashcard(
    item: FlashcardItem,
    onClick: (FlashcardItem) -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(item.imagePath) {
        try {
            context.assets.open(item.imagePath.replace(" ", "_")).use {
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

            Spacer(modifier = Modifier.height(4.dp))
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
fun ToysScreen(
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
                        text = "Toys",
                        fontFamily = BalooThambi2Family,
                        color = Color(0xFFE53935),
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
                ColorfulTitleToys(text = "TOYS")
            }

            items(toysListData) { item ->
                ToysFlashcard(
                    item = item,
                    onClick = { selectedItem ->
                        SoundPlayer.play(context, selectedItem.soundPath)
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
fun ToysScreenPreview() {
    BabiLingTheme {
        ToysScreen(
            onNavigateBack = {},
            onFinish = {},
            onNavigateForward = {},
            onItemSelected = {}
        )
    }
}