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

    // ✨ HÀM MỚI BẠN CẦN THÊM VÀO ✨
    /**
     * Lấy tất cả các bản ghi tiến độ của một người dùng, không quan tâm đã đồng bộ hay chưa.
     * Hàm này dùng cho màn hình thống kê tiến độ.
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity>
}
