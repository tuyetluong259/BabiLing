package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babiling.data.model.UserProgressEntity

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedProgressForUser(userId: String): List<UserProgressEntity>

    // Đánh dấu đã đồng bộ bằng flashcardId thay vì ID tự tăng
    @Query("UPDATE user_progress SET isSynced = 1 WHERE userId = :userId AND flashcardId IN (:ids)")
    suspend fun markAsSynced(userId: String, ids: List<String>)

    /**
     * Lấy tất cả các bản ghi tiến độ của một người dùng, không quan tâm đã đồng bộ hay chưa.
     * Hàm này dùng cho màn hình thống kê tiến độ.
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity>

    // ✨ ================= BỔ SUNG CHO MÀN HÌNH TIẾN ĐỘ ================= ✨

    /**
     * Đếm số bản ghi tiến độ của một user, cho một danh sách các thẻ cho trước,
     * và có masteryLevel > 0.
     * Hàm này thực hiện việc đếm trực tiếp trên CSDL, rất hiệu quả.
     */
    @Query("""
        SELECT COUNT(DISTINCT flashcardId) FROM user_progress
        WHERE userId = :userId
        AND flashcardId IN (:cardIds)
        AND masteryLevel > 0
    """)
    suspend fun getLearnedCardsCountForUserInTopic(userId: String, cardIds: List<String>): Int

    // ✨ ======================= KẾT THÚC BỔ SUNG ======================= ✨
}
