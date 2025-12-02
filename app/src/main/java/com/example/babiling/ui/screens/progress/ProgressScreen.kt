package com.example.babiling.ui.screens.progress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.R
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.BalooThambiFamily
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

// ✨ PHIÊN BẢN HOÀN THIỆN - Đã tương thích với ViewModel mới ✨

@Composable
fun ProgressScreen(
    paddingValues: PaddingValues
) {
    // 1. Khởi tạo ViewModel với Factory (giữ nguyên, đã đúng)
    val viewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModelFactory(LocalContext.current)
    )

    // ✨ SỬA ĐỔI 1: Lắng nghe một uiState duy nhất từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Hàm loadProgress() đã được gọi trong khối init của ViewModel, không cần gọi lại ở đây

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = "Tiến trình bài học",
                fontFamily = BalooThambiFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color(0xFFD32F2F),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )

            // ✨ SỬA ĐỔI 2: Cập nhật logic hiển thị dựa trên uiState mới
            when {
                // Dùng uiState.isLoading để kiểm tra trạng thái tải
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Dùng uiState.topicsWithProgress để kiểm tra danh sách có rỗng không
                uiState.topicsWithProgress.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Bạn chưa hoàn thành bài học nào.\nHãy bắt đầu học ngay!",
                            textAlign = TextAlign.Center,
                            fontFamily = BalooThambiFamily,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Image(
                            painter = painterResource(id = R.drawable.decor7),
                            contentDescription = "Mascot",
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .size(90.dp)
                        )
                    }
                }
                // Nếu có dữ liệu, hiển thị danh sách
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Lặp qua danh sách uiState.topicsWithProgress
                        items(uiState.topicsWithProgress, key = { it.topic.id }) { topicInfo ->
                            // Truyền dữ liệu mới vào Composable Item
                            TopicProgressItem(
                                topicInfo = topicInfo,
                                onClick = { /* TODO: Xử lý khi nhấn vào một chủ đề */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✨ SỬA ĐỔI 3: Cập nhật hàm Composable Item để nhận dữ liệu mới
@Composable
fun TopicProgressItem(topicInfo: TopicProgressInfo, onClick: () -> Unit) {
    val topic = topicInfo.topic
    val iconRes = getIconResourceForTopic(topic.id)
    val textColor = getTextColorForTopic(topic.id)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = topic.name,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = topic.name,
                    fontFamily = BalooThambiFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Dùng thanh tiến trình
                LinearProgressIndicator(
                    progress = { topicInfo.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = textColor,
                    trackColor = Color.LightGray.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Hiển thị số thẻ đã học / tổng số thẻ
                Text(
                    text = "${topicInfo.learnedCards} / ${topicInfo.totalCards} thẻ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

// --- Các hàm tiện ích getIconResourceForTopic và getTextColorForTopic giữ nguyên ---
@Composable
private fun getIconResourceForTopic(topicId: String): Int {
    return when (topicId) {
        "greetings" -> R.drawable.greetings
        "body" -> R.drawable.body
        "colors" -> R.drawable.color
        "fruit" -> R.drawable.fruit
        "animals" -> R.drawable.animals
        "toys" -> R.drawable.toys
        else -> R.drawable.logo
    }
}

@Composable
private fun getTextColorForTopic(topicId: String): Color {
    val colorRes = when (topicId) {
        "greetings" -> R.color.greetings_text
        "body" -> R.color.body_text
        "colors" -> R.color.colors_text
        "fruit" -> R.color.fruit_text
        "animals" -> R.color.animals_text
        "toys" -> R.color.toys_text
        else -> R.color.black
    }
    return colorResource(id = colorRes)
}

// Preview không cần thay đổi
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ProgressScreenPreview() {
    BabiLingTheme {
        Scaffold { innerPadding ->
            ProgressScreen(paddingValues = innerPadding)
        }
    }
}
