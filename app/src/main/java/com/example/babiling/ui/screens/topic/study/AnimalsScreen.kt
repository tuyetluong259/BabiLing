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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambi2Family
import androidx.compose.material3.CenterAlignedTopAppBar


val animalsListData = listOf(
    FlashcardItem("ALLIGATOR", "Cá sấu", "animals_flashcard_ABC/A.jpeg"),
    FlashcardItem("BEAR", "Gấu", "animals_flashcard_ABC/B.jpeg"),
    FlashcardItem("CAT", "Mèo", "animals_flashcard_ABC/C.jpeg"),
    FlashcardItem("DOG", "Chó", "animals_flashcard_ABC/D.jpeg"),
    FlashcardItem("ELEPHANT", "Voi", "animals_flashcard_ABC/E.jpeg"),
    FlashcardItem("FROG", "Ếch", "animals_flashcard_ABC/F.jpeg"),
    FlashcardItem("GOAT", "Dê", "animals_flashcard_ABC/G.jpeg"),
    FlashcardItem("HORSE", "Ngựa", "animals_flashcard_ABC/H.jpeg"),
    FlashcardItem("IGUANA", "Kỳ nhông", "animals_flashcard_ABC/I.jpeg"),
    FlashcardItem("JELLYFISH", "Sứa", "animals_flashcard_ABC/J.jpeg"),
    FlashcardItem("KANGAROO", "Chuột túi", "animals_flashcard_ABC/K.jpeg"),
    FlashcardItem("LION", "Sư tử", "animals_flashcard_ABC/L.jpeg"),
    FlashcardItem("MONKEY", "Khỉ", "animals_flashcard_ABC/M.jpeg"),
    FlashcardItem("NARWHAL", "Cá voi một sừng", "animals_flashcard_ABC/N.jpeg"),
    FlashcardItem("OCTOPUS", "Bạch tuộc", "animals_flashcard_ABC/O.jpeg"),
    FlashcardItem("PIG", "Heo", "animals_flashcard_ABC/P.jpeg"),
    FlashcardItem("QUAIL", "Chim cút", "animals_flashcard_ABC/Q.jpeg"),
    FlashcardItem("RABBIT", "Thỏ", "animals_flashcard_ABC/R.jpeg"),
    FlashcardItem("SNAKE", "Rắn", "animals_flashcard_ABC/S.jpeg"),
    FlashcardItem("TIGER", "Hổ", "animals_flashcard_ABC/T.jpeg"),
    FlashcardItem("URCHIN", "Nhím biển", "animals_flashcard_ABC/U.jpeg"),
    FlashcardItem("VULTURE", "Kền kền", "animals_flashcard_ABC/V.jpeg"),
    FlashcardItem("WHALE", "Cá voi", "animals_flashcard_ABC/W.jpeg"),
    FlashcardItem("FOX", "Cáo", "animals_flashcard_ABC/F.jpeg"),
    FlashcardItem("YAK", "Bò Tây Tạng", "animals_flashcard_ABC/Y.jpeg"),
    FlashcardItem("ZEBRA", "Ngựa vằn", "animals_flashcard_ABC/Z.jpeg")
)

@Composable
fun ColorfulTitleAnimals(
    text: String,
    fontSize: TextUnit = 40.sp
) {
    val colors = listOf(
        Color(0xFF8BC34A), // Xanh lá cây nhạt
        Color(0xFFFFC107), // Vàng
        Color(0xFF2196F3), // Xanh da trời
        Color(0xFFE91E63), // Hồng
        Color(0xFFFF5722), // Cam
        Color(0xFF9C27B0)  // Tím
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
fun AnimalsFlashcard(
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
            .aspectRatio(1.5f)
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
                        .weight(0.7f)
                    ,
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = BalooThambi2Family,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 1.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalsScreen(
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
                        text = "Animals",
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
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ITEM 1: TIÊU ĐỀ "ANIMALS"
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                ColorfulTitleAnimals(text = "ANIMALS")
            }

            items(animalsListData) { item ->
                AnimalsFlashcard(
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
fun AnimalsScreenPreview() {
    BabiLingTheme {
        AnimalsScreen(
            onNavigateBack = {},
            onFinish = {},
            onNavigateForward = {},
            onItemSelected = {}
        )
    }
}