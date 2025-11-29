package com.example.babiling

import android.app.Application
import android.util.Log // 1. THÊM IMPORT NÀY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("BabiLing_Debug", "App.onCreate: Bắt đầu.")

        // Đoạn code coroutine của bạn đã rất tốt, chỉ cần thêm Log vào để theo dõi
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("BabiLing_Debug", "Coroutine: Bắt đầu trên luồng IO.")
            try {
                Log.d("BabiLing_Debug", "Bước 1: Chuẩn bị gọi initDB.")
                ServiceLocator.initDB(applicationContext)
                Log.d("BabiLing_Debug", "Bước 1: initDB HOÀN TẤT.")

                Log.d("BabiLing_Debug", "Bước 2: Chuẩn bị gọi initRepo.")
                ServiceLocator.initRepo()
                Log.d("BabiLing_Debug", "Bước 2: initRepo HOÀN TẤT.")

                Log.d("BabiLing_Debug", "Bước 3: Chuẩn bị gọi seedIfNeeded.")
                ServiceLocator.seedIfNeeded(applicationContext)
                Log.d("BabiLing_Debug", "Bước 3: seedIfNeeded HOÀN TẤT.")

            } catch (e: Exception) {
                // Dòng này cực kỳ quan trọng, nó sẽ bắt bất kỳ lỗi nào xảy ra trong quá trình khởi tạo
                Log.e("BabiLing_Debug", "LỖI TRONG COROUTINE KHỞI TẠO: ", e)
            }
            Log.d("BabiLing_Debug", "Coroutine: Kết thúc.")
        }

        // Dòng log này sẽ chạy gần như ngay lập tức, chứng tỏ luồng chính không bị chặn
        Log.d("BabiLing_Debug", "App.onCreate: Kết thúc.")
    }
}
