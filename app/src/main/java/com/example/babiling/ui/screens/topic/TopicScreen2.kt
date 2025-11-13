package com.example.babiling.ui.screens.topic

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.*

/* **************************************************************************
 * CHÚ Ý QUAN TRỌNG:
 * Dữ liệu dưới đây sử dụng ID tài nguyên ảnh (R.drawable.xxx).
 * BẠN PHẢI đảm bảo các tệp ảnh tương ứng đã có trong thư mục res/drawable
 * của dự án để code chạy được. Nếu không, sẽ xảy ra lỗi khi chạy ứng dụng.
 ************************************************************************** */

// -----------------------------------------------------------------------------
// 1. DATA VÀ DỮ LIỆU MẪU CHO MÀN HÌNH 2
// -----------------------------------------------------------------------------

// ✨ Đổi tên data class
data class Topic2(
    val title: String,
    @DrawableRes val imageResId: Int,
    val textColor: Color
)

// ✨ Đổi tên hàm và sửa lỗi logic
fun getTopics2(): List<Topic2> {
    // Giả sử màn hình 2 có dữ liệu khác đi một chút
    return listOf(
        Topic2("School", R.drawable.school, textColor = OliveGreen),
        Topic2("My House", R.drawable.myhouse, textColor = WarmOrange),
        Topic2("Weather", R.drawable.weather, textColor = SkyBlue),
        Topic2("Transport", R.drawable.transport, textColor = AlertRed),
        Topic2("Sports", R.drawable.sports, textColor = IndigoBlue),
        Topic2("Places", R.drawable.places, textColor = ForestGreen)
    )
}

// -----------------------------------------------------------------------------
// 2. COMPOSABLE CHO THẺ CHỦ ĐỀ 2
// -----------------------------------------------------------------------------

// ✨ Đổi tên Composable thẻ
@Composable
fun TopicCard2(topic: Topic2, onClick: (Topic2) -> Unit) {
    Card(
        modifier = Modifier
            .size(170.dp)
            .clickable { onClick(topic) },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.title,
                style = MaterialTheme.typography.titleLarge, // Dùng style từ theme cho nhất quán
                color = topic.textColor
            )
            Spacer(Modifier.weight(1f))
            Image(
                painter = painterResource(id = topic.imageResId),
                contentDescription = topic.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 3. COMPOSABLE CHO MÀN HÌNH CHÍNH 2
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// ✨ Đổi tên màn hình chính
fun TopicSelectionScreen2(
    onNavigateBack: () -> Unit,
    onTopicSelected: (Topic2) -> Unit // ✨ Dùng Topic2
) {
    val topics = getTopics2() // ✨ Gọi hàm mới
    val inlineContentId = "starImage"

    val inlineContent = mapOf(
        Pair(
            inlineContentId,
            InlineTextContent(
                Placeholder(
                    width = 32.sp,
                    height = 32.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Ngôi sao"
                )
            }
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  }, // Thêm tiêu đề cho khác biệt
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryYellow
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = PrimaryYellow
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Chọn chủ đề bé yêu thích để bắt\n")
                    append("đầu học nhé ")
                    appendInlineContent(inlineContentId, "[star]")
                },
                inlineContent = inlineContent,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                color = Color.White // ✨ FIX UI: Sửa màu chữ cho dễ đọc
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(topics) { topic ->
                    // ✨ Gọi TopicCard2
                    TopicCard2(topic = topic) { selectedTopic ->
                        onTopicSelected(selectedTopic)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4. PREVIEW CHO MÀN HÌNH 2
// -----------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
// ✨ Đổi tên Preview
fun TopicSelectionPreview2() {
    BabiLingTheme {
        val context = LocalContext.current
        // ✨ Gọi màn hình mới
        TopicSelectionScreen2(
            onNavigateBack = {
                Toast.makeText(context, "Quay lại", Toast.LENGTH_SHORT).show()
            },
            onTopicSelected = { topic ->
                Toast.makeText(context, "Đã chọn: ${topic.title}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
