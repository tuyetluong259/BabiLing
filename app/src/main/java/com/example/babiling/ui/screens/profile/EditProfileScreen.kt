package com.example.babiling.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family

private val BackgroundColor = Color(0xFFB1E8C4)
private val HeaderColor = Color(0xFF717086)
private val DeleteColor = Color(0xFFE57373)
private val SaveButtonTextColor = Color(0xFFFF6B6B)
private val PlaceholderIconColor = Color(0xFFE57373)

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var accountName by remember { mutableStateOf("User 123") }
    var username by remember { mutableStateOf("user123@") }

    // Trạng thái cho Dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) } 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top Bar với nút back
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    tint = HeaderColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(80.dp),
                        tint = PlaceholderIconColor
                    )
                }

                // Edit icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HeaderColor)
                        .align(Alignment.BottomEnd)
                        .clickable { /* Handle change avatar */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                        contentDescription = "Edit avatar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Username
            Text(
                text = "User 123",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BalooThambi2Family,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // User ID
            Text(
                text = "@user1234567",
                fontSize = 14.sp,
                fontFamily = BalooThambi2Family,
                color = Color(0xFF5A7C4A),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tên tài khoản
            EditableProfileField(
                label = "Tên tài khoản: User 123",
                value = accountName,
                onValueChange = { accountName = it },
                fontFamily = BalooThambi2Family,
                labelColor = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tên đăng nhập
            EditableProfileField(
                label = "Tên đăng nhập: user123@",
                value = username,
                onValueChange = { username = it },
                fontFamily = BalooThambi2Family,
                labelColor = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Xóa tài khoản button - CẬP NHẬT ONCLICK
            Button(
                onClick = { showDeleteDialog = true }, // KÍCH HOẠT DIALOG XÓA
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Xóa tài khoản",
                    fontSize = 16.sp,
                    fontFamily = BalooThambi2Family,
                    fontWeight = FontWeight.SemiBold,
                    color = DeleteColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            Spacer(modifier = Modifier.weight(1f))

            // Chỉnh sửa button (Lưu)
            Button(
                onClick = { showSaveDialog = true }, // ✅ KÍCH HOẠT DIALOG LƯU
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Chỉnh sửa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = BalooThambi2Family,
                    color = SaveButtonTextColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }


    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Xác nhận xóa",
            body = "Bạn có muốn xóa tài khoản không?",
            confirmText = "Xóa",
            dismissText = "Hủy",
            confirmColor = DeleteColor,
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount() // Thực hiện hành động xóa
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    // ✅ THÊM LOGIC CHO DIALOG LƯU/CHỈNH SỬA
    if (showSaveDialog) {
        ConfirmationDialog(
            title = "Xác nhận chỉnh sửa",
            body = "Xác nhận chỉnh sửa",
            confirmText = "Lưu",
            dismissText = "Hủy",
            confirmColor = SaveButtonTextColor,
            onConfirm = {
                onSaveClick() // Thực hiện hành động lưu
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}


@Composable
private fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    labelColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = "Edit",
                tint = Color(0xFF888888),
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Handle edit */ }
            )
        }
    }
}
@Composable
fun ConfirmationDialog(
    title: String,
    body: String,
    confirmText: String,
    dismissText: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = BalooThambi2Family,
                fontWeight = FontWeight.Bold,
                color = HeaderColor
            )
        },
        text = {
            Text(
                text = body,
                fontFamily = BalooThambi2Family,
                color = HeaderColor
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmText,
                    fontFamily = BalooThambi2Family,
                    fontWeight = FontWeight.Bold,
                    color = confirmColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    dismissText,
                    fontFamily = BalooThambi2Family,
                    color = Color.Gray
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}