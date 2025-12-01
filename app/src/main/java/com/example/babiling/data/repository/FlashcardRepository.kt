package com.example.babiling.data.repository

import com.example.babiling.data.local.FlashcardDao
import com.example.babiling.data.local.UserProgressDao
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.UserProgressEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date

class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
    private val userProgressDao: UserProgressDao
) {
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // --- PHẦN 1: DỮ LIỆU TĨNH (FLASHCARD) ---
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity> =
        flashcardDao.getFlashcardsByTopic(topicId)

    suspend fun insertAll(list: List<FlashcardEntity>) = flashcardDao.insertAll(list)

    suspend fun countFlashcards(): Int = flashcardDao.countAll()

    // ✨ HÀM MỚI ĐÃ THÊM VÀO ✨
    /**
     * Lấy tất cả các thẻ flashcard từ database local.
     * Cần thiết cho chức năng "Ôn tập tất cả".
     */
    suspend fun getAllCards(): List<FlashcardEntity> {
        return flashcardDao.getAllCards()
    }
    // --- KẾT THÚC THÊM MỚI ---


    // --- PHẦN 2: DỮ LIỆU ĐỘNG (TIẾN ĐỘ HỌC) ---

    /**
     * A. GHI TIẾN ĐỘ - Được gọi từ ViewModel khi người dùng học 1 thẻ.
     */
    suspend fun recordProgress(flashcardId: String, newMasteryLevel: Int, newCorrectCountInRow: Int) {
        val userId = auth.currentUser?.uid ?: return // Dừng lại nếu chưa đăng nhập

        // 1. Tạo bản ghi tiến độ mới với cờ isSynced = false
        val progress = UserProgressEntity(
            flashcardId = flashcardId,
            userId = userId,
            masteryLevel = newMasteryLevel,
            correctCountInRow = newCorrectCountInRow,
            lastReviewed = Date(),
            isSynced = false
        )

        // 2. Ghi vào Room ngay lập tức.
        userProgressDao.upsert(progress)

        // 3. Kích hoạt tác vụ nền để đẩy dữ liệu này lên Firebase.
        syncProgressUp()
    }

    /**
     * B. ĐỒNG BỘ LÊN - Đẩy các tiến độ chưa được đồng bộ từ Room lên Firebase.
     */
    suspend fun syncProgressUp() {
        val userId = auth.currentUser?.uid ?: return
        val unsyncedItems = userProgressDao.getUnsyncedProgressForUser(userId)

        if (unsyncedItems.isEmpty()) return

        val collectionRef = firestore.collection("user_progress").document(userId).collection("items")

        try {
            val batch = firestore.batch()
            for (item in unsyncedItems) {
                val docRef = collectionRef.document(item.flashcardId)
                val progressMap = mapOf(
                    "masteryLevel" to item.masteryLevel,
                    "lastReviewed" to item.lastReviewed,
                    "correctCountInRow" to item.correctCountInRow
                )
                batch.set(docRef, progressMap, SetOptions.merge())
            }

            batch.commit().await()

            val syncedIds = unsyncedItems.map { it.flashcardId }
            userProgressDao.markAsSynced(userId, syncedIds)

        } catch (e: Exception) {
            // Lỗi mạng, không sao cả, dữ liệu vẫn an toàn trong Room.
        }
    }

    /**
     * C. ĐỒNG BỘ VỀ - Tải toàn bộ tiến độ của người dùng từ Firebase về và hợp nhất vào Room.
     */
    suspend fun syncProgressDown() {
        val userId = auth.currentUser?.uid ?: return
        val collectionRef = firestore.collection("user_progress").document(userId).collection("items")

        try {
            val snapshot = collectionRef.get().await()

            for (document in snapshot.documents) {
                val progress = UserProgressEntity(
                    flashcardId = document.id,
                    userId = userId,
                    masteryLevel = document.getLong("masteryLevel")?.toInt() ?: 0,
                    lastReviewed = document.getDate("lastReviewed") ?: Date(),
                    correctCountInRow = document.getLong("correctCountInRow")?.toInt() ?: 0,
                    isSynced = true
                )
                userProgressDao.upsert(progress)
            }
        } catch (e: Exception) {
            // Lỗi mạng.
        }
    }

    /**
     * Lấy toàn bộ tiến độ của một người dùng từ Room.
     */
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity> =
        userProgressDao.getAllProgressForUser(userId)
}
