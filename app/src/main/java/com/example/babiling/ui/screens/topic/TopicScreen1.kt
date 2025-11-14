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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
// 1. DATA VÀ DỮ LIỆU MẪU (ĐÃ SỬA LỖI NGHIÊM TRỌNG)
// -----------------------------------------------------------------------------

data class Topic(
    val title: String,
    @DrawableRes val imageResId: Int,
    val textColor: Color
)

fun getTopics(): List<Topic> {
    return listOf(
        // ✨ FIX: Thêm giá trị cho backgroundColor
        Topic("Greetings", R.drawable.greetings, textColor = AccentRed),
        Topic("Body", R.drawable.body, textColor = IndigoBlue),
        Topic("Colors", R.drawable.color, textColor = WarmOrange),
        Topic("Fruit", R.drawable.fruit, textColor = ForestGreen),
        Topic("Animals", R.drawable.animals, textColor = SkyBlue),
        Topic("Toys", R.drawable.toys, textColor = AlertRed)
    )
}

// -----------------------------------------------------------------------------
// 2. COMPOSABLE CHO THẺ CHỦ ĐỀ (ĐÃ SỬA LỖI CÚ PHÁP VÀ SHADOW)
// -----------------------------------------------------------------------------

@Composable
fun TopicCard(topic: Topic, onClick: (Topic) -> Unit) {
    Card(
        modifier = Modifier
            .size(170.dp)
            .shadow( // Thêm shadow để có hiệu ứng bóng đậm như mong muốn
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                spotColor = Color(0xCC000000),
                ambientColor = Color(0xCC000000)
            )
            .clickable { onClick(topic) },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Nền của Card
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topic.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
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
// 3. COMPOSABLE CHO MÀN HÌNH CHÍNH (ĐÃ SỬA LỖI HIỂN THỊ SHADOW)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicSelectionScreen(
    onNavigateBack: () -> Unit,
    onTopicSelected: (Topic) -> Unit
) {
    val topics = getTopics()
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

    // ✨ FIX: Sửa lại cấu trúc để Shadow hiển thị
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Để trống */ },
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
                    containerColor = PrimaryYellow // Giữ màu nền cho TopBar
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
        // Đặt nền của Scaffold thành trong suốt để không che mất bóng
        containerColor = Color.Transparent
    ) { paddingValues ->
        // Dùng Box để chứa nền vàng và nội dung
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryYellow) // Nền vàng chính được đặt ở đây
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(topics) { topic ->
                        TopicCard(topic = topic) { selectedTopic ->
                            onTopicSelected(selectedTopic)
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4. PREVIEW (KHÔNG THAY ĐỔI)
// -----------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopicSelectionPreview() {
    BabiLingTheme {
        val context = LocalContext.current
        TopicSelectionScreen(
            onNavigateBack = {
                Toast.makeText(context, "Quay lại", Toast.LENGTH_SHORT).show()
            },
            onTopicSelected = { topic ->
                Toast.makeText(context, "Đã chọn: ${topic.title}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
