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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.utils.rememberBitmapFromAssets

// ✨ BƯỚC 1: TINH GỌN CHỮ KÝ HÀM, LOẠI BỎ CÁC THAM SỐ THỪA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBack: () -> Unit,
    onLessonComplete: () -> Unit,
    viewModel: LearnViewModel = viewModel() // ViewModel sẽ tự lấy dữ liệu
) {
    val context = LocalContext.current
    val cards by viewModel.cards.collectAsState()
    val index by viewModel.index.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()
    var player: MediaPlayer? by remember { mutableStateOf(null) }

    // ✨ LẤY TOPICID TỪ CARD ĐẦU TIÊN ĐỂ HIỂN THỊ TIÊU ĐỀ
    // Cách này giúp UI không cần biết trước topicId
    val topicIdForTitle = cards.firstOrNull()?.topicId ?: "Loading..."

    DisposableEffect(Unit) {
        onDispose {
            player?.release()
        }
    }

    // ✨ BƯỚC 2: KHÔNG CẦN LAUNCHEDEFFECT ĐỂ GỌI viewModel.load() NỮA
    // ViewModel đã tự làm việc này trong khối init

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onLessonComplete()
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

        if (cards.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val currentCard = cards.getOrNull(index) ?: return@Scaffold

        // ✨ --- BẮT ĐẦU CHỈNH SỬA BỐ CỤC --- ✨
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // 1. Bỏ SpaceBetween để có thể tự kiểm soát khoảng cách
            verticalArrangement = Arrangement.Top
        ) {
            // Thẻ học (giữ nguyên)
            LearnFlashcard(card = currentCard)

            // 2. Dùng Spacer với weight để tạo khoảng trống co giãn, đẩy nội dung xuống dưới
            Spacer(modifier = Modifier.weight(1f))

            // Khối từ vựng và nút nghe (giữ nguyên)
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

            // 3. Thêm khoảng trống cố định phía trên nút bấm chính
            Spacer(modifier = Modifier.height(32.dp))

            // Nút Finish/Continue (giữ nguyên)
            Button(
                onClick = { viewModel.next() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835))
            ) {
                Text(
                    if (index == cards.size - 1) "FINISH" else "CONTINUE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}


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
        val previewCard = FlashcardEntity(
            id = "pre_01", topicId = "preview",
            name = "alligator", nameVi = "cá sấu",
            imagePath = "images/animals/alligator.png",
            soundPath = "", lessonNumber = 1
        )
        Scaffold(containerColor = Color(0xFFE3F2FD)) { padding ->
            // ✨ ÁP DỤNG BỐ CỤC MỚI CHO PREVIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LearnFlashcard(card = previewCard)

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(previewCard.name.uppercase(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(previewCard.nameVi, fontSize = 20.sp, color = Color.Gray)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835))
                    ) {
                        Icon(Icons.Filled.VolumeUp, "Nghe", modifier = Modifier.size(36.dp), tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835))
                ) {
                    Text("FINISH", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    }
}
