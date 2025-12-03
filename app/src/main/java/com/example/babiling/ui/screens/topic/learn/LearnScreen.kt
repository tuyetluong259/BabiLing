package com.example.babiling.ui.screens.topic.learn

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // ✨ 1. BẠN CẦN IMPORT NÀY ✨
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.utils.rememberBitmapFromAssets

/**
 * Màn hình học thẻ (flashcard).
 * ✨ HOÀN THIỆN: Đã được sửa để có thể hoạt động với NavGraph và ViewModelFactory.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBack: () -> Unit,
    onLessonComplete: (topicId: String) -> Unit,
    // ViewModel sẽ được tạo ở đây, sử dụng Factory mà chúng ta đã làm.
    // Các giá trị topicId và lessonNumber phải được truyền vào từ NavHost.
    topicId: String,
    lessonNumber: Int
) {
    // Tạo ViewModel bằng cách sử dụng Factory
    val viewModel: LearnViewModel = viewModel(
        factory = LearnViewModelFactory(
            context = LocalContext.current,
            topicId = topicId,
            lessonNumber = lessonNumber
        )
    )

    // Từ đây trở xuống, code của bạn đã rất tốt, chỉ cần sử dụng viewModel đã được tạo ở trên.
    val context = LocalContext.current
    val cards by viewModel.cards.collectAsState()
    val index by viewModel.index.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    var player: MediaPlayer? by remember { mutableStateOf(null) }

    // Dùng topicId được truyền vào để hiển thị tiêu đề ngay lập tức
    val topicIdForTitle = topicId

    DisposableEffect(Unit) {
        onDispose {
            player?.release()
        }
    }

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onLessonComplete(topicId)
        }
    }

    Scaffold(
        containerColor = Color(0xFFE3F2FD),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        topicIdForTitle.replaceFirstChar { it.titlecase() },
                        color = Color(0xFF00B0FF),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF00B0FF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->

        if (cards.isEmpty() && !isFinished) { // Thêm điều kiện !isFinished để không hiển thị loading khi vừa học xong
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // getOrNull an toàn hơn, tránh crash nếu index bị sai lệch
        val currentCard = cards.getOrNull(index) ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LearnFlashcard(card = currentCard)

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(currentCard.name.uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(currentCard.nameVi, fontSize = 20.sp, color = Color.Gray)
                Spacer(Modifier.height(24.dp))
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
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835))
                ) {
                    Icon(
                        Icons.Filled.VolumeUp,
                        contentDescription = "Nghe",
                        modifier = Modifier.size(36.dp),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.next() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835))
            ) {
                Text(
                    // Sử dụng `lastIndex` để code an toàn và dễ đọc hơn
                    if (index == cards.lastIndex) "FINISH" else "CONTINUE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}


// --- Phần Preview giữ nguyên, nó không ảnh hưởng đến code chính ---

@Composable
fun LearnFlashcard(card: FlashcardEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(2.dp, Color(0xFF00B0FF).copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val bitmap = rememberBitmapFromAssets(imagePath = card.imagePath)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = card.name,
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Divider(
                color = Color(0xFF00B0FF).copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${card.name.first().uppercaseChar()}${card.name.first()}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = Color(0xFF00B0FF).copy(alpha = 0.5f), thickness = 2.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Magenta)) {
                            append(card.name.first())
                        }
                        append(card.name.substring(1))
                    }
                    Text(
                        text = annotatedString,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Learn Screen Preview")
@Composable
fun LearnScreenPreview() {
    BabiLingTheme {
        // Preview không dùng ViewModel nên nó vẫn hoạt động bình thường
        LearnScreen(onBack = {}, onLessonComplete = {}, topicId = "animals", lessonNumber = 1)
    }
}
