package com.example.babiling.data.model

import androidx.room.Entity
import com.google.firebase.firestore.Exclude // ✨ THÊM IMPORT NÀY ✨
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Lớp này đại diện cho tiến độ của một người dùng trên một thẻ flashcard cụ thể.
 * Nó được thiết kế để hoạt động với cả Room (local) và Firestore (remote).
 *
 * @param userId ID của người dùng.
 * @param flashcardId ID của thẻ học.
 * @param topicId ID của chủ đề chứa thẻ này.
 * @param masteryLevel Mức độ thành thạo của thẻ (0-5).
 * @param correctCountInRow Số lần trả lời đúng liên tiếp.
 * @param lastReviewed Thời gian học gần nhất, được Firestore tự động điền.
 * @param synced Cờ này CHỈ TỒN TẠI TRONG cơ sở dữ liệu ROOM.
 *               - `false`: Có thay đổi ở local, cần được đồng bộ LÊN server (sync up).
 *               - `true`: Dữ liệu ở local khớp với server.
 */
@Entity(tableName = "user_progress", primaryKeys = ["userId", "flashcardId"])
data class UserProgressEntity(
    val userId: String = "",
    val flashcardId: String = "",
    val topicId: String = "",
    val masteryLevel: Int = 0,
    val correctCountInRow: Int = 0,

    @ServerTimestamp
    var lastReviewed: Date? = null,

    // Dùng @get:Exclude để Firestore bỏ qua trường này khi ghi (serialize) dữ liệu LÊN.
    // Firestore sẽ không cố ghi trường "synced" lên server.
    // Tuy nhiên, khi đọc (deserialize) từ Firestore về, nó vẫn có thể đọc nếu có.
    // Đặt giá trị mặc định là `true` vì dữ liệu tải về từ Firestore mặc nhiên là đã được đồng bộ.
    @get:Exclude
    var synced: Boolean = true

) {
    // Firestore yêu cầu một constructor không tham số để có thể chuyển đổi DocumentSnapshot thành object.
    // Bằng cách cung cấp giá trị mặc định cho tất cả các thuộc tính trong constructor chính,
    // Kotlin sẽ tự động tạo một constructor không tham số cho chúng ta.
    // Nếu bạn xóa giá trị mặc định của một trường nào đó, bạn cần phải tự tạo một constructor rỗng ở đây:
    // constructor() : this(userId = "", flashcardId = "", ...)
}
