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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository quản lý việc truy cập dữ liệu Flashcard và tiến độ người dùng (UserProgress).
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

    // ✅ SỬA LỖI: Đổi tên hàm từ getAllTopicsFlow() thành getAllTopics()
    fun getAllTopicsFlow(): Flow<List<TopicEntity>> = topicDao.getAllTopics()


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
            // Lưu ý: flashcardDao.getCardIdsByTopic là hàm suspend tĩnh,
            // có thể gây vấn đề nếu được gọi không đúng cách trong Flow
            // Hiện tại ta giữ nguyên, giả định DAO này là suspend.
            val cardIdsInTopic = flashcardDao.getCardIdsByTopic(topicId)
            if (cardIdsInTopic.isEmpty()) {
                flowOf(emptySet())
            } else {
                userProgressDao.getCompletedLessonsFlowForUser(currentUserId, cardIdsInTopic)
                    .map { completedLessonNumbers -> completedLessonNumbers.toSet() }
            }
        }
    }

    // ✅ THÊM HÀM FLOW MỚI: Lắng nghe sự thay đổi của toàn bộ tiến trình người dùng
    fun getAllProgressFlow(userId: String): Flow<List<UserProgressEntity>> =
        userProgressDao.getAllProgressFlowForUser(userId)


    // --- PHẦN 2: TRUY VẤN TIẾN ĐỘ HỌC CỦA NGƯỜI DÙNG ---

    suspend fun getAllProgressForUser(userId: String): List<UserProgressEntity> =
        userProgressDao.getAllProgressForUser(userId)


    // --- PHẦN 3: GHI VÀ ĐỒNG BỘ TIẾN ĐỘ HỌC ---

    suspend fun upsertMultipleProgress(progressList: List<UserProgressEntity>) {
        withContext(Dispatchers.IO) {
            userProgressDao.upsertAll(progressList)
            Log.d("BabiLing_Repo", "Đã yêu cầu DAO ghi (upsert) ${progressList.size} bản ghi tiến độ.")
        }
    }

    // ... (recordSingleProgress giữ nguyên) ...
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
            synced = false // Đánh dấu là chưa đồng bộ
        )
        userProgressDao.upsert(progress)
        Log.d("BabiLing_Repo", "Đã ghi tiến độ cho thẻ: $flashcardId")
    }

    // ... (syncProgressUp giữ nguyên) ...
    /**
     * Đẩy các tiến độ chưa được đồng bộ từ Room lên Firestore.
     */
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
            // Ghi progress LÊN Firestore, nhưng loại bỏ trường `synced` vì nó chỉ có ý nghĩa ở local
            val progressMap = mapOf(
                "userId" to progress.userId,
                "flashcardId" to progress.flashcardId,
                "topicId" to progress.topicId,
                "masteryLevel" to progress.masteryLevel,
                "correctCountInRow" to progress.correctCountInRow,
                "lastReviewed" to progress.lastReviewed,
                // Không đưa `synced`/`isSynced` lên Firestore
            )
            batch.set(docRef, progressMap, SetOptions.merge())
        }

        try {
            batch.commit().await()
            Log.d("BabiLing_Sync", "[syncUp] Đẩy lên Firestore thành công. Đánh dấu đã đồng bộ trong Room...")
            // Lấy danh sách ID đã đồng bộ thành công để cập nhật lại cờ `synced = true` trong Room
            val syncedIds = unSyncedProgress.map { it.flashcardId }
            userProgressDao.markAsSynced(userId, syncedIds)
            Log.d("BabiLing_Sync", "[syncUp] Hoàn tất đồng bộ lên.")
        } catch (e: Exception) {
            Log.e("BabiLing_Sync", "[syncUp] LỖI khi commit batch hoặc đánh dấu đồng bộ!", e)
        }
    }

    // ... (syncProgressDown giữ nguyên) ...
    /**
     * Tải các tiến độ từ Firestore về và ghi đè vào Room.
     */
    suspend fun syncProgressDown() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("BabiLing_Sync", "[syncDown] Bắt đầu tải tiến độ từ Firestore...")
        try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("progress").get().await()

            if (snapshot.isEmpty) {
                Log.d("BabiLing_Sync", "[syncDown] Không tìm thấy tiến độ trên Firestore.")
                return
            }

            // Khi tải về, chúng ta mặc định `synced` là true vì nó đến từ server
            val firestoreProgressList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(UserProgressEntity::class.java)?.copy(synced = true)
            }

            if (firestoreProgressList.isNotEmpty()) {
                Log.d("BabiLing_Sync", "[syncDown] Tải về ${firestoreProgressList.size} bản ghi. Ghi vào Room...")
                userProgressDao.upsertAll(firestoreProgressList)
                Log.d("BabiLing_Sync", "[syncDown] Hoàn tất đồng bộ xuống.")
            }
        } catch (e: Exception) {
            Log.e("BabiLing_Sync", "[syncDown] LỖI khi tải tiến độ từ Firestore!", e)
        }
    }

    suspend fun getCardCountPerLesson(topicId: String): Map<Int, Int> {
        return withContext(Dispatchers.IO) {
            // Gọi hàm DAO và biến đổi danh sách thành một Map
            flashcardDao.getCardCountPerLessonFromDb(topicId)
                .associate { it.lessonNumber to it.cardCount }
        }
    }
}