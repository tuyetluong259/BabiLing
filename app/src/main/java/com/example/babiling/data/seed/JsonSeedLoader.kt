package com.example.babiling.data.seed

import android.content.Context
import com.example.babiling.data.model.FlashcardEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonSeedLoader {

    /**
     * Tải danh sách các FlashcardEntity từ một file JSON trong thư mục assets.
     */
    fun loadListFromAsset(context: Context, assetPath: String): List<FlashcardEntity> {
        // Đọc nội dung file JSON từ đường dẫn được cung cấp
        val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }

        // Định nghĩa kiểu dữ liệu mà Gson sẽ chuyển đổi thành (một danh sách các SeedItem)
        val type = object : TypeToken<List<SeedItem>>() {}.type

        // Dùng Gson để chuyển đổi chuỗi JSON thành một danh sách các đối tượng SeedItem
        val items: List<SeedItem> = Gson().fromJson(json, type)

        // Ánh xạ (map) từng SeedItem thành FlashcardEntity và trả về danh sách kết quả
        return items.map { it.toFlashcardEntity() }
    }

    /**
     * Data class trung gian đại diện cho cấu trúc của một object trong file JSON.
     * Tên các biến trong class này BẮT BUỘC phải khớp chính xác với tên các trường trong file JSON.
     */
    private data class SeedItem(
        // Các biến này sẽ được Gson tự động điền giá trị từ JSON
        val id: String,
        val topicId: String, // ĐÃ SỬA: Phải là "topicId" để khớp với JSON
        val name: String,
        val nameVi: String,
        val imagePath: String,
        val soundPath: String
    ) {
        /**
         * Hàm chuyển đổi từ đối tượng trung gian (SeedItem) thành đối tượng thực thể của database (FlashcardEntity).
         */
        fun toFlashcardEntity() = FlashcardEntity(
            // id của FlashcardEntity phải lấy từ id của SeedItem
            id = this.id, // ĐÃ SỬA: Lấy id từ chính SeedItem, không phải gán cứng

            // topicId của FlashcardEntity lấy từ topicId của SeedItem
            topicId = this.topicId, // ĐÃ SỬA: Lấy topicId từ chính SeedItem

            name = this.name,
            nameVi = this.nameVi,
            imagePath = this.imagePath,
            soundPath = this.soundPath
        )
    }
}
