package com.example.babiling.data.repository

// ✨ 1. XÓA CÁC IMPORT SAI ✨
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

    suspend fun getAllCards(): List<FlashcardEntity> {
        return flashcardDao.getAllCards()
    }

    // ✨ ================= BỔ SUNG CHO LUỒNG HỌC THEO BÀI ================= ✨

    suspend fun getFlashcardsByLesson(topicId: String, lessonNumber: Int): List<FlashcardEntity> {
        return flashcardDao.getFlashcardsByLesson(topicId, lessonNumber)
    }

    suspend fun getLessonNumbersForTopic(topicId: String): List<Int> {
        return flashcardDao.getLessonNumbersForTopic(topicId)
    }

    // ✨ ======================= KẾT THÚC BỔ SUNG ======================= ✨


    // --- PHẦN 2: DỮ LIỆU ĐỘNG (TIẾN ĐỘ HỌC) ---

    // ✨ ================= BỔ SUNG CHO MÀN HÌNH TIẾN ĐỘ ================= ✨

    suspend fun getLearnedCardsCount(userId: String, topicId: String): Int {
        val cardIdsInTopic = flashcardDao.getCardIdsByTopic(topicId)
        if (cardIdsInTopic.isEmpty()) {
            return 0
        }
        return userProgressDao.getLearnedCardsCountForUserInTopic(userId, cardIdsInTopic)
    }

    // ✨ ======================= KẾT THÚC BỔ SUNG ======================= ✨


    suspend fun recordProgress(flashcardId: String, newMasteryLevel: Int, newCorrectCountInRow: Int) {
        val userId = auth.currentUser?.uid ?: return
        val progress = UserProgressEntity(
            userId = userId,
            flashcardId = flashcardId,
            masteryLevel = newMasteryLevel,
            correctCountInRow = newCorrectCountInRow,
            lastReviewed = Date(),
            isSynced = false
        )
        // ✨ 2. SỬA TÊN HÀM CHO ĐÚNG ✨
        userProgressDao.upsert(progress)
    }

    suspend fun syncProgressUp() {
        val userId = auth.currentUser?.uid ?: return
        // ✨ 3. SỬA TÊN HÀM CHO ĐÚNG ✨
        val unSyncedProgress = userProgressDao.getUnsyncedProgressForUser(userId)
        if (unSyncedProgress.isEmpty()) return // Không có gì để đồng bộ

        val batch = firestore.batch()
        unSyncedProgress.forEach { progress ->
            val docRef = firestore.collection("users").document(userId)
                .collection("progress").document(progress.flashcardId)
            batch.set(docRef, progress, SetOptions.merge())
        }
        batch.commit().await()

        // Đánh dấu đã đồng bộ
        // ✨ 4. SỬA LẠI LỜI GỌI HÀM CHO ĐÚNG ✨
        userProgressDao.markAsSynced(userId, unSyncedProgress.map { it.flashcardId })
    }

    suspend fun syncProgressDown() {
        val userId = auth.currentUser?.uid ?: return
        val snapshot = firestore.collection("users").document(userId)
            .collection("progress").get().await()
        val firestoreProgress = snapshot.toObjects(UserProgressEntity::class.java)

        // ✨ 5. SỬA LẠI LOGIC CHO ĐÚNG ✨
        // Thay vì gọi một hàm không tồn tại, chúng ta sẽ lặp và `upsert` từng cái
        if (firestoreProgress.isNotEmpty()) {
            firestoreProgress.forEach { progress ->
                userProgressDao.upsert(progress)
            }
        }
    }

    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity> =
        userProgressDao.getAllProgressForUser(userId)
}
