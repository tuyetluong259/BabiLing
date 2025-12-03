package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert // Sử dụng @Upsert thay cho @Insert(onConflict) để rõ ràng hơn
import com.example.babiling.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho việc truy cập dữ liệu tiến độ người dùng.
 * ✨ PHIÊN BẢN HOÀN THIỆN - Đã sửa lỗi tên cột và tối ưu ✨
 */
@Dao
interface UserProgressDao {

    /**
     * Chèn hoặc cập nhật một danh sách các bản ghi tiến độ.
     * Sử dụng @Upsert cho mục đích này là chuẩn nhất.
     */
    @Upsert
    suspend fun upsertAll(progressList: List<UserProgressEntity>)

    /**
     * Chèn hoặc cập nhật một bản ghi tiến độ duy nhất.
     */
    @Upsert
    suspend fun upsert(progress: UserProgressEntity)

    /**
     * Lấy tất cả các bản ghi tiến độ chưa được đồng bộ của người dùng.
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId AND synced = 0")
    suspend fun getUnsyncedProgressForUser(userId: String): List<UserProgressEntity>

    /**
     * Đánh dấu các bản ghi đã được đồng bộ thành công.
     */
    @Query("UPDATE user_progress SET synced = 1 WHERE userId = :userId AND flashcardId IN (:ids)")
    suspend fun markAsSynced(userId: String, ids: List<String>)

    /**
     * Lấy TẤT CẢ các bản ghi tiến độ của một người dùng (hàm tĩnh).
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity>

    // ✅ HÀM FLOW MỚI CHO ProgressViewModel
    /**
     * Lấy TẤT CẢ các bản ghi tiến độ của một người dùng dưới dạng Flow.
     * Hàm này được dùng trong ProgressViewModel để tự động cập nhật UI khi có thay đổi trong Room.
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getAllProgressFlowForUser(userId: String): Flow<List<UserProgressEntity>>

    /**
     * Lấy một Flow chứa danh sách các số của bài học đã hoàn thành cho một user và một topic.
     */
    @Query("""
        SELECT DISTINCT T1.lessonNumber
        FROM flashcards AS T1
        INNER JOIN user_progress AS T2 ON T1.id = T2.flashcardId
        WHERE T2.userId = :userId AND T1.id IN (:cardIdsInTopic) AND T2.masteryLevel > 0
    """)
    fun getCompletedLessonsFlowForUser(userId: String, cardIdsInTopic: List<String>): Flow<List<Int>>
}