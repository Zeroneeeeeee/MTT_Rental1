package com.example.mtt_rental.viewmodel.tenant

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mtt_rental.model.User
import com.example.mtt_rental.viewmodel.repo.AuthRepository

class OTPVerificationViewModel : ViewModel() {

    private val _otpCode = mutableStateOf("")
    val otpCode: State<String> = _otpCode

    private val _verificationId = mutableStateOf("")
    val verificationId: State<String> = _verificationId

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _successMessage = mutableStateOf("")
    val successMessage: State<String> = _successMessage

    private val _verificationResult = mutableStateOf<VerificationResult?>(null)
    val verificationResult: State<VerificationResult?> = _verificationResult

    fun updateOtpCode(code: String) {
        _otpCode.value = code
        _errorMessage.value = ""
    }

    fun sendOTP(phoneNumber: String, activity: Activity) {
        _isLoading.value = true
        _errorMessage.value = ""

        AuthRepository.sendOTP(
            phoneNumber = phoneNumber,
            activity = activity,
            onCodeSent = { verificationId ->
                _verificationId.value = verificationId
                _isLoading.value = false
                _successMessage.value = "Mã OTP đã được gửi đến số điện thoại của bạn"
            },
            onVerificationCompleted = { smsCode ->
                _otpCode.value = smsCode ?: ""
                _isLoading.value = false
            },
            onVerificationFailed = { error ->
                _isLoading.value = false
                _errorMessage.value = error
            }
        )
    }

    fun verifyOTP(userData: User) {
        if (_otpCode.value.length != 6) {
            _errorMessage.value = "Vui lòng nhập đầy đủ 6 số"
            return
        }

        _isLoading.value = true

        AuthRepository.verifyOTPAndCreateUser(
            verificationId = _verificationId.value,
            otpCode = _otpCode.value,
            userData = userData,
            onSuccess = { user ->
                _isLoading.value = false
                _successMessage.value = "Đăng ký thành công!"
                _verificationResult.value = VerificationResult.Success(user)
            },
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = error
                _verificationResult.value = VerificationResult.Error(error)
            }
        )
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }

    fun clearVerificationResult() {
        _verificationResult.value = null
    }
}

sealed class VerificationResult {
    data class Success(val user: User) : VerificationResult()
    data class Error(val message: String) : VerificationResult()
}