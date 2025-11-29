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

    // --- PHẦN 1: DỮ LIỆU TĨNH (FLASHCARD) - Giữ nguyên, đã tốt ---
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity> =
        flashcardDao.getFlashcardsByTopic(topicId)

    suspend fun insertAll(list: List<FlashcardEntity>) = flashcardDao.insertAll(list)

    suspend fun countFlashcards(): Int = flashcardDao.countAll()

    // --- PHẦN 2: DỮ LIỆU ĐỘNG (TIẾN ĐỘ HỌC) - NÂNG CẤP TOÀN DIỆN ---

    /**
     * A. GHI TIẾN ĐỘ - Được gọi từ ViewModel khi người dùng học 1 thẻ.
     * Đây là hàm chính thay thế cho upsertProgress cũ.
     */
    suspend fun recordProgress(flashcardId: String, newMasteryLevel: Int, newCorrectCountInRow: Int) {
        val userId = auth.currentUser?.uid ?: return // Dừng lại nếu chưa đăng nhập

        // 1. Tạo bản ghi tiến độ mới với cờ isSynced = false
        val progress = UserProgressEntity(
            flashcardId = flashcardId,
            userId = userId,
            masteryLevel = newMasteryLevel,
            correctCountInRow = newCorrectCountInRow,
            lastReviewed = Date(), // Ghi lại thời điểm hiện tại
            isSynced = false       // QUAN TRỌNG: Luôn là false khi mới ghi vào Room
        )

        // 2. Ghi vào Room ngay lập tức, UI sẽ cập nhật tức thì.
        userProgressDao.upsert(progress)

        // 3. Kích hoạt một tác vụ nền để đẩy dữ liệu này lên Firebase.
        syncProgressUp()
    }

    /**
     * B. ĐỒNG BỘ LÊN - Đẩy các tiến độ chưa được đồng bộ từ Room lên Firebase.
     * Tác vụ này sẽ được kích hoạt sau mỗi lần ghi tiến độ hoặc sau khi đăng nhập.
     */
    suspend fun syncProgressUp() {
        val userId = auth.currentUser?.uid ?: return
        val unsyncedItems = userProgressDao.getUnsyncedProgressForUser(userId)

        if (unsyncedItems.isEmpty()) return // Không có gì để làm.

        // Cấu trúc chuẩn: /user_progress/{userId}/items/{flashcardId}
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
                // Dùng SetOptions.merge() để không ghi đè dữ liệu khác trên server (nếu có).
                batch.set(docRef, progressMap, SetOptions.merge())
            }

            batch.commit().await() // Gửi toàn bộ lên server

            // Nếu gửi thành công, cập nhật lại cờ isSynced trong Room.
            val syncedIds = unsyncedItems.map { it.flashcardId }
            userProgressDao.markAsSynced(userId, syncedIds)

        } catch (e: Exception) {
            // Lỗi mạng hoặc server. Dữ liệu vẫn an toàn trong Room với isSynced = false.
            // Lần tới khi hàm này được gọi, nó sẽ thử lại.
        }
    }

    /**
     * C. ĐỒNG BỘ VỀ - Tải toàn bộ tiến độ của người dùng từ Firebase về và hợp nhất vào Room.
     * Tác vụ này nên được gọi ngay sau khi người dùng đăng nhập thành công.
     */
    suspend fun syncProgressDown() {
        val userId = auth.currentUser?.uid ?: return
        val collectionRef = firestore.collection("user_progress").document(userId).collection("items")

        try {
            val snapshot = collectionRef.get().await()

            for (document in snapshot.documents) {
                // TODO: Triển khai logic Hợp nhất (Merge) nâng cao hơn nếu cần.
                // Hiện tại, chúng ta ưu tiên dữ liệu từ server và ghi đè vào Room.
                val progress = UserProgressEntity(
                    flashcardId = document.id,
                    userId = userId,
                    masteryLevel = document.getLong("masteryLevel")?.toInt() ?: 0,
                    lastReviewed = document.getDate("lastReviewed") ?: Date(),
                    correctCountInRow = document.getLong("correctCountInRow")?.toInt() ?: 0,
                    isSynced = true // Dữ liệu từ server luôn được coi là đã đồng bộ
                )
                userProgressDao.upsert(progress)
            }
        } catch (e: Exception) {
            // Lỗi mạng, không thể tải dữ liệu. Ứng dụng vẫn dùng dữ liệu đang có trong Room.
        }
    }

    // ✨ HÀM MỚI BẠN CẦN THÊM VÀO ✨
    /**
     * Lấy toàn bộ tiến độ của một người dùng từ Room.
     * Hàm này là một "cầu nối" đơn giản, gọi thẳng đến hàm tương ứng trong DAO.
     */
    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity> =
        userProgressDao.getAllProgressForUser(userId)
}
