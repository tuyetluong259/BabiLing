package com.example.babiling.ui.screens.topic.result

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // ✨ THÊM IMPORT NÀY
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResultScreen(
    topicId: String,
    viewModel: ResultViewModel = viewModel(),
    // ✨ THÊM MỘT CALLBACK ĐỂ ĐÓNG MÀN HÌNH NÀY ✨
    onFinish: () -> Unit
) {
    // Lắng nghe trạng thái từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Tải dữ liệu khi màn hình được tạo lần đầu
    LaunchedEffect(topicId) {
        viewModel.loadResult(topicId)
    }

    val percent = if (uiState.totalCards > 0) {
        uiState.learnedCards / uiState.totalCards.toFloat()
    } else {
        0f
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text("Đang đồng bộ và tải kết quả...", modifier = Modifier.padding(top = 16.dp))
        } else {
            Text(
                text = "Tổng kết",
                style = MaterialTheme.typography.headlineLarge, // Dùng headlineLarge cho nổi bật
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "${(percent * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Đã học ${uiState.learnedCards} / ${uiState.totalCards} thẻ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ✨ THÊM NÚT "HOÀN THÀNH" ✨
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onFinish, // Gọi callback khi người dùng nhấn nút
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("HOÀN THÀNH")
            }
        }
    }
}
