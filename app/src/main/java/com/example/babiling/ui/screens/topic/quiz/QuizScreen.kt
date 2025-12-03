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
    // ✨ BỎ `onFinish` ĐI, CHỈ DÙNG `onNavigateBack` ✨
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
                        if (topicId.isNullOrEmpty() || topicId == "all") "Ôn tập tổng hợp" else "Ôn tập: ${topicId.replaceFirstChar { it.uppercase() }}",
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
                // ✨ CHỈ CẦN TRUYỀN onNavigateBack XUỐNG ✨
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
    onBack: () -> Unit, // ✨ CHỈ CẦN THAM SỐ onBack ✨
    viewModel: QuizViewModel
) {
    var score by remember { mutableStateOf(0) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    if (currentQuestion == null) {
        // ✨ GỌI onBack KHI HOÀN THÀNH ✨
        // Logic điều hướng sẽ được xử lý ở NavGraph, cách làm này vẫn đúng!
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

// ✨✨✨ MÀN HÌNH TỔNG KẾT QUIZ ✨✨✨
@Composable
fun QuizCompleted(
    modifier: Modifier = Modifier,
    score: Int,
    totalQuestions: Int,
    onBack: () -> Unit // ✨ DÙNG LẠI onBack, KHÔNG CẦN onFinish ✨
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("HOÀN THÀNH!", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Spacer(Modifier.height(16.dp))
        Text("Kết quả của bạn:", style = MaterialTheme.typography.headlineMedium)
        Text("$score / $totalQuestions", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onBack, // ✨ GỌI onBack KHI NHẤN NÚT ✨
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
        ) {
            Text("TIẾP TỤC", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


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
                    val bitmap = rememberBitmapFromAssets(imagePath = question.card.imagePath)
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
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isAnswered) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5AFE))
                ) {
                    Text("TIẾP TỤC", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            } else {
                Button(
                    onClick = {
                        val isCorrect = answerChars.joinToString("") == question.card.name
                        onAnswered(isCorrect)
                        userAnswerState = isCorrect
                        isAnswered = true
                    },
                    enabled = answerChars.none { it == null },
                    modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
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


@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isConfirmed: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isConfirmed && isCorrect && isSelected -> Color(0xFF4CAF50) // Đúng và được chọn
            isConfirmed && isSelected && !isCorrect -> Color(0xFFF44336) // Sai và được chọn
            isConfirmed && isCorrect -> Color(0xFF4CAF50) // Đáp án đúng (không được chọn)
            isSelected -> Color(0xFF3D5AFE) // Đang được chọn (chưa xác nhận)
            else -> Color.Gray.copy(alpha = 0.3f)
        },
        animationSpec = tween(500), label = ""
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isConfirmed && isCorrect && isSelected -> Color(0xFFE8F5E9)
            isConfirmed && isSelected && !isCorrect -> Color(0xFFFFEBEE)
            isConfirmed && isCorrect -> Color(0xFFE8F5E9)
            else -> Color.White
        },
        animationSpec = tween(500), label = ""
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}


@Composable
fun AnswerBox(char: Char?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (char != null) Color.White else Color(0xFFE0E0E0))
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (char != null) {
            Text(text = char.toString().uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CharacterChip(char: Char, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = char.toString().uppercase(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Preview cho màn hình hoàn thành
@Preview(showBackground = true, name = "QuizCompleted Preview")
@Composable
private fun QuizCompletedPreview() {
    BabiLingTheme {
        Surface(color = Color(0xFFFEF3E0)) {
            QuizCompleted(score = 8, totalQuestions = 10, onBack = {})
        }
    }
}
