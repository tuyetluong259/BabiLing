package com.example.babiling.ui.screens.topic.result

import androidx.compose.foundation.layout.*
// ✨ 1. SỬA LẠI IMPORT CHO ĐÚNG TÊN CHUẨN ✨
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
    // Giả sử bạn cũng đã đổi tên ViewModel thành ResultViewModel
    viewModel: ResultViewModel = viewModel(),
    onBack: () -> Unit = {}
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
        horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa theo chiều ngang
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState.isLoading) {
            // ✨ 2. SỬA LẠI TÊN COMPOSABLE ✨
            CircularProgressIndicator()
            Text("Đang tải kết quả...", modifier = Modifier.padding(top = 16.dp))
        } else {
            Text(
                text = "Kết quả chủ đề", // Văn bản phù hợp với màn hình kết quả
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "${(percent * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // ✨ 3. SỬA LẠI TÊN COMPOSABLE VÀ THAM SỐ ✨
            LinearProgressIndicator(
                progress = { percent }, // Tham số đúng là `progress`
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    // Thêm bo tròn cho đẹp hơn
                    .clip(RoundedCornerShape(8.dp))
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
