package com.example.babiling.ui.screens.settings.actions

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(navController: NavController) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6395EE)
    val textColor = Color(0xFF2D2D2D)

    // URL để báo cáo sự cố (ví dụ)
    val REPORT_URL = "https://forms.gle/XeBjmijDFNcT63bU7"

    var isDeveloperInfoExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ReportIssueTopBar(
                title = "Báo cáo sự cố",
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ReportIssueItem(
                title = "Báo cáo sự cố đến nhà phát triển",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(REPORT_URL))
                    context.startActivity(intent)
                }
            )

            DeveloperInfoItem(
                isExpanded = isDeveloperInfoExpanded,
                onClick = { isDeveloperInfoExpanded = !isDeveloperInfoExpanded }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueTopBar(
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


@Composable
fun ReportIssueItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = title,
                fontFamily = BalooThambi2Family,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )
        }
    }
}
@Composable
fun DeveloperInfoItem(
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F5F5)
    val textColor = Color(0xFF2D2D2D)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Thông tin nhà phát triển",
                fontFamily = BalooThambi2Family,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {

                Text(
                    text = "Đồ án môn học: Lập trình thiết bị di động",
                    fontFamily = BalooThambi2Family,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6395EE),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                Text(
                    text = "Ứng dụng **BabiLing** là app học tiếng Anh cho trẻ em, được phát triển như một đồ án môn học.",
                    fontFamily = BalooThambi2Family,
                    fontSize = 14.sp,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                DeveloperDetail(label = "Phát triển bởi:", value = "Đội ngũ sinh viên thực hiện đồ án")
                DeveloperDetail(label = "Thành viên 1:", value = "Hoàng Mai Kiều")
                DeveloperDetail(label = "Thành viên 2:", value = "Lương Thị Ánh Tuyết")

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DeveloperDetail(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = BalooThambi2Family,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            fontFamily = BalooThambi2Family,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D2D2D)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportIssueScreenPreview() {
    ReportIssueScreen(navController = rememberNavController())
}