package com.example.babiling.ui.screens.settings.actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler // ✨ 1. IMPORT ĐỂ MỞ LINK ✨
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6395EE)
    val textColor = Color(0xFF2D2D2D)

    // ✨ 2. LẤY URI HANDLER TỪ CONTEXT ✨
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            SupportTopBar(
                // Đổi lại title cho đúng
                title = "Hỗ trợ",
                textColor = textColor,
                primaryColor = primaryColor,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    // ✨ 3. THÊM MỤC TÀI LIỆU HƯỚNG DẪN ✨
                    SupportActionItem(
                        text = "Tài liệu hướng dẫn",
                        iconResId = R.drawable.instructions, // TODO: Thay bằng icon của bạn
                        onClick = {
                            // Mở link khi người dùng nhấn vào
                            uriHandler.openUri("https://github.com/tuyetluong259/BabiLing.git") // ✨ THAY LINK CỦA BẠN VÀO ĐÂY ✨
                        }
                    )

                    Divider(color = backgroundColor, thickness = 2.dp)

                    // ✨ THÊM MỤC BÁO CÁO SỰ CỐ ✨
                    SupportActionItem(
                        text = "Báo cáo sự cố",
                        iconResId = R.drawable.support, // TODO: Thay bằng icon của bạn
                        onClick = {
                            // Bạn có thể điều hướng đến một màn hình báo cáo sự cố trong app
                            uriHandler.openUri("https://forms.gle/XeBjmijDFNcT63bU7") // ✨ THAY LINK CỦA BẠN VÀO ĐÂY ✨
                        }
                    )
                }
            }
        }
    }
}

/**
 * Composable cho một mục hành động trong màn hình Hỗ trợ.
 * Gồm icon, văn bản và mũi tên, có thể nhấn vào được.
 */
@Composable
fun SupportActionItem(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    val textColor = Color(0xFF2D2D2D)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontFamily = BalooThambi2Family,
            style = Typography.titleMedium,
            color = Purple40,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.right), // Icon mũi tên phải
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

// TopBar không thay đổi nhiều, chỉ đổi title
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportTopBar(
    title: String,
    textColor: Color,
    primaryColor: Color,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = primaryColor
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Quay lại",
                    colorFilter = ColorFilter.tint(Color(0xFF2D2D2D)),
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SupportScreenPreview() {
    SupportScreen(navController = rememberNavController())
}

