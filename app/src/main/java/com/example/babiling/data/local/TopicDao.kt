package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babiling.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) cho bảng 'topics'.
 * Chịu trách nhiệm cho các tác vụ truy vấn liên quan đến Chủ đề (Topic).
 * ✨ PHIÊN BẢN HOÀN THIỆN - Đã thêm hàm getAllTopicsStatic ✨
 */
@Dao
interface TopicDao {

    /**
     * Thêm một danh sách các chủ đề vào database.
     * Nếu có xung đột (cùng id), nó sẽ được thay thế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)

    /**
     * Lấy tất cả các chủ đề từ database.
     * Trả về một Flow để UI có thể tự động cập nhật.
     */
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<TopicEntity>>

    /**
     * Đếm số lượng chủ đề có trong database.
     * Dùng để kiểm tra xem có cần khởi tạo dữ liệu lần đầu không.
     */
    @Query("SELECT COUNT(*) FROM topics")
    suspend fun count(): Int

    // ✨ ================= HÀM CÒN THIẾU ĐƯỢC THÊM VÀO ĐÂY ================= ✨
    /**
     * Lấy tất cả các chủ đề từ database dưới dạng một danh sách tĩnh (List).
     * Dùng cho ProgressViewModel để lấy dữ liệu thô và tự xử lý.
     */
    @Query("SELECT * FROM topics")
    suspend fun getAllTopicsStatic(): List<TopicEntity>
}
