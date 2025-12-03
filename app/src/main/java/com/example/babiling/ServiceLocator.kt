package com.example.babiling

import android.content.Context
import com.example.babiling.data.local.AppDatabase
import com.example.babiling.data.repository.AuthRepository
import com.example.babiling.data.repository.FlashcardRepository
import com.example.babiling.data.seed.Seeder
// ✨ BƯỚC 1: IMPORT FirebaseAuth (Đã có sẵn) ✨
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ServiceLocator hoạt động như một "trung tâm điều phối" duy nhất.
 */
object ServiceLocator {

    @Volatile
    private var database: AppDatabase? = null
    @Volatile
    private var flashcardRepository: FlashcardRepository? = null
    @Volatile
    private var authRepository: AuthRepository? = null

    // Hàm này đã đúng, không cần sửa
    fun provideRepository(context: Context): FlashcardRepository {
        val currentRepo = flashcardRepository
        if (currentRepo != null) {
            return currentRepo
        }

        synchronized(this) {
            val synchronizedRepo = flashcardRepository
            if (synchronizedRepo != null) {
                return synchronizedRepo
            }

            val db = getDatabase(context)

            val newRepo = FlashcardRepository(
                flashcardDao = db.flashcardDao(),
                userProgressDao = db.userProgressDao(),
                topicDao = db.topicDao()
            )
            flashcardRepository = newRepo

            seedDataIfNeeded(context, newRepo)

            return newRepo
        }
    }

    // ✅ ĐÃ SỬA: Hàm này nhận FirebaseAuth và Context, sau đó truyền vào AuthRepository.
    fun provideAuthRepository(context: Context): AuthRepository {
        // Kiểm tra xem đã có instance chưa, nếu có thì trả về
        val currentRepo = authRepository
        if (currentRepo != null) {
            return currentRepo
        }

        // Đồng bộ hóa để tránh tạo nhiều instance trên các thread khác nhau
        synchronized(this) {
            val synchronizedRepo = authRepository
            if (synchronizedRepo != null) {
                return synchronizedRepo
            }

            // Lấy một instance của FirebaseAuth
            val firebaseAuth = FirebaseAuth.getInstance()

            // Khởi tạo AuthRepository với cả FirebaseAuth và Context
            val newRepo = AuthRepository(
                firebaseAuth = firebaseAuth, // Truyền FirebaseAuth
                context = context.applicationContext // Dùng applicationContext để tránh memory leak
            )
            authRepository = newRepo
            return newRepo
        }
    }


    // Hàm này đã đúng, không cần sửa
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

            val newDb = AppDatabase.getInstance(context)
            database = newDb
            return newDb
        }
    }

    // Hàm này đã đúng, không cần sửa
    private fun seedDataIfNeeded(context: Context, repo: FlashcardRepository) {
        CoroutineScope(Dispatchers.IO).launch {
            Seeder.seedIfNeeded(context, repo)
        }
    }
}