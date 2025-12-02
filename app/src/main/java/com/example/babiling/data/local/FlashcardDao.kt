package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babiling.data.model.FlashcardEntity

/**
 * Data Access Object (DAO) cho bảng 'flashcards'.
 * Chịu trách nhiệm cho các tác vụ truy vấn liên quan đến Flashcard.
 * ✨ PHIÊN BẢN HOÀN THIỆN - Đã thêm hàm getCardCountForTopic ✨
 */
@Dao
interface FlashcardDao {

    /**
     * Lấy tất cả các thẻ flashcard thuộc một chủ đề cụ thể.
     */
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity>

    /**
     * Lấy tất cả các thẻ flashcard có trong database.
     */
    @Query("SELECT * FROM flashcards")
    suspend fun getAllCards(): List<FlashcardEntity>

    /**
     * Chèn một danh sách các thẻ flashcard vào database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FlashcardEntity>)

    /**
     * Đếm tổng số thẻ có trong database.
     */
    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun countAll(): Int


    // ================= BỔ SUNG TRUY VẤN CHO BÀI HỌC =================

    /**
     * Lấy tất cả các flashcard thuộc một chủ đề VÀ một bài học cụ thể.
     */
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId AND lessonNumber = :lessonNumber ORDER BY id ASC")
    suspend fun getFlashcardsByLesson(topicId: String, lessonNumber: Int): List<FlashcardEntity>

    /**
     * Lấy danh sách các số thứ tự bài học (không trùng lặp) có trong một chủ đề.
     */
    @Query("SELECT DISTINCT lessonNumber FROM flashcards WHERE topicId = :topicId ORDER BY lessonNumber ASC")
    suspend fun getLessonNumbersForTopic(topicId: String): List<Int>

    // ================= BỔ SUNG CHO MÀN HÌNH TIẾN ĐỘ =================

    /**
     * Lấy danh sách ID của tất cả các thẻ trong một chủ đề cụ thể.
     */
    @Query("SELECT id FROM flashcards WHERE topicId = :topicId")
    suspend fun getCardIdsByTopic(topicId: String): List<String>

    /**
     * Lấy một danh sách các Flashcard từ một danh sách các ID.
     */
    @Query("SELECT * FROM flashcards WHERE id IN (:ids)")
    suspend fun getFlashcardsByIds(ids: List<String>): List<FlashcardEntity>

    // ✨ ================= HÀM CÒN THIẾU ĐƯỢC THÊM VÀO ĐÂY ================= ✨
    /**
     * Đếm tổng số thẻ của một chủ đề cụ thể.
     * Dùng cho ProgressViewModel để tính toán phần trăm tiến độ.
     */
    @Query("SELECT COUNT(id) FROM flashcards WHERE topicId = :topicId")
    suspend fun getCardCountForTopic(topicId: String): Int
}

