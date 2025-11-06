package com.example.babiling.data.repository

import com.example.babiling.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import java.lang.IllegalArgumentException
import kotlinx.coroutines.delay

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseUtils.auth

    var verificationId: String? = null

    // --- LOGIC PHONE AUTH ---
    suspend fun sendVerificationCode(phoneNumber: String): Boolean {
        // Mô phỏng kiểm tra số điện thoại (tương tự như kiểm tra của Firebase)
        if (phoneNumber.length < 10) {
            throw IllegalArgumentException("Số điện thoại không hợp lệ (cần ít nhất 10 chữ số).")
        }

        // Mô phỏng độ trễ mạng
        delay(1000)

        if (phoneNumber == "0123456789") {
            verificationId = "FAKE_VERIFICATION_ID_${System.currentTimeMillis()}"
            return true
        } else {
            throw Exception("Số điện thoại không đúng hoặc gặp lỗi Firebase.")
        }
    }

    // --- LOGIC SIGN IN WITH OTP ---
    suspend fun signInWithOtp(otpCode: String): Boolean {
        delay(1500)
        if (verificationId != null && otpCode == "123456") {
            // Logic Firebase thực tế: firebaseAuth.signInWithCredential(...)
            return true
        } else {
            throw Exception("Mã OTP không đúng hoặc quá trình xác minh đã hết hạn.")
        }
    }

    // --- LOGIC SIGN IN WITH GOOGLE ---
    suspend fun signInWithGoogle(): Boolean {
        delay(1500)
        // Mô phỏng: luôn thành công
        return true
    }
}