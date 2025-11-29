package com.example.babiling.data.seed

import android.content.Context
import android.util.Log
import com.example.babiling.data.repository.FlashcardRepository
object Seeder {

    private val topicFiles = listOf(
        "flashcards/greetings.json",
        "flashcards/body.json",
        "flashcards/colors.json",
        "flashcards/fruit.json",
        "flashcards/animals.json",
        "flashcards/toys.json"
        // ... các file khác
    )

    suspend fun seedIfNeeded(context: Context, repo: FlashcardRepository) {
        // Log khi bắt đầu vào hàm
        Log.d("BabiLing_Debug", "Seeder: Bắt đầu kiểm tra dữ liệu.")

        try {
            val cnt = repo.countFlashcards()
            // Log số lượng thẻ đếm được
            Log.d("BabiLing_Debug", "Seeder: Đếm được $cnt thẻ trong DB.")

            if (cnt > 0) {
                // Log nếu dữ liệu đã có và không cần làm gì thêm
                Log.d("BabiLing_Debug", "Seeder: Dữ liệu đã tồn tại, không cần gieo mầm.")
                return // Kết thúc sớm
            }

            // Log khi bắt đầu quá trình đọc file
            Log.d("BabiLing_Debug", "Seeder: Database trống, bắt đầu đọc file JSON.")
            val all = mutableListOf<com.example.babiling.data.model.FlashcardEntity>()
            for (asset in topicFiles) {
                try {
                    Log.d("BabiLing_Debug", "Seeder: Đang đọc file '$asset'...")
                    val list = JsonSeedLoader.loadListFromAsset(context, asset)
                    all += list
                    Log.d("BabiLing_Debug", "Seeder: Đọc thành công file '$asset', tìm thấy ${list.size} thẻ.")
                } catch (e: Exception) {
                    // Log lỗi nếu một file cụ thể bị lỗi (sai tên, sai định dạng JSON...)
                    Log.e("BabiLing_Debug", "Seeder: LỖI khi đọc file '$asset'", e)
                }
            }

            if (all.isNotEmpty()) {
                Log.d("BabiLing_Debug", "Seeder: Chuẩn bị chèn ${all.size} thẻ vào database...")
                repo.insertAll(all)
                // Log sau khi chèn thành công
                Log.d("BabiLing_Debug", "Seeder: Chèn dữ liệu HOÀN TẤT.")
            } else {
                // Log nếu không đọc được thẻ nào từ tất cả các file
                Log.w("BabiLing_Debug", "Seeder: Không tìm thấy thẻ nào để chèn sau khi đọc tất cả các file.")
            }

        } catch (e: Exception) {
            // Bắt tất cả các lỗi khác có thể xảy ra trong quá trình (ví dụ: lỗi từ DAO)
            Log.e("BabiLing_Debug", "Seeder: LỖI KHÔNG MONG ĐỢI trong quá trình gieo mầm!", e)
        }

        Log.d("BabiLing_Debug", "Seeder: Kết thúc quá trình.")
    }
}
