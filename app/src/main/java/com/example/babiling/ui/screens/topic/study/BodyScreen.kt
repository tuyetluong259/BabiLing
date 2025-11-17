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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambi2Family
import androidx.compose.material3.CenterAlignedTopAppBar


val bodyList = listOf(
    FlashcardItem("ARMS", "body_flashcard/body_flashcard_arms.png"),
    FlashcardItem("BODY", "body_flashcard/body_flashcard_body.png"),
    FlashcardItem("NOSE", "body_flashcard/body_flashcard_nose.png"),
    FlashcardItem("EARS", "body_flashcard/body_flashcard_ears.png"),
    FlashcardItem("ELBOWS", "body_flashcard/body_flashcard_elbows.png"),
    FlashcardItem("EYELASHES", "body_flashcard/body_flashcard_eyelashes.png"),
    FlashcardItem("EYES", "body_flashcard/body_flashcard_eyes.png"),
    FlashcardItem("FACE", "body_flashcard/body_flashcard_face.png"),
    FlashcardItem("FEET", "body_flashcard/body_flashcard_feet.png"),
    FlashcardItem("FINGERS", "body_flashcard/body_flashcard_fingers.png"),
    FlashcardItem("HAIR", "body_flashcard/body_flashcard_hair.png"),
    FlashcardItem("HANDS", "body_flashcard/body_flashcard_hands.png"),
    FlashcardItem("HEAD", "body_flashcard/body_flashcard_head.png"),
    FlashcardItem("KNEES", "body_flashcard/body_flashcard_knees.png"),
    FlashcardItem("LEGS", "body_flashcard/body_flashcard_legs.png"),
    FlashcardItem("LIPS", "body_flashcard/body_flashcard_lips.png"),
    FlashcardItem("MOUTH", "body_flashcard/body_flashcard_mouth.png"),
    FlashcardItem("NECK", "body_flashcard/body_flashcard_neck.png")
)

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

            items(bodyList) { item ->
                FlashcardCard(
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