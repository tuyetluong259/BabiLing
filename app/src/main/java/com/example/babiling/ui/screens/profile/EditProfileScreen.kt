package com.example.babiling.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.babiling.R
import com.example.babiling.ui.screens.auth.AuthViewModel
import com.example.babiling.ui.theme.BalooThambi2Family

private val BackgroundColor = Color(0xFFB1E8C4)
private val HeaderColor = Color(0xFF717086)
private val DeleteColor = Color(0xFFE57373)
private val SaveButtonTextColor = Color(0xFFFF6B6B)
private val PlaceholderIconColor = Color(0xFFE57373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    // ✅ SỬA ĐỔI: HÀM NÀY SẼ BÁO TOAST
    onDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // 1. Khởi tạo giá trị từ currentUser
    var accountName by remember(currentUser) { mutableStateOf(currentUser?.displayName ?: "User 123") }
    var username by remember(currentUser) { mutableStateOf(currentUser?.email?.substringBefore('@') ?: "user123") }

    val photoUrl = currentUser?.photoUrl

    // Trạng thái cho Dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    // ✅ LẮNG NGHE MESSAGES VÀ XỬ LÝ TOAST/ĐIỀU HƯỚNG
    LaunchedEffect(uiState) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
            onSaveClick() // Điều hướng về ProfileScreen sau khi lưu thành công
        }
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            authViewModel.clearMessages()
        }
    }

    // ✅ ĐỊNH NGHĨA HÀM TOAST CHO TÍNH NĂNG CHƯA PHÁT TRIỂN
    val showDevelopingToast: () -> Unit = {
        Toast.makeText(context, "Tính năng đang được phát triển!", Toast.LENGTH_SHORT).show()
    }


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
                // VÙNG HIỂN THỊ AVATAR
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUrl),
                            contentDescription = "User Avatar",
                            modifier = Modifier.matchParentSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Avatar Placeholder",
                            modifier = Modifier.size(80.dp),
                            tint = PlaceholderIconColor
                        )
                    }
                }

                // Edit icon (KÍCH HOẠT CHỌN ẢNH)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HeaderColor)
                        .align(Alignment.BottomEnd)
                        .clickable { showDevelopingToast() /* Tạm thời báo Toast */ },
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

            // Tên hiển thị hiện tại (DISPLAY NAME)
            Text(
                text = currentUser?.displayName ?: "User 123",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = BalooThambi2Family,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Email/UserID hiện tại (Static Display)
            Text(
                text = currentUser?.email ?: "@user1234567",
                fontSize = 14.sp,
                fontFamily = BalooThambi2Family,
                color = Color(0xFF5A7C4A),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tên hiển thị (Editable)
            EditableProfileField(
                label = "Tên hiển thị",
                value = accountName,
                onValueChange = { accountName = it },
                fontFamily = BalooThambi2Family,
                labelColor = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tên đăng nhập (Editable)
            EditableProfileField(
                label = "Tên đăng nhập",
                value = username,
                onValueChange = { username = it },
                fontFamily = BalooThambi2Family,
                labelColor = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Xóa tài khoản button
            Button(
                onClick = { showDeleteDialog = true },
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
                onClick = { showSaveDialog = true },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = SaveButtonTextColor, modifier = Modifier.size(24.dp))
                } else {
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
                showDevelopingToast() // ✅ BÁO TOAST THAY VÌ XÓA
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showSaveDialog) {
        ConfirmationDialog(
            title = "Xác nhận chỉnh sửa",
            body = "Xác nhận lưu các thay đổi?",
            confirmText = "Lưu",
            dismissText = "Hủy",
            confirmColor = SaveButtonTextColor,
            onConfirm = {
                authViewModel.saveProfileChanges(
                    newUsername = username,
                    newAccountName = accountName
                )
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    labelColor: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                label,
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_edit),
                contentDescription = "Edit",
                tint = Color(0xFF888888),
                modifier = Modifier.size(24.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color(0xFF888888),
            unfocusedIndicatorColor = Color.LightGray,
            cursorColor = Color(0xFFE57373)
        ),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            fontFamily = fontFamily,
            color = Color(0xFF2D2D2D),
            fontWeight = FontWeight.SemiBold
        )
    )
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