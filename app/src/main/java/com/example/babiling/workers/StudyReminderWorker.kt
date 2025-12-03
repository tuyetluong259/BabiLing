package com.example.babiling.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.babiling.R // Đảm bảo bạn có icon notification
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class StudyReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Kiểm tra xem người dùng có đang hoạt động không (tùy chọn)
        // Hiện tại, ta chỉ kiểm tra xem user có đăng nhập không.
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Không có người dùng, không cần gửi thông báo
            return Result.success()
        }

        // --- Logic Gửi thông báo ---
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val channelId = "study_reminder_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo Notification Channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nhắc nhở học tập",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo nhắc nhở quay lại học từ BabiLing."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("BabiLing: Đừng quên học nhé! 😉")
            .setContentText("Bạn ơi! Đã 12 tiếng rồi bạn chưa vào học tiếng Anh. Cùng bé học ngay thôi nào!")
            .setSmallIcon(R.drawable.decor3) // Thay bằng icon nhỏ của bạn
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Gửi thông báo
        notificationManager.notify(Random.nextInt(), notification)
    }
}