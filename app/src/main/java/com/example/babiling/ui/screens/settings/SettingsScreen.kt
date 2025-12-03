package com.example.babiling.ui.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext // ✨ IMPORT CẦN THIẾT ✨
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.Screen
import com.example.babiling.ui.theme.BalooThambi2Family

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6395EE)
    val textColor = Color(0xFF2D2D2D)

    // ✨ LẤY CONTEXT ĐỂ SỬ DỤNG TOAST ✨
    val context = LocalContext.current

    // Định nghĩa hàm Toast
    val showDevelopingToast: () -> Unit = {
        Toast.makeText(context, "Tính năng đang được phát triển!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        fontFamily = BalooThambi2Family,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = primaryColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Quay lại",
                            colorFilter = ColorFilter.tint(textColor),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tài khoản",
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Chỉnh sửa hồ sơ",
                        onClick = { navController.navigate(Screen.EditProfile.route) }
                    )
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Shield,
                        title = "Bảo vệ",
                        onClick = { navController.navigate(Screen.Security.route) }
                    )
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Thông báo",
                        onClick = { navController.navigate(Screen.Notifications.route) }
                    )
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Đổi mật khẩu",
                        onClick = showDevelopingToast // ✨ SỬ DỤNG TOAST MỚI ✨
                    )
                }
            }

            // Actions Section
            Text(
                text = "Actions",
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.Flag,
                        title = "Báo cáo sự cố",
                        onClick = { navController.navigate(Screen.Support.route) }
                    )
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.Default.PersonAdd,
                        title = "Thêm tài khoản",
                        onClick = showDevelopingToast // ✨ SỬ DỤNG TOAST MỚI ✨
                    )
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Đăng xuất",
                        onClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ... (Hàm SettingsItem và SettingsScreenPreview giữ nguyên)
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF6395EE),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            fontFamily = BalooThambi2Family,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D2D2D),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}