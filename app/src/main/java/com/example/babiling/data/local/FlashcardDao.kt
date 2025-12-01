package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babiling.data.model.FlashcardEntity

@Dao
interface FlashcardDao {

    /**
     * Lấy tất cả các thẻ flashcard thuộc một chủ đề cụ thể.
     */
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity>

    /**
     * ✨ HÀM MỚI ✨
     * Lấy tất cả các thẻ flashcard có trong database.
     * Cần thiết cho chức năng "Ôn tập tất cả".
     */
    @Query("SELECT * FROM flashcards")
    suspend fun getAllCards(): List<FlashcardEntity>

    /**
     * Chèn một danh sách các thẻ flashcard vào database.
     * Nếu thẻ đã tồn tại, nó sẽ được thay thế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FlashcardEntity>)

    /**
     * Đếm tổng số thẻ có trong database.
     * Hữu ích cho việc kiểm tra xem database có trống không (để seed dữ liệu).
     */
    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun countAll(): Int

    /**
     * ✨ HÀM MỚI ✨
     * Cập nhật tiến độ học của một thẻ flashcard cụ thể.
     *
     * @param flashcardId ID của thẻ cần cập nhật.
     * @param newMasteryLevel Mức độ thành thạo mới.
     * @param newCorrectCountInRow Số lần trả lời đúng liên tiếp mới.
     */
    @Query("""
        UPDATE flashcards 
        SET masteryLevel = :newMasteryLevel, correctCountInRow = :newCorrectCountInRow
        WHERE id = :flashcardId
    """)
    suspend fun updateProgress(flashcardId: String, newMasteryLevel: Int, newCorrectCountInRow: Int)

}
