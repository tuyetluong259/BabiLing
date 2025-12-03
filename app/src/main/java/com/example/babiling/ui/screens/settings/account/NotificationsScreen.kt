package com.example.babiling.ui.screens.settings.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy // ✨ WORKMANAGER IMPORT ✨
import androidx.work.PeriodicWorkRequestBuilder // ✨ WORKMANAGER IMPORT ✨
import androidx.work.WorkManager // ✨ WORKMANAGER IMPORT ✨
import com.example.babiling.R
import com.example.babiling.ui.theme.BalooThambi2Family
import com.example.babiling.workers.StudyReminderWorker // ✨ IMPORT WORKER CỦA BẠN ✨
import java.util.concurrent.TimeUnit

// Định nghĩa khóa cho SharedPreferences (để lưu trạng thái)
private const val PREFS_NAME = "BabiLingPrefs"
private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
private const val WORK_TAG_STUDY_REMINDER = "StudyReminderWorkerTag"

// Lấy SharedPreferences
private fun getPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF5F5F5)
    val cardColor = Color.White
    val primaryColor = Color(0xFF6395EE)
    val textColor = Color(0xFF2D2D2D)
    val context = LocalContext.current
    val prefs = remember { getPrefs(context) }

    // Trạng thái của Switch (Lấy từ SharedPreferences)
    var isNewNotificationEnabled by remember {
        mutableStateOf(prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false))
    }

    // Trạng thái để hiển thị Dialog
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Logic xử lý khi người dùng thay đổi trạng thái
    val handleToggle: (Boolean) -> Unit = { isEnabled ->
        if (isEnabled) {
            // Nếu cố gắng BẬT, hiện Dialog xác nhận
            showConfirmationDialog = true
        } else {
            // Nếu TẮT, cập nhật trạng thái ngay lập tức và hủy công việc
            isNewNotificationEnabled = false
            prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, false).apply()
            cancelStudyReminder(context)
            Toast.makeText(context, "Đã tắt thông báo nhắc nhở.", Toast.LENGTH_SHORT).show()
        }
    }

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
                    title = "Nhận thông báo nhắc nhở",
                    primaryColor = primaryColor,
                    isEnabled = isNewNotificationEnabled,
                    onCheckedChange = handleToggle
                )
            }
        }
    }

    // Dialog Xác nhận BẬT
    if (showConfirmationDialog) {
        ConfirmationDialog(
            primaryColor = primaryColor,
            onDismiss = {
                // Hủy (đóng dialog và KHÔNG thay đổi trạng thái Switch)
                showConfirmationDialog = false
            },
            onConfirm = {
                // Xác nhận (cập nhật trạng thái Switch và lên lịch)
                isNewNotificationEnabled = true
                prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, true).apply()
                scheduleStudyReminder(context)
                showConfirmationDialog = false
                Toast.makeText(context, "Thông báo nhắc nhở đã được bật.", Toast.LENGTH_SHORT).show()
            },
            titleText = "Xác nhận",
            bodyText = "Bạn có muốn nhận thông báo mới của BabiLing sau 12 tiếng không hoạt động?"
        )
    }
}

// -------------------------------------------------------------
// MARK: WorkManager Logic
// -------------------------------------------------------------

fun scheduleStudyReminder(context: Context) {
    Log.d("NotificationsScreen", "Đang lên lịch thông báo nhắc nhở (12 giờ)...")

    // Thiết lập chu kỳ lặp lại là 12 tiếng
    val workRequest = PeriodicWorkRequestBuilder<StudyReminderWorker>(
        repeatInterval = 12,
        repeatIntervalTimeUnit = TimeUnit.HOURS
    )
        // Thông báo đầu tiên sau 12 giờ
        .setInitialDelay(12, TimeUnit.HOURS)
        .build()

    // Đặt lịch công việc duy nhất. KEEP: Nếu đã có, giữ nguyên công việc cũ.
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_TAG_STUDY_REMINDER,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}

fun cancelStudyReminder(context: Context) {
    Log.d("NotificationsScreen", "Hủy bỏ lịch thông báo nhắc nhở.")
    WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG_STUDY_REMINDER)
}


// -------------------------------------------------------------
// MARK: Các Composable Phụ
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsTopBar(
    title: String,
    textColor: Color,
    primaryColor: Color,
    onBackClick: () -> Unit
) {
    // ... (Giữ nguyên code TopBar) ...
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

// ⚠️ Bạn phải đảm bảo Composable ConfirmationDialog này được định nghĩa ở một file khác
// Ví dụ: ConfirmationDialog.kt hoặc trong cùng file settings/account.
// Nếu nó chưa được định nghĩa, bạn sẽ gặp lỗi Unresolved reference.

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(navController = rememberNavController())
}