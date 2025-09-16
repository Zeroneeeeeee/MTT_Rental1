package com.example.mtt_rental.ui.tenant

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mtt_rental.model.User
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mtt_rental.viewmodel.tenant.OTPVerificationViewModel
import com.example.mtt_rental.viewmodel.tenant.VerificationResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationScreen(
    phoneNumber: String,
    userData: User,
    onVerificationSuccess: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: OTPVerificationViewModel = viewModel()
) {
    val context = LocalContext.current
    var resendCountdown by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    val otpCode by viewModel.otpCode
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage
    val verificationResult by viewModel.verificationResult

    // Handle verification result
    LaunchedEffect(verificationResult) {
        val result = verificationResult
        when (result) {
            is VerificationResult.Success -> {
                onVerificationSuccess()
                viewModel.clearVerificationResult()
            }

            is VerificationResult.Error -> {
                // Error is already handled in errorMessage state
                viewModel.clearVerificationResult()
            }

            null -> {
                // No result yet
            }
        }
    }

    // Start countdown timer and send initial OTP
    LaunchedEffect(Unit) {
        viewModel.sendOTP(phoneNumber, context as Activity)
        while (resendCountdown > 0) {
            delay(1000)
            resendCountdown--
        }
        canResend = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Xác thực OTP",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Nhập mã OTP được gửi đến số điện thoại",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Text(
            text = phoneNumber,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // OTP Input
        OTPInputField(
            otpCode = otpCode,
            onOtpChange = { viewModel.updateOtpCode(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Verify Button
        Button(
            onClick = {
                viewModel.verifyOTP(userData)
            },
            enabled = !isLoading && otpCode.length == 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Xác thực OTP", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Resend OTP
        if (canResend) {
            TextButton(
                onClick = {
                    canResend = false
                    resendCountdown = 60
                    viewModel.sendOTP(phoneNumber, context as Activity)
                }
            ) {
                Text("Gửi lại mã OTP")
            }
        } else {
            Text(
                text = "Gửi lại mã sau ${resendCountdown}s",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        // Countdown effect for resend
        LaunchedEffect(canResend) {
            if (!canResend && resendCountdown > 0) {
                while (resendCountdown > 0) {
                    delay(1000)
                    resendCountdown--
                }
                canResend = true
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error/Success Messages
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = Color.Green,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OTPInputField(
    otpCode: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(6) { index ->
            val isFocused = otpCode.length == index
            val char = otpCode.getOrNull(index)?.toString() ?: ""

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = if (isFocused) MaterialTheme.colorScheme.primary
                        else if (char.isNotEmpty()) MaterialTheme.colorScheme.primary
                        else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        if (char.isNotEmpty()) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = char,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            val newOtp = otpCode.toMutableList()
                            if (index < newOtp.size) {
                                newOtp[index] = newValue.firstOrNull() ?: ' '
                            } else if (newValue.isNotEmpty()) {
                                newOtp.add(newValue.first())
                            }

                            val updatedOtp = if (newValue.isEmpty() && index < otpCode.length) {
                                otpCode.removeRange(index, index + 1)
                            } else {
                                newOtp.take(6).joinToString("").replace(" ", "")
                            }

                            onOtpChange(updatedOtp)

                            // Auto focus next field
                            if (newValue.isNotEmpty() && index < 5) {
                                focusRequesters.getOrNull(index + 1)?.requestFocus()
                            }
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .focusRequester(focusRequesters[index])
                        .fillMaxSize()
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OTPVerificationScreenPreview() {
    OTPVerificationScreen(
        phoneNumber = "+84123456789",
        userData = User(
            idUser = "testuser",
            password = "password123",
            email = "test@example.com",
            phoneNumber = "+84123456789",
            profileName = "Test User",
            userType = "User"
        )
    )
}