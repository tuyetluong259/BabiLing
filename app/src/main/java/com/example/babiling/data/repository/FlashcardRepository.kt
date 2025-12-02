package com.example.babiling.data.repository

import android.util.Log
import com.example.babiling.data.local.FlashcardDao
import com.example.babiling.data.local.TopicDao
import com.example.babiling.data.local.UserProgressDao
import com.example.babiling.data.model.FlashcardEntity
import com.example.babiling.data.model.TopicEntity
import com.example.babiling.data.model.UserProgressEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repository quản lý việc truy cập dữ liệu Flashcard và tiến độ người dùng (UserProgress).
 * ✨ PHIÊN BẢN HOÀN THIỆN - Đã đầy đủ các hàm cần thiết ✨
 */
class FlashcardRepository(
    private val flashcardDao: FlashcardDao,
    private val userProgressDao: UserProgressDao,
    private val topicDao: TopicDao
) {
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // --- PHẦN 0: TRUY VẤN DỮ LIỆU TOPIC ---
    suspend fun insertAllTopics(topics: List<TopicEntity>) = topicDao.insertAll(topics)
    suspend fun countTopics(): Int = topicDao.count()
    suspend fun getAllTopicsStatic(): List<TopicEntity> = topicDao.getAllTopicsStatic()


    // --- PHẦN 1: TRUY VẤN DỮ LIỆU FLASHCARD ---
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity> =
        flashcardDao.getFlashcardsByTopic(topicId)

    suspend fun insertAllFlashcards(list: List<FlashcardEntity>) = flashcardDao.insertAll(list)

    suspend fun countFlashcards(): Int = flashcardDao.countAll()

    suspend fun getAllCards(): List<FlashcardEntity> = flashcardDao.getAllCards()

    suspend fun getFlashcardsForLesson(topicId: String, lessonNumber: Int): List<FlashcardEntity> =
        flashcardDao.getFlashcardsByLesson(topicId, lessonNumber)

    suspend fun getLessonNumbersForTopic(topicId: String): List<Int> =
        flashcardDao.getLessonNumbersForTopic(topicId)

    suspend fun getCardCountForTopic(topicId: String): Int = flashcardDao.getCardCountForTopic(topicId)


    // --- CÁC HÀM FLOW CHO VIỆC CẬP NHẬT UI TỰ ĐỘNG ---

    fun getCompletedLessonsFlow(topicId: String): Flow<Set<Int>> {
        val userId = auth.currentUser?.uid ?: return flowOf(emptySet())

        return flowOf(userId).flatMapLatest { currentUserId ->
            val cardIdsInTopic = flashcardDao.getCardIdsByTopic(topicId)
            if (cardIdsInTopic.isEmpty()) {
                flowOf(emptySet())
            } else {
                userProgressDao.getCompletedLessonsFlowForUser(currentUserId, cardIdsInTopic)
                    .map { completedLessonNumbers -> completedLessonNumbers.toSet() }
            }
        }
    }

    // --- PHẦN 2: TRUY VẤN TIẾN ĐỘ HỌC CỦA NGƯỜI DÙNG ---

    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity> =
        userProgressDao.getAllProgressForUser(userId)


    // --- PHẦN 3: GHI VÀ ĐỒNG BỘ TIẾN ĐỘ HỌC ---

    suspend fun recordMultipleProgress(cards: List<FlashcardEntity>) {
        val userId = auth.currentUser?.uid ?: return

        val progressList = cards.map { card ->
            UserProgressEntity(
                userId = userId,
                flashcardId = card.id,
                masteryLevel = 1,
                lastReviewed = Date(),
                isSynced = false,
                topicId = card.topicId
            )
        }
        userProgressDao.upsertAll(progressList)
        Log.d("BabiLing_Repo", "Đã yêu cầu DAO ghi ${progressList.size} bản ghi tiến độ.")
    }

    // ✨ ================= HÀM MỚI CHO QUIZVIEWMODEL THÊM VÀO ĐÂY ================= ✨
    /**
     * Ghi nhận hoặc cập nhật tiến độ cho MỘT thẻ duy nhất vào CSDL Room.
     * Được gọi bởi QuizViewModel sau khi người dùng trả lời một câu hỏi.
     */
    suspend fun recordSingleProgress(
        flashcardId: String,
        topicId: String,
        newMasteryLevel: Int,
        newCorrectCountInRow: Int
    ) {
        val userId = auth.currentUser?.uid ?: return
        val progress = UserProgressEntity(
            userId = userId,
            flashcardId = flashcardId,
            topicId = topicId,
            masteryLevel = newMasteryLevel,
            correctCountInRow = newCorrectCountInRow,
            lastReviewed = Date(),
            isSynced = false // Đánh dấu là chưa đồng bộ
        )
        userProgressDao.upsert(progress)
        Log.d("BabiLing_Repo", "Đã ghi tiến độ cho thẻ: $flashcardId")
    }
    // ✨ ========================================================================= ✨

    suspend fun syncProgressUp() {
        val userId = auth.currentUser?.uid ?: return
        val unSyncedProgress = userProgressDao.getUnsyncedProgressForUser(userId)

        if (unSyncedProgress.isEmpty()) {
            Log.d("BabiLing_Sync", "[syncUp] Không có tiến độ mới để đồng bộ lên.")
            return
        }

        Log.d("BabiLing_Sync", "[syncUp] Tìm thấy ${unSyncedProgress.size} bản ghi. Bắt đầu đẩy lên Firestore...")
        val batch = firestore.batch()
        unSyncedProgress.forEach { progress ->
            val docRef = firestore.collection("users").document(userId)
                .collection("progress").document(progress.flashcardId)
            // Ghi progress LÊN Firestore, nhưng loại bỏ trường isSynced vì nó chỉ có ý nghĩa ở local
            val progressMap = mapOf(
                "userId" to progress.userId,
                "flashcardId" to progress.flashcardId,
                "topicId" to progress.topicId,
                "masteryLevel" to progress.masteryLevel,
                "correctCountInRow" to progress.correctCountInRow,
                "lastReviewed" to progress.lastReviewed,
                // Không đưa isSynced/synced lên Firestore
            )
            batch.set(docRef, progressMap, SetOptions.merge())
        }

        try {
            batch.commit().await()
            Log.d("BabiLing_Sync", "[syncUp] Đẩy lên Firestore thành công. Đánh dấu đã đồng bộ trong Room...")
            val syncedIds = unSyncedProgress.map { it.flashcardId }
            userProgressDao.markAsSynced(userId, syncedIds)
            Log.d("BabiLing_Sync", "[syncUp] Hoàn tất đồng bộ lên.")
        } catch (e: Exception) {
            Log.e("BabiLing_Sync", "[syncUp] LỖI khi commit batch hoặc đánh dấu đồng bộ!", e)
        }
    }

    suspend fun syncProgressDown() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("BabiLing_Sync", "[syncDown] Bắt đầu tải tiến độ từ Firestore...")
        try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("progress").get().await()
            // Khi tải về, chúng ta mặc định isSynced là true vì nó đến từ server
            val firestoreProgressList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(UserProgressEntity::class.java)?.copy(isSynced = true)
            }

            if (firestoreProgressList.isNotEmpty()) {
                Log.d("BabiLing_Sync", "[syncDown] Tải về ${firestoreProgressList.size} bản ghi. Ghi vào Room...")
                userProgressDao.upsertAll(firestoreProgressList)
                Log.d("BabiLing_Sync", "[syncDown] Hoàn tất đồng bộ xuống.")
            } else {
                Log.d("BabiLing_Sync", "[syncDown] Không tìm thấy tiến độ trên Firestore.")
            }
        } catch (e: Exception) {
            Log.e("BabiLing_Sync", "[syncDown] LỖI khi tải tiến độ từ Firestore!", e)
        }
    }
}
