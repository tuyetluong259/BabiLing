package com.example.babiling.data.repository

import android.content.Context
import android.content.Intent
import com.example.babiling.utils.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest // ✨ IMPORT MỚI ✨
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

/**
 * Repository xử lý logic liên quan đến xác thực người dùng (Authentication)
 * và lưu trữ thông tin cơ bản của người dùng lên Firestore.
 */
class AuthRepository(
    // ✅ THAM SỐ ĐƯỢC TRUYỀN TỪ ServiceLocator: Để tránh lỗi "No parameter with name 'firebaseAuth' found."
    val firebaseAuth: FirebaseAuth,
    private val context: Context
) {
    private val firestoreDb: FirebaseFirestore = FirebaseUtils.firestore

    // Cần lấy web_client_id từ file google-services.json của bạn
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("683013257609-de6tu6gqjh09oq2s1mh5hk9mia2pi5g9.apps.googleusercontent.com")
        .requestEmail()
        .build()

    // Khởi tạo GoogleSignInClient bằng Context đã được truyền vào
    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // ... (Các hàm truy vấn Google SignIn và handleGoogleSignIn giữ nguyên) ...

    /**
     * Cung cấp GoogleSignInClient cho các thành phần bên ngoài (như LoginScreen)
     * để có thể gọi các hành động như signOut() hoặc lấy signInIntent.
     */
    fun getGoogleSignInClient(): com.google.android.gms.auth.api.signin.GoogleSignInClient {
        return googleSignInClient
    }

    /**
     * Lấy Intent để bắt đầu quá trình đăng nhập bằng Google.
     * LoginScreen sẽ gọi hàm này.
     */
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Xử lý kết quả trả về từ Google, đăng nhập vào Firebase và lưu vào Firestore.
     * ViewModel sẽ gọi hàm này sau khi MainActivity nhận được kết quả.
     */
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
                "level" to 1
            )
            firestoreDb.collection("users").document(firebaseUser.uid).set(userProfile).await()
        }
    }


    /**
     * Thực hiện đăng nhập bằng Email và Password.
     */
    suspend fun signInWithEmailPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Tạo một người dùng mới trong Firebase Authentication VÀ lưu hồ sơ vào Firestore.
     */
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
            "level" to 1
        )
        firestoreDb.collection("users").document(firebaseUser.uid).set(userProfile).await()
    }

    // ✨ HÀM MỚI: CẬP NHẬT HỒ SƠ NGƯỜI DÙNG ✨
    /**
     * Cập nhật username (Firestore) và tên hiển thị (Firestore & Firebase Auth)
     */
    suspend fun updateProfile(uid: String, newUsername: String, newAccountName: String) {
        val userRef = firestoreDb.collection("users").document(uid)

        // 1. Cập nhật Firestore (để lưu username và displayName)
        val updates = hashMapOf(
            "username" to newUsername,
            "displayName" to newAccountName,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        userRef.update(updates).await()

        // 2. Cập nhật Firebase Auth (để cập nhật displayName)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newAccountName)
            .build()

        firebaseAuth.currentUser?.updateProfile(profileUpdates)?.await()
    }

    /**
     * Lấy thông tin người dùng hiện tại đang đăng nhập.
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Thực hiện đăng xuất người dùng ra khỏi thiết bị.
     */
    fun signOut() {
        firebaseAuth.signOut()
        // Đăng xuất khỏi Google để lần sau có thể chọn lại tài khoản
        googleSignInClient.signOut()
    }
}