package com.example.babiling

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * File chứa các hàm và hằng số tiện ích cho việc tương tác với Firebase.
 * Mục đích chính là cung cấp các instance Firebase singleton.
 */
object FirebaseUtils {

    // --- References to Firebase Services ---

    /**
     * Cung cấp instance của FirebaseAuth để xử lý xác thực (login, register, reset password).
     */
    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    /**
     * Cung cấp instance của FirebaseFirestore (Database NoSQL).
     * Thường dùng để lưu trữ hồ sơ người dùng, topic, lesson data.
     */
    val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    /**
     * Cung cấp instance của FirebaseStorage để quản lý file (ảnh avatar, audio lesson).
     */
    val storage: FirebaseStorage
        get() = FirebaseStorage.getInstance()

    /**
     * Cung cấp instance của Realtime Database (nếu ứng dụng sử dụng thay vì Firestore).
     * Bạn có thể chọn chỉ sử dụng một trong hai (Firestore hoặc Realtime Database).
     */
    val database: FirebaseDatabase
        get() = FirebaseDatabase.getInstance()


    // --- Firestore Path/Collection Constants ---

    object FirestoreCollections {
        const val USERS = "users"           // Lưu trữ User Profiles
        const val TOPICS = "topics"         // Lưu trữ danh sách các chủ đề
        const val LESSONS = "lessons"       // Lưu trữ dữ liệu bài học/câu hỏi
        const val PROGRESS = "progress"     // Lưu trữ tiến độ học tập của người dùng
    }

    // --- Utility Functions ---

    /**
     * Tạo đường dẫn đầy đủ đến avatar của người dùng trong Firebase Storage.
     */
    fun getAvatarStorageRef(userId: String) = storage.reference.child("avatars/$userId.jpg")

    /**
     * Lấy đường dẫn Collection Firestore cho người dùng (ví dụ: firestore.collection("users")).
     */
    fun getUsersCollection() = firestore.collection(FirestoreCollections.USERS)

    /**
     * Lấy đường dẫn Document Firestore cho một người dùng cụ thể.
     */
    fun getUserDocument(userId: String) = getUsersCollection().document(userId)

}