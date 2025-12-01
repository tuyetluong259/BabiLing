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


    // ✨ ================= BỔ SUNG TRUY VẤN CHO BÀI HỌC ================= ✨

    /**
     * Lấy tất cả các flashcard thuộc một chủ đề VÀ một bài học cụ thể.
     * Đây là hàm cốt lõi để màn hình LearnScreen có thể hiển thị đúng các thẻ.
     */
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId AND lessonNumber = :lessonNumber ORDER BY id ASC")
    suspend fun getFlashcardsByLesson(topicId: String, lessonNumber: Int): List<FlashcardEntity>

    /**
     * Lấy danh sách các số thứ tự bài học (không trùng lặp) có trong một chủ đề.
     * Ví dụ: trả về [1, 2, 3, 4] cho chủ đề "animals".
     * Cần thiết để màn hình LessonSelectionScreen hiển thị các nút "Bài 1", "Bài 2",...
     */
    @Query("SELECT DISTINCT lessonNumber FROM flashcards WHERE topicId = :topicId ORDER BY lessonNumber ASC")
    suspend fun getLessonNumbersForTopic(topicId: String): List<Int>

    // ✨ ======================= KẾT THÚC BỔ SUNG ======================= ✨


    // ✨ ================= BỔ SUNG CHO MÀN HÌNH TIẾN ĐỘ ================= ✨

    /**
     * Lấy danh sách ID của tất cả các thẻ trong một chủ đề cụ thể.
     * Hàm này được gọi bởi FlashcardRepository để cung cấp danh sách ID cho UserProgressDao.
     */
    @Query("SELECT id FROM flashcards WHERE topicId = :topicId")
    suspend fun getCardIdsByTopic(topicId: String): List<String>

    // ✨ ======================= KẾT THÚC BỔ SUNG ======================= ✨
}
