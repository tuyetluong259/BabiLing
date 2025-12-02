package com.example.babiling.data.seed

import android.content.Context
import android.util.Log
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.TopicEntity
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
        Log.d("BabiLing_Debug", "Seeder: Bắt đầu kiểm tra dữ liệu.")

        try {
            // ✨ BƯỚC 1: SỬA LỖI KIỂM TRA - Kiểm tra số lượng Chủ đề thay vì Thẻ ✨
            val topicCount = repo.countTopics()
            Log.d("BabiLing_Debug", "Seeder: Đếm được $topicCount chủ đề trong DB.")

            if (topicCount > 0) {
                // Dữ liệu Chủ đề đã tồn tại, không cần gieo mầm
                Log.d("BabiLing_Debug", "Seeder: Dữ liệu Chủ đề đã tồn tại, không cần gieo mầm.")
                return // Kết thúc sớm nếu Chủ đề đã có
            }

            // Database trống Chủ đề, bắt đầu đọc và chèn
            Log.d("BabiLing_Debug", "Seeder: Database trống, bắt đầu đọc file JSON.")
            val allFlashcards = mutableListOf<FlashcardEntity>()

            // ... (Vòng lặp đọc JSON giữ nguyên)
            for (asset in topicFiles) {
                try {
                    Log.d("BabiLing_Debug", "Seeder: Đang đọc file '$asset'...")
                    val list = JsonSeedLoader.loadListFromAsset(context, asset)
                    allFlashcards += list
                    Log.d("BabiLing_Debug", "Seeder: Đọc thành công file '$asset', tìm thấy ${list.size} thẻ.")
                } catch (e: Exception) {
                    Log.e("BabiLing_Debug", "Seeder: LỖI khi đọc file '$asset'", e)
                }
            }

            if (allFlashcards.isNotEmpty()) {
                // ✨ BƯỚC 2: TẠO VÀ CHÈN CÁC ENTITY TOPIC TRƯỚC ✨
                val topicsToInsert = createTopicsFromFlashcards(allFlashcards)

                Log.d("BabiLing_Debug", "Seeder: Chuẩn bị chèn ${topicsToInsert.size} chủ đề...")
                repo.insertAllTopics(topicsToInsert)
                Log.d("BabiLing_Debug", "Seeder: Chèn Chủ đề HOÀN TẤT.")

                // BƯỚC 3: CHÈN FLASHCARD SAU
                Log.d("BabiLing_Debug", "Seeder: Chuẩn bị chèn ${allFlashcards.size} thẻ...")
                repo.insertAllFlashcards(allFlashcards)
                Log.d("BabiLing_Debug", "Seeder: Chèn dữ liệu Thẻ HOÀN TẤT.")
            } else {
                Log.w("BabiLing_Debug", "Seeder: Không tìm thấy thẻ nào để chèn sau khi đọc tất cả các file.")
            }

        } catch (e: Exception) {
            Log.e("BabiLing_Debug", "Seeder: LỖI KHÔNG MONG ĐỢI trong quá trình gieo mầm!", e)
        }

        Log.d("BabiLing_Debug", "Seeder: Kết thúc quá trình.")
    }

    /**
     * Hàm helper để tạo TopicEntity từ danh sách FlashcardEntity.
     */
    private fun createTopicsFromFlashcards(flashcards: List<FlashcardEntity>): List<TopicEntity> {
        return flashcards
            .groupBy { it.topicId } // Nhóm thẻ theo ID Chủ đề
            .map { (topicId, cardsInTopic) ->
                // Tính lessonCount: Đếm số lượng lessonNumber khác nhau trong nhóm này
                val distinctLessons = cardsInTopic.map { it.lessonNumber }.distinct().size

                // Tên và mô tả là dữ liệu tĩnh, ta tạm thời suy diễn từ ID.
                TopicEntity(
                    id = topicId,
                    name = topicId.replaceFirstChar { it.uppercase() }, // Ví dụ: "greetings" -> "Greetings"
                    description = "Chủ đề học từ vựng về ${topicId.replaceFirstChar { it.uppercase() }}",
                    lessonCount = distinctLessons
                )
            }
    }
}