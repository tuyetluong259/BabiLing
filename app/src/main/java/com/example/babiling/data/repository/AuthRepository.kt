package com.example.babiling.data.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.babiling.utils.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import android.util.Log // ✅ Cần Log để ghi lỗi

/**
 * Repository xử lý logic liên quan đến xác thực người dùng (Authentication)
 * và lưu trữ thông tin cơ bản của người dùng lên Firestore.
 */
class AuthRepository(
    val firebaseAuth: FirebaseAuth,
    private val context: Context
) {
    private val firestoreDb: FirebaseFirestore = FirebaseUtils.firestore

    // ✅ KHỞI TẠO SHOAREDPREFERENCES: Giữ lại để lưu cache cho việc kiểm tra nhanh
    private val setupPrefs: SharedPreferences = context.getSharedPreferences("BABILING_SETUP_STATUS", Context.MODE_PRIVATE)

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("683013257609-de6tu6gqjh09oq2s1mh5hk9mia2pi5g9.apps.googleusercontent.com")
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // ---------------------------------------------
    // MARK: PROFILE SETUP CHECK
    // ---------------------------------------------

    /**
     * KIỂM TRA TRẠNG THÁI SETUP BAN ĐẦU (SYNC)
     * Hàm này chỉ kiểm tra local cache (SharedPreferences).
     * Hàm chính xác phải là fetchIsProfileSetupComplete() (Async)
     */
    fun isProfileSetupComplete(): Boolean {
        val uid = getCurrentUser()?.uid ?: return false
        val setupKey = "${uid}_setup"
        return setupPrefs.getBoolean(setupKey, false)
    }

    /**
     * ✅ HÀM MỚI: KIỂM TRA TRẠNG THÁI SETUP BAN ĐẦU TỪ FIRESTORE (ASYNC)
     * Đây là nguồn dữ liệu bền vững (persistent) để xử lý việc cài đặt lại ứng dụng.
     */
    suspend fun fetchIsProfileSetupComplete(): Boolean {
        val uid = getCurrentUser()?.uid ?: return false
        return try {
            val documentSnapshot = firestoreDb.collection("users").document(uid).get().await()
            // Đọc cờ isSetupComplete từ Firestore. Mặc định là FALSE nếu không tìm thấy.
            val isComplete = documentSnapshot.getBoolean("isSetupComplete") ?: false

            // Cập nhật lại cache local (SharedPreferences) sau khi fetch thành công
            val setupKey = "${uid}_setup"
            setupPrefs.edit().putBoolean(setupKey, isComplete).apply()

            isComplete
        } catch (e: Exception) {
            Log.e("AuthRepo", "Lỗi fetch trạng thái setup từ Firestore", e)
            // Trong trường hợp lỗi mạng, quay lại kiểm tra cache (tính năng hiện tại)
            isProfileSetupComplete()
        }
    }

    /**
     * ✅ SỬA LOGIC: ĐÁNH DẤU TRẠNG THÁI ĐÃ HOÀN THÀNH SETUP
     * Ghi cả vào SharedPreferences (cache) và Firestore (persistence).
     */
    fun markProfileSetupComplete(isComplete: Boolean = true) {
        val uid = getCurrentUser()?.uid
            ?: run { return }

        // 1. Ghi vào Firestore (Persistence)
        firestoreDb.collection("users").document(uid)
            .update("isSetupComplete", isComplete)
            .addOnFailureListener { e ->
                Log.e("AuthRepo", "Lỗi ghi cờ setup lên Firestore", e)
            }

        // 2. Ghi vào SharedPreferences (Cache)
        val setupKey = "${uid}_setup"
        setupPrefs.edit().putBoolean(setupKey, isComplete).apply()
    }

    // ---------------------------------------------
    // MARK: AUTH FUNCTIONS
    // ---------------------------------------------

    fun getGoogleSignInClient(): com.google.android.gms.auth.api.signin.GoogleSignInClient {
        return googleSignInClient
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleGoogleSignIn(data: Intent?) {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
        val idToken = account.idToken ?: throw Exception("Không lấy được ID token từ Google.")

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val firebaseUser = authResult.user ?: throw Exception("Không thể đăng nhập vào Firebase bằng Google.")

        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

        if (isNewUser) {
            val userProfile = hashMapOf(
                "uid" to firebaseUser.uid,
                "username" to (firebaseUser.displayName ?: "User"),
                "email" to firebaseUser.email,
                "photoUrl" to firebaseUser.photoUrl.toString(),
                "createdAt" to FieldValue.serverTimestamp(),
                "score" to 0,
                "level" to 1,
                // ✅ THÊM CỜ SETUP MẶC ĐỊNH CHO USER MỚI
                "isSetupComplete" to false
            )
            firestoreDb.collection("users").document(firebaseUser.uid).set(userProfile).await()
        }
    }

    suspend fun signInWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun createUser(email: String, password: String) {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Không thể tạo người dùng trong Authentication.")
        val username = email.substringBefore('@')

        val userProfile = hashMapOf(
            "uid" to firebaseUser.uid,
            "username" to username,
            "email" to email,
            "createdAt" to FieldValue.serverTimestamp(),
            "score" to 0,
            "level" to 1,
            // ✅ THÊM CỜ SETUP MẶC ĐỊNH CHO USER MỚI
            "isSetupComplete" to false
        )
        firestoreDb.collection("users").document(firebaseUser.uid).set(userProfile).await()
    }

    suspend fun updateProfile(uid: String, newUsername: String, newAccountName: String) {
        val userRef = firestoreDb.collection("users").document(uid)

        // 1. Cập nhật Firestore
        val updates = hashMapOf(
            "username" to newUsername,
            "displayName" to newAccountName,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userRef.update(updates).await()

        // 2. Cập nhật Firebase Auth
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newAccountName)
            .build()

        firebaseAuth.currentUser?.updateProfile(profileUpdates)?.await()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Thực hiện đăng xuất người dùng ra khỏi thiết bị.
     * KHÔNG xóa cờ setup (vì nó nằm trên Firestore).
     */
    fun signOut() {
        val uid = getCurrentUser()?.uid
        if (uid != null) {
            // Xóa cache local (nếu cần)
            val setupKey = "${uid}_setup"
            setupPrefs.edit().remove(setupKey).apply()
        }

        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}