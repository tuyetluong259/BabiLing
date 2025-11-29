package com.example.babiling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // ✨ 1. Import TypeConverters
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.UserProgressEntity

// ✨ 2. Thêm Annotation @TypeConverters để Room biết cách xử lý kiểu Date
@TypeConverters(Converters::class)
@Database(entities = [FlashcardEntity::class, UserProgressEntity::class], version = AppDatabase.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        // ✨ 3. TĂNG VERSION TỪ 1 LÊN 2
        // Mỗi khi thay đổi cấu trúc (thêm/xóa/sửa bảng), bạn phải tăng số này.
        const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "babiling.db"

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // .fallbackToDestructiveMigration() sẽ xóa DB cũ và tạo lại khi tăng version.
                // Điều này tiện lợi trong giai đoạn phát triển.
                .fallbackToDestructiveMigration()
                .build()
    }
}
