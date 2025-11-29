package com.example.babiling.ui.screens.topic.progress

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProgressScreen(
    topicId: String,
    viewModel: ProgressViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    // Lắng nghe trạng thái từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Tải dữ liệu khi màn hình được tạo lần đầu
    LaunchedEffect(topicId) {
        viewModel.loadProgress(topicId)
    }

    val percent = if (uiState.totalCards > 0) {
        uiState.learnedCards / uiState.totalCards.toFloat()
    } else {
        0f
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text("Đang tải tiến độ...", modifier = Modifier.padding(top = 16.dp))
        } else {
            Text(
                text = "Tiến độ của bạn",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "${(percent * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LinearProgressIndicator(
                progress = { percent }, // Dùng lambda syntax mới
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                // Có thể thêm bo tròn cho đẹp hơn
                // .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Đã học ${uiState.learnedCards} / ${uiState.totalCards} thẻ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
