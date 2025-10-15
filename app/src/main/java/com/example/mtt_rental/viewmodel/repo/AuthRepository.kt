package com.example.mtt_rental.viewmodel.repo

import android.app.Activity
import com.example.mtt_rental.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

object AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")

    fun sendOTP(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onVerificationCompleted: (String?) -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onVerificationCompleted(credential.smsCode)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                onVerificationFailed("Gửi OTP thất bại: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+84$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOTPAndCreateUser(
        verificationId: String,
        otpCode: String,
        userData: User,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // OTP verified successfully, now save user to database
                    userRef.child(userData.idUser).setValue(userData)
                        .addOnSuccessListener {
                            onSuccess(userData)
                        }
                        .addOnFailureListener { exception ->
                            onError("Lỗi lưu thông tin người dùng: ${exception.message}")
                        }
                } else {
                    onError("Mã OTP không đúng. Vui lòng thử lại.")
                }
            }
    }
}