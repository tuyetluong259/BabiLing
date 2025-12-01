package com.example.babiling.ui.screens.topic.learn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.* // ✨ IMPORT TẤT CẢ CÁC MÀU ✨

// ✨ GỌI CÁC MÀU TỪ Color.kt THAY VÌ HARDCODE ✨
private val lessonColors = listOf(
    LessonBlue,
    LessonGreen,
    LessonPink,
    LessonYellow,
    LessonRed,
    LessonPurple,
    LessonOrange
)

@Composable
fun LessonSelectionScreen(
    topicId: String,
    // viewModel: LessonViewModel = viewModel(),
    lessons: List<Int>,
    completedLessons: Set<Int>,
    onLessonSelected: (topicId: String, lessonNumber: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // ✨ SỬ DỤNG MÀU ĐÃ ĐỊNH NGHĨA ✨
            .background(LightBlueBackground)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                modifier = Modifier.size(32.dp),
                // ✨ SỬ DỤNG MÀU ĐÃ ĐỊNH NGHĨA ✨
                tint = DarkGreyIcon
            )
        }

        Image(
            painter = painterResource(id = R.drawable.decor7),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .size(60.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.decor9),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 48.dp)
                .size(120.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Chọn bài học",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                // ✨ SỬ DỤNG MÀU ĐÃ ĐỊNH NGHĨA ✨
                color = BrownRedTitle,
                modifier = Modifier.padding(top = 80.dp, bottom = 30.dp),
                textAlign = TextAlign.Center
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 150.dp)
            ) {
                itemsIndexed(lessons) { index, lessonNumber ->
                    val color = lessonColors[index % lessonColors.size]
                    val isCompleted = completedLessons.contains(lessonNumber)

                    LessonItem(
                        lessonNumber = lessonNumber,
                        color = color,
                        isCompleted = isCompleted,
                        onClick = {
                            onLessonSelected(topicId, lessonNumber)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonItem(
    lessonNumber: Int,
    color: Color,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã hoàn thành",
                    modifier = Modifier.size(36.dp),
                    // ✨ SỬ DỤNG MÀU ĐÃ ĐỊNH NGHĨA ✨
                    tint = DarkGreenCheck
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(55.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Bài $lessonNumber",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// Preview không thay đổi
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun LessonSelectionScreenPreview() {
    BabiLingTheme {
        LessonSelectionScreen(
            topicId = "animals",
            lessons = listOf(1, 2, 3, 4, 5),
            completedLessons = setOf(1, 3),
            onLessonSelected = { _, _ -> },
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun LessonSelectionScreenSinglePreview() {
    BabiLingTheme {
        LessonSelectionScreen(
            topicId = "greetings",
            lessons = listOf(1),
            completedLessons = emptySet(),
            onLessonSelected = { _, _ -> },
            onNavigateBack = {}
        )
    }
}
