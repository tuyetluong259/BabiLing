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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.R
import com.example.babiling.ui.theme.*
import androidx.compose.ui.platform.LocalContext

// Danh sách màu có thể được giữ ở đây để không cần tạo lại mỗi lần recompose
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
    onLessonSelected: (topicId: String, lessonNumber: Int) -> Unit,
    onNavigateBack: () -> Unit,
    // ✨ 1. Khởi tạo ViewModel với Factory tương ứng ✨
// ✨ SỬA LỖI: Truyền cả `context` và `topicId` vào Factory theo đúng thứ tự ✨
    viewModel: LessonViewModel = viewModel(
        factory = LessonViewModelFactory(LocalContext.current, topicId)
    )
) {
    // ✨ 2. Lấy danh sách bài học từ StateFlow mới là `lessonsUiState` ✨
    val lessons by viewModel.lessonsUiState.collectAsState()

    // ✨ 3. Tách phần UI ra một hàm riêng để dễ quản lý và preview ✨
    LessonSelectionScreenContent(
        lessons = lessons, // Truyền vào danh sách bài học
        onLessonSelected = { lessonNumber ->
            onLessonSelected(topicId, lessonNumber)
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun LessonSelectionScreenContent(
    // ✨ 4. Thay đổi tham số: nhận trực tiếp một List<LessonUiState> ✨
    lessons: List<LessonUiState>,
    onLessonSelected: (lessonNumber: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                tint = DarkGreyIcon
            )
        }

        // Các Image trang trí giữ nguyên
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
                color = BrownRedTitle,
                modifier = Modifier.padding(top = 80.dp, bottom = 30.dp),
                textAlign = TextAlign.Center
            )

            // ✨ 5. Hiển thị vòng xoay loading nếu danh sách bài học rỗng ✨
            // (ViewModel cung cấp initialValue là emptyList)
            if (lessons.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 150.dp)
                ) {
                    // ✨ 6. Sử dụng dữ liệu trực tiếp từ `lessons` ✨
                    itemsIndexed(lessons) { index, lesson -> // lesson bây giờ là LessonUiState
                        val color = lessonColors[index % lessonColors.size]

                        LessonItem(
                            lessonNumber = lesson.number, // Lấy từ lesson.number
                            color = color,
                            isCompleted = lesson.isCompleted, // Lấy từ lesson.isCompleted
                            onClick = {
                                onLessonSelected(lesson.number)
                            }
                        )
                    }
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

// ✨ 7. Cập nhật Preview để sử dụng hàm Content mới và dữ liệu giả theo cấu trúc mới ✨
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun LessonSelectionScreenPreview() {
    BabiLingTheme {
        LessonSelectionScreenContent(
            lessons = listOf(
                LessonUiState(number = 1, isCompleted = true, totalCards = 10),
                LessonUiState(number = 2, isCompleted = false, totalCards = 12),
                LessonUiState(number = 3, isCompleted = true, totalCards = 8),
                LessonUiState(number = 4, isCompleted = false, totalCards = 15),
            ),
            onLessonSelected = { _ -> },
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun LessonSelectionScreenLoadingPreview() {
    BabiLingTheme {
        LessonSelectionScreenContent(
            lessons = emptyList(), // Trạng thái loading tương ứng với danh sách rỗng
            onLessonSelected = { _ -> },
            onNavigateBack = {}
        )
    }
}
