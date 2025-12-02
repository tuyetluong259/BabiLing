package com.example.babiling.ui.screens.settings.account

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family
import com.example.babiling.ui.screens.settings.account.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6395EE)
    val textColor = Color(0xFF2D2D2D)

    // Trạng thái của Switch
    var isNewNotificationEnabled by remember { mutableStateOf(false) }

    // Trạng thái để hiển thị Dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NotificationsTopBar(
                title = "Thông báo",
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                NotificationToggleItem(
                    title = "Nhận thông báo mới",
                    primaryColor = primaryColor,
                    isEnabled = isNewNotificationEnabled,
                    onCheckedChange = { isEnabled ->
                        if (isEnabled) {
                            // Nếu cố gắng BẬT, hiện Dialog
                            showConfirmationDialog = true
                        } else {
                            // Nếu TẮT, cập nhật trạng thái ngay lập tức
                            isNewNotificationEnabled = false
                            // TODO: Xử lý logic tắt thông báo
                        }
                    }
                )
            }
        }
    }

    if (showConfirmationDialog) {
        ConfirmationDialog(
            primaryColor = primaryColor,
            onDismiss = {
                // Hủy (đóng dialog và KHÔNG thay đổi trạng thái Switch)
                showConfirmationDialog = false
            },
            onConfirm = {
                // Xác nhận (cập nhật trạng thái Switch và đóng dialog)
                isNewNotificationEnabled = true
                showConfirmationDialog = false
                // TODO: GỌI HÀM BẬT THÔNG BÁO
            },
            titleText = "Xác nhận",
            bodyText = "Bạn có muốn nhận thông báo mới của BabiLing?"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsTopBar(
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
fun NotificationToggleItem(
    title: String,
    primaryColor: Color,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val textColor = Color(0xFF2D2D2D)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isEnabled) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontFamily = BalooThambi2Family,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(navController = rememberNavController())
}