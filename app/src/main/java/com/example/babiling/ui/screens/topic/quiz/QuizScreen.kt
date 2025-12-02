package com.example.babiling.ui.screens.topic.quiz

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.ui.theme.*
import com.example.babiling.utils.rememberBitmapFromAssets

// Factory để tạo QuizViewModel với Application context
class QuizViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    topicId: String?,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val questions = uiState.questions

    LaunchedEffect(topicId) {
        viewModel.load(topicId)
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        containerColor = Color(0xFFFEF3E0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (topicId.isNullOrEmpty()) "Ôn tập tổng hợp" else "Ôn tập: ${topicId.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D5AFE)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF3D5AFE))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (questions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Không có câu hỏi nào để hiển thị.")
            }
        } else {
            QuizContentContainer(
                modifier = Modifier.padding(innerPadding),
                questions = questions,
                onSubmitAnswer = { card, isCorrect ->
                    viewModel.submitAnswer(card, isCorrect)
                },
                onBack = onNavigateBack,
                viewModel = viewModel
            )
        }
    }
}


@Composable
private fun QuizContentContainer(
    modifier: Modifier = Modifier,
    questions: List<QuizQuestion>,
    onSubmitAnswer: (FlashcardEntity, Boolean) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel
) {
    var score by remember { mutableStateOf(0) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    if (currentQuestion == null) {
        QuizCompleted(modifier = modifier, score = score, totalQuestions = questions.size, onBack = onBack)
    } else {
        when (currentQuestion) {
            is QuizQuestion.MultipleChoice -> {
                MultipleChoiceQuestionUI(
                    modifier = modifier,
                    question = currentQuestion,
                    onAnswered = { isCorrect ->
                        if (isCorrect) score++
                        onSubmitAnswer(currentQuestion.flashcard, isCorrect)
                    },
                    onNext = { currentQuestionIndex++ },
                    viewModel = viewModel
                )
            }
            is QuizQuestion.FillInWord -> {
                FillInWordQuestionUI(
                    modifier = modifier,
                    question = currentQuestion,
                    onAnswered = { isCorrect ->
                        if (isCorrect) score++
                        onSubmitAnswer(currentQuestion.flashcard, isCorrect)
                    },
                    onNext = { currentQuestionIndex++ }
                )
            }
        }
    }
}

// ✨✨✨ LOGIC MỚI CHO TRẮC NGHIỆM ĐƯỢC CẬP NHẬT Ở ĐÂY ✨✨✨
@Composable
fun MultipleChoiceQuestionUI(
    modifier: Modifier = Modifier,
    question: QuizQuestion.MultipleChoice,
    onAnswered: (isCorrect: Boolean) -> Unit,
    onNext: () -> Unit,
    viewModel: QuizViewModel
) {
    // Trạng thái cho đáp án đã chọn
    var selectedOption by remember { mutableStateOf<FlashcardEntity?>(null) }
    // Trạng thái cho biết người dùng đã nhấn "XÁC NHẬN" hay chưa
    var isConfirmed by remember { mutableStateOf(false) }

    // Reset lại trạng thái khi có câu hỏi mới
    LaunchedEffect(question) {
        selectedOption = null
        isConfirmed = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Đẩy nút điều khiển xuống dưới
    ) {
        // Phần câu hỏi và các lựa chọn
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Từ \"${question.card.nameVi}\" trong tiếng Anh là gì?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))

                // Các button đáp án
                question.options.forEach { optionCard ->
                    OptionButton(
                        text = optionCard.name,
                        isSelected = selectedOption?.id == optionCard.id,
                        isCorrect = question.card.name == optionCard.name,
                        isConfirmed = isConfirmed,
                        onClick = {
                            if (!isConfirmed) { // Chỉ cho phép chọn/nghe khi chưa xác nhận
                                selectedOption = optionCard
                                viewModel.playSound(optionCard.soundPath)
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        // Phần các nút điều khiển (XÁC NHẬN / TIẾP TỤC)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isConfirmed) {
                    // Hiện nút "TIẾP TỤC" sau khi đã xác nhận
                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
                    ) {
                        Text("TIẾP TỤC", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                } else {
                    // Hiện nút "XÁC NHẬN" khi chưa xác nhận
                    Button(
                        onClick = {
                            isConfirmed = true
                            val isCorrect = selectedOption?.name == question.card.name
                            onAnswered(isCorrect)
                        },
                        enabled = selectedOption != null, // Chỉ bật khi đã chọn đáp án
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDD835),
                            disabledContainerColor = Color(0xFFFDD835).copy(alpha = 0.5f)
                        )
                    ) {
                        Text("XÁC NHẬN", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInWordQuestionUI(
    modifier: Modifier = Modifier,
    question: QuizQuestion.FillInWord,
    onAnswered: (isCorrect: Boolean) -> Unit,
    onNext: () -> Unit
) {
    var answerChars by remember { mutableStateOf<List<Char?>>(emptyList()) }
    var remainingChars by remember { mutableStateOf<List<Char>>(emptyList()) }
    var isAnswered by remember { mutableStateOf(false) }
    var userAnswerState by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(question) {
        answerChars = List(question.card.name.length) { null }
        remainingChars = question.scrambledChars
        isAnswered = false
        userAnswerState = null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("What is this?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    val bitmap = com.example.babiling.utils.rememberBitmapFromAssets(imagePath = question.card.imagePath)
                    if (bitmap != null) {
                        Image(bitmap = bitmap.asImageBitmap(), contentDescription = question.card.name, modifier = Modifier.size(120.dp))
                    } else {
                        Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) { Text("No Image") }
                    }
                }
            }
        }

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            answerChars.forEachIndexed { index, char ->
                AnswerBox(char = char) {
                    if (char != null && !isAnswered) {
                        val newAnswerChars = answerChars.toMutableList().apply { set(index, null) }
                        answerChars = newAnswerChars
                        remainingChars = (remainingChars + char).shuffled()
                    }
                }
            }
        }

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            remainingChars.forEachIndexed { index, char ->
                CharacterChip(char = char) {
                    if (!isAnswered) {
                        val firstEmptyIndex = answerChars.indexOfFirst { it == null }
                        if (firstEmptyIndex != -1) {
                            answerChars = answerChars.toMutableList().apply { set(firstEmptyIndex, char) }
                            remainingChars = remainingChars.toMutableList().apply { removeAt(index) }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (userAnswerState != null) {
                if (userAnswerState == true) {
                    Text("Chính xác!", color = CorrectGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                } else {
                    Text("Đáp án đúng là: ${question.card.name}", color = IncorrectRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isAnswered) {
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))) {
                    Text("TIẾP TỤC", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            } else {
                Button(
                    onClick = {
                        val userAnswer = answerChars.joinToString("")
                        val isCorrect = userAnswer.equals(question.card.name, ignoreCase = true)
                        userAnswerState = isCorrect
                        onAnswered(isCorrect)
                        isAnswered = true
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = answerChars.all { it != null },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835), disabledContainerColor = Color(0xFFFDD835).copy(alpha = 0.5f))
                ) {
                    Text("KIỂM TRA", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun AnswerBox(char: Char?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp, 60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFCEE6FF), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(char.uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CharacterChip(char: Char, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(char.uppercase(), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ✨✨✨ LOGIC MỚI CHO VIỀN VÀ MÀU NỀN CỦA OPTIONBUTTON ✨✨✨
@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isConfirmed: Boolean,
    onClick: () -> Unit
) {
    val defaultColor = Color(0xFFCEE6FF) // Màu nền xanh nhạt mặc định

    // Màu nền thay đổi sau khi xác nhận
    val containerColor by animateColorAsState(
        targetValue = if (!isConfirmed) {
            defaultColor // Trước khi xác nhận, luôn là màu mặc định
        } else {
            when {
                isCorrect -> CorrectGreen // Đáp án đúng -> Xanh
                isSelected && !isCorrect -> IncorrectRed // Chọn sai -> Đỏ
                else -> defaultColor.copy(alpha = 0.5f) // Các đáp án khác -> Mờ đi
            }
        },
        animationSpec = tween(500), label = "option container color"
    )

    // Màu viền để thể hiện lựa chọn trước khi xác nhận
    val borderColor by animateColorAsState(
        targetValue = if (isSelected && !isConfirmed) {
            Color(0xFF3D5AFE) // Viền xanh đậm khi được chọn
        } else {
            Color.Transparent // Không có viền
        },
        animationSpec = tween(200), label = "option border color"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)), // Thêm viền
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text, fontSize = 18.sp, modifier = Modifier.padding(vertical = 8.dp))
    }
}


@Composable
fun QuizCompleted(
    modifier: Modifier = Modifier,
    score: Int,
    totalQuestions: Int,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chúc mừng!",
            style = MaterialTheme.typography.headlineMedium,
            color = IncorrectRed
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Bạn đã hoàn thành bài ôn tập!",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = LessonOrange
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Kết quả của bạn:",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$score / $totalQuestions",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = IncorrectRed
            )
        )
        {
            Text("Quay về", fontSize = 18.sp)
        }
    }
}

// --- Preview Section ---
private class FakeQuizViewModel(application: Application) : QuizViewModel(application) {
    override fun load(topicId: String?) { /* Do nothing in preview */ }
    override fun playSound(soundPath: String) { /* Do nothing in preview, just log */ }
}

@Preview(showBackground = true, name = "QuizScreen Multiple Choice")
@Composable
private fun QuizScreenPreview() {
    val context = LocalContext.current
    BabiLingTheme {
        QuizScreen(
            topicId = "fruits",
            onNavigateBack = {},
            viewModel = FakeQuizViewModel(context.applicationContext as Application)
        )
    }
}

@Preview(showBackground = true, name = "Quiz FillInWord Preview")
@Composable
private fun QuizFillInWordPreview() {
    BabiLingTheme {
        FillInWordQuestionUI(
            question = QuizQuestion.FillInWord(
                card = FlashcardEntity(
                    id = "1", topicId = "body", name = "shoulder", nameVi = "Vai",
                    imagePath = "", soundPath = "", lessonNumber = 1
                ),
                scrambledChars = "relduoshas".toList()
            ),
            onAnswered = {},
            onNext = {}
        )
    }
}

@Preview(showBackground = true, name = "Quiz Completed Preview")
@Composable
private fun QuizCompletedPreview() {
    BabiLingTheme {
        QuizCompleted(score = 8, totalQuestions = 10, onBack = {})
    }
}
