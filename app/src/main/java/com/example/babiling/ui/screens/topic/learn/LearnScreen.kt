package com.example.babiling.ui.screens.topic.learn

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.ui.theme.Learn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    topicId: String,
    viewModel: LearnViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val cards by viewModel.cards.collectAsState()
    val index by viewModel.index.collectAsState()
    var player: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            player?.release()
        }
    }

    LaunchedEffect(topicId) { viewModel.load(topicId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Học từ vựng") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (cards.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text("Đang tải thẻ...", Modifier.padding(top = 80.dp))
            }
            return@Scaffold
        }

        val currentCard = cards.getOrNull(index)

        // --- ✨ BỐ CỤC MỚI THEO YÊU CẦU ✨ ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Thêm padding chung cho toàn màn hình
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentCard == null) {
                // Trường hợp thẻ không tồn tại (sẽ không xảy ra nếu logic đúng)
                Spacer(modifier = Modifier.weight(1f))
            } else {
                // ✨ THAY ĐỔI 1: Phóng to Card và cho nó chiếm phần lớn không gian trên
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f), // Card chiếm 70% không gian dọc còn lại
                    colors = CardDefaults.cardColors(
                        containerColor = Learn // Bạn có thể thay bằng màu khác
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize() // Lấp đầy Card
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center // Canh giữa nội dung trong Card
                    ) {
                        // --- Ảnh của thẻ ---
                        val bitmap = remember(currentCard.imagePath) {
                            try {
                                val input = context.assets.open(currentCard.imagePath)
                                BitmapFactory.decodeStream(input)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = currentCard.name,
                                contentScale = ContentScale.Fit, // Dùng Fit để ảnh không bị cắt
                                modifier = Modifier
                                    .weight(1f) // Cho ảnh chiếm không gian còn lại
                                    .clip(MaterialTheme.shapes.medium)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- Tên tiếng Anh và tiếng Việt ---
                        Text(currentCard.name, fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(currentCard.nameVi, fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center)
                    }
                }

                // ✨ THAY ĐỔI 2: Dịch nút Voice xuống và tạo khoảng trống
                // Dùng Spacer với weight để đẩy nút Voice và thanh điều hướng xuống dưới
                Spacer(modifier = Modifier.weight(0.1f))

                // --- Nút Nghe hình tròn ---
                Button(
                    onClick = {
                        player?.release()
                        try {
                            val afd = context.assets.openFd(currentCard.soundPath)
                            player = MediaPlayer().apply {
                                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                prepare()
                                start()
                            }
                        } catch (e: Exception) {
                            Log.e("BabiLing_Audio", "Lỗi phát âm thanh: ${currentCard.soundPath}", e)
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Nghe",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Đẩy thanh điều hướng xuống đáy
            Spacer(modifier = Modifier.weight(0.2f))

            // ✨ THAY ĐỔI 3: Giữ nguyên thanh điều hướng ở cuối
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.previous() },
                    enabled = index > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Thẻ trước", modifier = Modifier.size(48.dp))
                }

                Text(
                    text = "${index + 1} / ${cards.size}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = { viewModel.next() },
                    enabled = index < cards.size - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Thẻ tiếp theo", modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

// ----- PHẦN PREVIEW GIỮ NGUYÊN -----
@Preview(showBackground = true, name = "Learn Screen Preview")
@Composable
fun LearnScreenPreview() {
    BabiLingTheme {
        LearnScreen(topicId = "preview", onBack = {})
    }
}
