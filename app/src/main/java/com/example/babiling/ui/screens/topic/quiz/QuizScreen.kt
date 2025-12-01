package com.example.babiling.ui.screens.topic.quiz

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.copy
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    // 1. ✨ ĐÃ SỬA: Cho phép topicId có thể null
    topicId: String?,
    onNavigateBack: () -> Unit
) {
    // Khởi tạo ViewModel và các trạng thái
    val viewModel: QuizViewModel = viewModel()
    val cards by viewModel.cards.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showResult by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Tải dữ liệu khi màn hình được tạo hoặc topicId thay đổi
    LaunchedEffect(key1 = topicId) {
        viewModel.load(topicId) // ViewModel sẽ tự xử lý logic null/rỗng
        // Reset lại trạng thái khi vào chủ đề mới
        currentIndex = 0
        showResult = false
        selectedOption = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // 2. ✨ ĐÃ SỬA: Hiển thị tiêu đề linh hoạt
                title = {
                    Text(
                        if (topicId.isNullOrEmpty()) "Ôn tập tất cả"
                        else "Ôn tập: ${topicId.replaceFirstChar { it.uppercase() }}"
                    )
                },
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
        if (currentIndex >= cards.size) {
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
        val currentCard = cards[currentIndex]
        val options = remember(currentCard) {
            // Tạo 3 lựa chọn ngẫu nhiên
            val otherCards = cards.filter { it.id != currentCard.id }.shuffled().take(3)
            (otherCards.map { it.name } + currentCard.name).shuffled()
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Câu hỏi ${currentIndex + 1} / ${cards.size}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Hiển thị hình ảnh
            val bitmap = remember(currentCard.imagePath) {
                try {
                    context.assets.open(currentCard.imagePath).use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    null // Trả về null nếu không tìm thấy ảnh
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = currentCard.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Cho ảnh chiếm không gian linh hoạt
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không có hình ảnh")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị các lựa chọn
            options.forEach { optionText ->
                OptionButton(
                    text = optionText,
                    isSelected = selectedOption == optionText,
                    isCorrect = optionText == currentCard.name,
                    showResult = showResult,
                    onClick = {
                        if (!showResult) { // Chỉ cho phép trả lời một lần mỗi câu
                            selectedOption = optionText
                            showResult = true
                            viewModel.submitAnswer(currentCard, optionText == currentCard.name)
                        }
                    }
                )
            }

            // Nút "Tiếp" chỉ hiển thị sau khi đã trả lời
            if (showResult) {
                Button(
                    onClick = {
                        currentIndex++
                        showResult = false // Reset lại để chuẩn bị cho câu tiếp theo
                        selectedOption = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Tiếp tục")
                }
            } else {
                // Thêm một Box trống để giữ nguyên vị trí, tránh giao diện bị "giật"
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit
) {
    // 3. ✨ ĐÃ SỬA: Cải thiện UI, đổi màu khi trả lời
    val backgroundColor by animateColorAsState(
        targetValue = if (showResult && isCorrect) {
            CorrectGreen.copy(alpha = 0.3f)
        } else if (showResult && isSelected && !isCorrect) {
            IncorrectRed.copy(alpha = 0.3f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(300)
    )

    val borderColor by animateColorAsState(
        targetValue = if (showResult && isCorrect) {
            CorrectGreen
        } else if (showResult && isSelected && !isCorrect) {
            IncorrectRed
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300)
    )

    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}
