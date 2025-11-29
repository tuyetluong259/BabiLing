package com.example.babiling

import android.content.Context
import android.util.Log // 1. THÊM IMPORT NÀY
import androidx.room.Room
import com.example.babiling.data.local.AppDatabase
import com.example.babiling.data.repository.FlashcardRepository
import com.example.babiling.data.seed.Seeder

object ServiceLocator {
    private var db: AppDatabase? = null
    private var repo: FlashcardRepository? = null

    // Hàm này sẽ được gọi trên luồng nền
    fun initDB(context: Context) {
        if (db == null) {
            // Log trước khi thực hiện tác vụ nặng
            Log.d("BabiLing_Debug", "ServiceLocator: Chuẩn bị build database...")
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "babiling.db"
            ).fallbackToDestructiveMigration().build()
            // Log ngay sau khi tác vụ nặng hoàn thành
            Log.d("BabiLing_Debug", "ServiceLocator: Build database XONG.")
        } else {
            Log.d("BabiLing_Debug", "ServiceLocator: Database đã được khởi tạo trước đó.")
        }
    }

    // Hàm này khởi tạo repo sau khi DB đã sẵn sàng
    fun initRepo() {
        if (repo == null) {
            // Dùng try-catch để phòng trường hợp db vẫn còn là null (lỗi logic đâu đó)
            try {
                repo = FlashcardRepository(db!!.flashcardDao(), db!!.userProgressDao())
                Log.d("BabiLing_Debug", "ServiceLocator: Khởi tạo repository XONG.")
            } catch (e: NullPointerException) {
                Log.e("BabiLing_Debug", "LỖI: Cố gắng khởi tạo Repo trong khi DB vẫn là null!", e)
            }
        } else {
            Log.d("BabiLing_Debug", "ServiceLocator: Repository đã được khởi tạo trước đó.")
        }
    }

    // Hàm này phải được gọi sau khi initRepo đã chạy
    fun provideRepository(): FlashcardRepository {
        if (repo == null) {
            // Đây là một tình huống lỗi nghiêm trọng, cần phải ghi log lại
            Log.e("BabiLing_Debug", "LỖI NGHIÊM TRỌNG: provideRepository() được gọi trước khi initRepo() hoàn tất!")
            // Dù sẽ crash, nhưng việc khởi tạo tạm thời có thể giúp thấy lỗi rõ hơn
            initRepo()
        }
        return repo!!
    }

    suspend fun seedIfNeeded(context: Context) {
        // Hàm này không cần Log bên trong, vì chúng ta sẽ thêm Log vào Seeder.kt
        Seeder.seedIfNeeded(context, provideRepository())
    }
}
