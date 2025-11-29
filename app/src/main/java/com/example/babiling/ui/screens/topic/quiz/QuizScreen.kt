package com.example.babiling.ui.screens.topic.quiz

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Thêm @OptIn để sử dụng các API mới của Material 3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    topicId: String,
    // Đổi tên onBack thành onNavigateBack cho nhất quán
    onNavigateBack: () -> Unit
) {
    // Khởi tạo ViewModel và các trạng thái
    val viewModel: QuizViewModel = viewModel()
    val cards by viewModel.cards.collectAsState()
    var idx by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) } // Trạng thái để kiểm tra câu trả lời
    var lastAnswerCorrect by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Tải dữ liệu khi topicId thay đổi
    LaunchedEffect(topicId) {
        viewModel.load(topicId)
        // Reset lại trạng thái khi vào chủ đề mới
        idx = 0
        showResult = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ôn tập: ${topicId.replaceFirstChar { it.uppercase() }}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        // Giao diện tải dữ liệu
        if (cards.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("Đang tải câu hỏi...", Modifier.padding(top = 70.dp))
            }
            return@Scaffold
        }

        // Giao diện khi đã hoàn thành tất cả câu hỏi
        if (idx >= cards.size) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Hoàn thành!", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Tuyệt vời, quay lại")
                }
            }
            return@Scaffold
        }

        // Giao diện câu đố chính
        val current = cards[idx]
        val options = remember(cards, idx) {
            val others = cards.filter { it.id != current.id }.shuffled().take(2).map { it.name }
            (others + current.name).shuffled()
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hiển thị hình ảnh
            val bitmap = remember(current.imagePath) {
                try {
                    val input = context.assets.open(current.imagePath)
                    BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    null // Trả về null nếu không tìm thấy ảnh
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = current.name,
                    modifier = Modifier
                        .size(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có hình ảnh")
                }
            }

            // Hiển thị các lựa chọn
            options.forEach { opt ->
                Text(
                    text = opt,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            if (!showResult) { // Chỉ cho phép trả lời một lần
                                val correct = opt == current.name
                                lastAnswerCorrect = correct
                                viewModel.submitAnswer(current, correct)
                                showResult = true
                            }
                        }
                        .padding(16.dp)
                )
            }

            // Nút "Tiếp" chỉ hiển thị sau khi đã trả lời
            if (showResult) {
                Button(
                    onClick = {
                        idx++
                        showResult = false // Reset lại để chuẩn bị cho câu tiếp theo
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (lastAnswerCorrect) "Chính xác! Tiếp tục" else "Sai rồi! Tiếp tục")
                }
            }
        }
    }
}
