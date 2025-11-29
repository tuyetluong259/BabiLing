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


val bodyList = listOf(
    FlashcardItem("ARMS", "Cánh tay", "images/body/body_flashcard_arms.png", "sounds/body/arms.mp3"),
    FlashcardItem("BODY", "Cơ thể", "images/body/body_flashcard_body.png", "sounds/body/body.mp3"),
    FlashcardItem("NOSE", "Mũi", "images/body/body_flashcard_nose.png", "sounds/body/nose.mp3"),
    FlashcardItem("EARS", "Tai", "images/body/body_flashcard_ears.png", "sounds/body/ears.mp3"),
    FlashcardItem("ELBOWS", "Khuỷu tay", "images/body/body_flashcard_elbows.png", "sounds/body/elbows.mp3"),
    FlashcardItem("EYELASHES", "Lông mi", "images/body/body_flashcard_eyelashes.png", "sounds/body/eyelashes.mp3"),
    FlashcardItem("EYES", "Mắt", "images/body/body_flashcard_eyes.png", "sounds/body/eyes.mp3"),
    FlashcardItem("FACE", "Khuôn mặt", "images/body/body_flashcard_face.png", "sounds/body/face.mp3"),
    FlashcardItem("FEET", "Bàn chân", "images/body/body_flashcard_feet.png", "sounds/body/feet.mp3"),
    FlashcardItem("FINGERS", "Ngón tay", "images/body/body_flashcard_fingers.png", "sounds/body/fingers.mp3"),
    FlashcardItem("HAIR", "Tóc", "images/body/body_flashcard_hair.png", "sounds/body/hair.mp3"),
    FlashcardItem("HANDS", "Bàn tay", "images/body/body_flashcard_hands.png", "sounds/body/hands.mp3"),
    FlashcardItem("HEAD", "Đầu", "images/body/body_flashcard_head.png", "sounds/body/head.mp3"),
    FlashcardItem("KNEES", "Đầu gối", "images/body/body_flashcard_knees.png", "sounds/body/knees.mp3"),
    FlashcardItem("LEGS", "Chân", "images/body/body_flashcard_legs.png", "sounds/body/legs.mp3"),
    FlashcardItem("LIPS", "Môi", "images/body/body_flashcard_lips.png", "sounds/body/lips.mp3"),
    FlashcardItem("MOUTH", "Miệng", "images/body/body_flashcard_mouth.png", "sounds/body/mouth.mp3"),
    FlashcardItem("NECK", "Cổ", "images/body/body_flashcard_neck.png", "sounds/body/neck.mp3")
)

@Composable
fun ColorfulTitle(
    text: String,
    fontSize: TextUnit = 40.sp
) {
    val colors = listOf(
        Color(0xFFEF5350), // Cam đỏ
        Color(0xFFFFCA28), // Vàng
        Color(0xFF26A69A), // Xanh lá đậm
        Color(0xFF42A5F5), // Xanh dương
        Color(0xFFFFA726), // Cam
        Color(0xFF66BB6A), // Xanh lá
        Color(0xFFAB47BC), // Tím (nếu cần)
        Color(0xFF29B6F6)  // Xanh dương nhạt
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
                    // Lớp màu chính
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
fun BodyPartCard(
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
                        .weight(0.7f)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi", fontSize = 10.sp, color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tiếng Việt
            Text(
                text = item.nameVi,
                fontSize = 14.sp,
                fontFamily = BalooThambi2Family,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyScreen(
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
                        text = "Body",
                        fontFamily = BalooThambi2Family,
                        color = Color(0xFF485FDA),
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
                ColorfulTitle(text = "BODY PARTS")
            }

            items(bodyList) { item ->
                BodyPartCard(
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
fun BodyScreenPreview() {
    BabiLingTheme {
        BodyScreen(
            onNavigateBack = {},
            onFinish = {},
            onNavigateForward = {},
            onItemSelected = {}
        )
    }
}