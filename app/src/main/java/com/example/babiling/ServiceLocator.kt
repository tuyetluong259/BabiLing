package com.example.babiling

import android.content.Context
import com.example.babiling.data.local.AppDatabase
import com.example.babiling.data.repository.FlashcardRepository
import com.example.babiling.data.seed.Seeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ServiceLocator hoạt động như một "trung tâm điều phối" duy nhất.
 * Nó đảm bảo rằng Database và Repository chỉ được tạo MỘT LẦN và được tái sử dụng
 * trong suốt vòng đời của ứng dụng.
 */
object ServiceLocator {

    // Sử dụng @Volatile để đảm bảo các thay đổi được ghi vào bộ nhớ chính ngay lập tức,
    // an toàn cho việc truy cập từ nhiều luồng.
    @Volatile
    private var database: AppDatabase? = null
    @Volatile
    private var repository: FlashcardRepository? = null

    /**
     * Hàm chính để lấy Repository. Đây là hàm duy nhất mà các ViewModel sẽ gọi đến.
     * Nó sẽ tự động kiểm tra và khởi tạo Database nếu cần.
     * `synchronized` đảm bảo rằng khối code này chỉ được một luồng thực thi tại một thời điểm,
     * ngăn chặn việc tạo ra nhiều instance của Repository.
     */
    fun provideRepository(context: Context): FlashcardRepository {
        // Nếu repository đã được tạo, trả về ngay lập tức (Double-Checked Locking).
        val currentRepo = repository
        if (currentRepo != null) {
            return currentRepo
        }

        // Nếu chưa, khóa luồng và kiểm tra lại để đảm bảo an toàn.
        synchronized(this) {
            // Kiểm tra lại lần nữa sau khi đã khóa luồng.
            val synchronizedRepo = repository
            if (synchronizedRepo != null) {
                return synchronizedRepo
            }

            // Khởi tạo Database
            val db = getDatabase(context)

            // ✨✨✨ SỬA LỖI QUAN TRỌNG NHẤT TẠI ĐÂY ✨✨✨
            // Khởi tạo Repository với ĐẦY ĐỦ CẢ 3 DAO.
            val newRepo = FlashcardRepository(
                flashcardDao = db.flashcardDao(),
                userProgressDao = db.userProgressDao(),
                topicDao = db.topicDao() // <-- TRUYỀN `topicDao` CÒN THIẾU VÀO ĐÂY
            )
            repository = newRepo

            // Sau khi Repository đã sẵn sàng, tiến hành khởi tạo dữ liệu nền.
            seedDataIfNeeded(context, newRepo)

            return newRepo
        }
    }

    /**
     * Hàm private để khởi tạo Database.
     * Cũng sử dụng Double-Checked Locking để đảm bảo chỉ tạo một instance duy nhất.
     */
    private fun getDatabase(context: Context): AppDatabase {
        val currentDb = database
        if (currentDb != null) {
            return currentDb
        }
        synchronized(this) {
            val synchronizedDb = database
            if (synchronizedDb != null) {
                return synchronizedDb
            }
            val newDb = AppDatabase.getInstance(context) // Gọi hàm getInstance từ AppDatabase
            database = newDb
            return newDb
        }
    }

    /**
     * Hàm private để chạy tiến trình seed data (thêm dữ liệu mẫu) trên một luồng nền (IO).
     * Điều này ngăn chặn việc block luồng chính (UI Thread).
     */
    private fun seedDataIfNeeded(context: Context, repo: FlashcardRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            Seeder.seedIfNeeded(context, repo)
        }
    }
}
