package com.example.babiling.ui.screens.topic.quiz

import com.google.accompanist.flowlayout.MainAxisAlignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.ui.theme.BabiLingTheme
import com.example.babiling.utils.rememberBitmapFromAssets
import com.google.accompanist.flowlayout.FlowRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    topicId: String?,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val questions by viewModel.questions.collectAsState()
    val contextTopicId = topicId ?: "all"

    LaunchedEffect(contextTopicId) {
        viewModel.load(topicId)
    }

    Scaffold(
        containerColor = Color(0xFFFEF3E0), // Màu nền màu kem giống ảnh
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (topicId.isNullOrEmpty()) "Ôn tập tổng hợp" else "Ôn tập: ${topicId.replaceFirstChar { it.uppercase() }}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3D5AFE) // Màu xanh dương cho tiêu đề
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
    ) { paddingValues ->
        if (questions.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            QuizContentContainer(
                modifier = Modifier.padding(paddingValues),
                questions = questions,
                onSubmitAnswer = { card, isCorrect ->
                    viewModel.submitAnswer(card, isCorrect)
                },
                onBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun QuizContentContainer(
    modifier: Modifier = Modifier,
    questions: List<QuizQuestion>,
    onSubmitAnswer: (FlashcardEntity, Boolean) -> Unit,
    onBack: () -> Unit
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
                    onNext = { currentQuestionIndex++ }
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

@Composable
fun MultipleChoiceQuestionUI(
    modifier: Modifier = Modifier,
    question: QuizQuestion.MultipleChoice,
    onAnswered: (isCorrect: Boolean) -> Unit,
    onNext: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val isAnswered = selectedOption != null

    LaunchedEffect(question) { selectedOption = null }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Từ \"${question.card.nameVi}\" trong tiếng Anh là gì?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
        }
        items(question.options.size) { index ->
            val option = question.options[index]
            OptionButton(
                text = option,
                isSelected = selectedOption == option,
                isCorrect = question.card.name == option,
                isAnswered = isAnswered,
                onClick = {
                    if (!isAnswered) {
                        selectedOption = option
                        onAnswered(question.card.name == option)
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
        item {
            if (isAnswered) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth(0.8f)) { Text("Tiếp tục") }
            }
        }
    }
}

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

    LaunchedEffect(question) {
        answerChars = List(question.card.name.length) { null }
        remainingChars = question.scrambledChars
        isAnswered = false
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("What body is this?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
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

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            mainAxisAlignment = MainAxisAlignment.Center // <<<<< SỬA LẠI THÀNH DÒNG NÀY
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

        Button(
            onClick = {
                if (!isAnswered) {
                    val userAnswer = answerChars.joinToString("")
                    onAnswered(userAnswer.equals(question.card.name, ignoreCase = true))
                    isAnswered = true
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = answerChars.all { it != null } || isAnswered,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDD835), disabledContainerColor = Color(0xFFFDD835).copy(alpha = 0.5f))
        ) {
            Text(
                if (isAnswered) "TIẾP TỤC" else "FINISH",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
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
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
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
        modifier = Modifier.size(48.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(char.uppercase(), fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ✨ --- CÁC COMPOSABLE CŨ ĐÃ ĐƯỢC ĐIỀN ĐẦY ĐỦ --- ✨
@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = when {
            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
            isSelected && !isCorrect -> Color.Red.copy(alpha = 0.5f)
            isCorrect -> Color.Green.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(500), label = "option color"
    )

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        enabled = !isAnswered
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
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Chúc mừng!", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))
        Text(
            "Bạn đã hoàn thành bài ôn tập!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "Kết quả của bạn:",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "$score / $totalQuestions",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(48.dp))
        Button(onClick = onBack) {
            Text("Quay về")
        }
    }
}

@Preview(showBackground = true, name = "Quiz FillInWord Preview")
@Composable
fun QuizScreenFillInWordPreview() {
    BabiLingTheme {
        FillInWordQuestionUI(
            question = QuizQuestion.FillInWord(
                card = FlashcardEntity(
                    id = "body_01",
                    topicId = "body",
                    name = "head",
                    nameVi = "Cái đầu",
                    imagePath = "images/animals/dog.png", // Dùng tạm ảnh
                    soundPath = "",
                    lessonNumber = 1
                ),
                scrambledChars = listOf('a', 'l', 'g', 'e', 'o', 'd', 'p', 'h')
            ),
            onAnswered = {},
            onNext = {}
        )
    }
}
