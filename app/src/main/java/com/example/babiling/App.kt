package com.example.babiling

import android.app.Application
import android.util.Log

/**
 * điểm khởi đầu của ứng dụng.
 * Được sử dụng để thực hiện các tác vụ khởi tạo toàn cục.
 */class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BabiLing_App", "Application.onCreate: Bắt đầu khởi tạo toàn cục.")

        // tự động khởi tạo Database, Repository và chạy seed data
        // trên một luồng nền mà không làm treo giao diện người dùng.
        try {
            ServiceLocator.provideRepository(this)
            Log.d("BabiLing_App", "Application.onCreate: Yêu cầu khởi tạo Repository thành công.")
        } catch (e: Exception) {
            // Ghi lại bất kỳ lỗi nghiêm trọng nào xảy ra trong quá trình khởi tạo đồng bộ
            Log.e("BabiLing_App", "LỖI NGHIÊM TRỌNG KHI KHỞI TẠO SERVICE LOCATOR: ", e)
        }

        Log.d("BabiLing_App", "Application.onCreate: Kết thúc.")
    }
}
