package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
// import androidx.room.Transaction // ✨ BỎ IMPORT NÀY ✨
// import com.example.babiling.data.model.TopicWithProgress // ✨ BỎ IMPORT NÀY ✨
import com.example.babiling.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO cho việc truy cập dữ liệu tiến độ người dùng.
 * ✨ PHIÊN BẢN HOÀN THIỆN - Đã đơn giản hóa ✨
 */
@Dao
interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(progressList: List<UserProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedProgressForUser(userId: String): List<UserProgressEntity>

    @Query("UPDATE user_progress SET isSynced = 1 WHERE userId = :userId AND flashcardId IN (:ids)")
    suspend fun markAsSynced(userId: String, ids: List<String>)

    /**
     * Lấy TẤT CẢ các bản ghi tiến độ của một người dùng.
     * ViewModel sẽ dùng hàm này để lấy dữ liệu thô và tự xử lý.
     */
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity>

    /**
     * Lấy một Flow chứa danh sách các số của bài học đã hoàn thành cho một user và một topic.
     * Dùng cho màn hình LessonSelectionScreen.
     */
    @Query("""
        SELECT DISTINCT T1.lessonNumber
        FROM flashcards AS T1
        INNER JOIN user_progress AS T2 ON T1.id = T2.flashcardId
        WHERE T2.userId = :userId AND T1.id IN (:cardIdsInTopic) AND T2.masteryLevel > 0
    """)
    fun getCompletedLessonsFlowForUser(userId: String, cardIdsInTopic: List<String>): Flow<List<Int>>

    // ✨✨✨ XÓA BỎ HOÀN TOÀN CÂU TRUY VẤN PHỨC TẠP `getAllTopicsWithProgress` ✨✨✨
    // Chúng ta không cần nó nữa vì ViewModel sẽ tự xử lý logic này.
}
