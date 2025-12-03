package com.example.babiling.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.TopicEntity
import com.example.babiling.data.model.UserProgressEntity

@TypeConverters(Converters::class)
@Database(
    entities = [FlashcardEntity::class, UserProgressEntity::class, TopicEntity::class],
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun topicDao(): TopicDao

    companion object {
        const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "babiling.db"

        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user_progress ADD COLUMN topicId TEXT NOT NULL DEFAULT 'unknown'")
                database.execSQL("""
                    UPDATE user_progress 
                    SET topicId = (SELECT topicId FROM flashcards WHERE flashcards.id = user_progress.flashcardId)
                """)
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `topics` (
                        `id` TEXT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `description` TEXT NOT NULL, 
                        `lessonCount` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """)
            }
        }

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                // ✨ ================= THÊM DÒNG NÀY VÀO ĐÂY ================= ✨
                // Dòng này sẽ là "cứu cánh" khi không có migration phù hợp.
                // Nó sẽ xóa DB cũ và tạo lại, rất hữu ích trong quá trình phát triển.
                .fallbackToDestructiveMigration()
                // ✨ ========================================================== ✨
                .build()
    }
}
