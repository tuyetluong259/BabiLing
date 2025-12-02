package com.example.babiling.data.model

import androidx.room.Entity
import com.google.firebase.firestore.PropertyName // ✨ THÊM IMPORT NÀY ✨
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/** * ✨ PHIÊN BẢN HOÀN THIỆN - ĐÃ SỬA LỖI VỚI FIRESTORE ✨
 * Lớp này đại diện cho tiến độ của một người dùng trên một thẻ flashcard cụ thể.
 *
 * @param userId ID của người dùng sở hữu tiến độ này.
 * @param flashcardId ID của thẻ học, ví dụ "animal_001".
 * @param topicId ID của chủ đề chứa thẻ này.
 * @param masteryLevel Mức độ thành thạo, ví dụ từ 0 (chưa biết) đến 5 (đã thành thạo).
 * @param lastReviewed Thời gian học gần nhất. @ServerTimestamp sẽ để Firestore tự điền giờ.
 * @param correctCountInRow Số lần trả lời đúng liên tiếp.
 * @param isSynced Cờ này CHỈ TỒN TẠI TRONG ROOM. true nếu đã đồng bộ.
 */
// Sửa lại PrimaryKey để bao gồm cả userId, đảm bảo mỗi user có một bộ tiến độ riêng
@Entity(tableName = "user_progress", primaryKeys = ["userId", "flashcardId"])
data class UserProgressEntity(
    // Các trường khác đã rất tốt
    val userId: String = "",
    val flashcardId: String = "",
    val topicId: String = "",
    val masteryLevel: Int = 0,
    val correctCountInRow: Int = 0,
    @ServerTimestamp val lastReviewed: Date? = null,

    // ✨ SỬA ĐỔI: Thêm Annotation để chỉ rõ tên trên Firestore ✨
    // Thuộc tính trong Kotlin là "isSynced", nhưng trên Firestore sẽ được lưu là "synced".
    @get:PropertyName("synced")
    val isSynced: Boolean = false

    // Khi tất cả các trường có giá trị mặc định, Kotlin sẽ tự tạo một hàm khởi tạo không tham số:
    // UserProgressEntity()
    // -> Firestore sẽ dùng hàm này và không báo lỗi nữa.
)
