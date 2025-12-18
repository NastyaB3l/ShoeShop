package com.example.shoeshop.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstproject.data.model.VerifyOtpRequest
import com.example.shoeshop.data.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class EmailVerificationViewModel : ViewModel() {
    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    private val _recoveryState = MutableStateFlow<RecoveryState?>(null)
    val recoveryState: StateFlow<RecoveryState?> = _recoveryState

    fun verifyEmailOtp(email: String, otpCode: String) {
        verifyOtpInternal(email, otpCode, OtpType.EMAIL)
    }

    fun verifyRecoveryOtp(email: String, otpCode: String) {
        verifyOtpInternal(email, otpCode, OtpType.RECOVERY)
    }

    private fun verifyOtpInternal(email: String, otpCode: String, otpType: OtpType) {
        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.Loading

                when (otpType) {
                    OtpType.EMAIL -> {
                        val response = RetrofitInstance.userManagementService.verifyOtp(
                            VerifyOtpRequest(
                                email = email,
                                token = otpCode,
                                type = "signup" // Supabase использует "signup", а не "email"
                            )
                        )

                        if (response.isSuccessful) {
                            response.body()?.let { verifyResponse ->
                                Log.v("verifyEmailOtp", "Email verified successfully")
                                _verificationState.value = VerificationState.Success(
                                    type = OtpType.EMAIL,
                                    data = verifyResponse
                                )
                            } ?: run {
                                _verificationState.value = VerificationState.Error("Empty response")
                            }
                        } else {
                            val errorMessage = parseVerificationError(response.code(), response.message(), otpType)
                            _verificationState.value = VerificationState.Error(errorMessage)
                        }
                    }

                    OtpType.RECOVERY -> {
                        // Проверьте правильность endpoint - возможно должно быть что-то другое
                        val response = RetrofitInstance.userManagementService.verifyRecoveryOtp(
                            VerifyOtpRequest(
                                email = email,
                                token = otpCode,
                                type = "recovery"
                            )
                        )

                        if (response.isSuccessful) {
                            response.body()?.let { recoveryResponse ->
                                Log.v("verifyRecoveryOtp", "Recovery OTP verified successfully")
                                _verificationState.value = VerificationState.Success(
                                    type = OtpType.RECOVERY,
                                    data = recoveryResponse
                                )
                                _recoveryState.value = RecoveryState(
                                    resetToken = recoveryResponse.reset_token,
                                    email = email
                                )
                            } ?: run {
                                _verificationState.value = VerificationState.Error("Empty recovery response")
                            }
                        } else {
                            val errorMessage = parseVerificationError(response.code(), response.message(), otpType)
                            _verificationState.value = VerificationState.Error(errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is ConnectException -> "No internet connection"
                    is SocketTimeoutException -> "Connection timeout"
                    else -> "Verification failed: ${e.message}"
                }
                _verificationState.value = VerificationState.Error(errorMessage)
                Log.e("EmailVerificationViewModel", "Exception: ${e.message}", e)
            }
        }
    }

    private fun parseVerificationError(code: Int, message: String, otpType: OtpType): String {
        return when (code) {
            400 -> when (otpType) {
                OtpType.EMAIL -> "Invalid OTP code"
                OtpType.RECOVERY -> "Invalid recovery code"
            }
            401 -> when (otpType) {
                OtpType.EMAIL -> "OTP expired or invalid"
                OtpType.RECOVERY -> "Recovery code expired or invalid"
            }
            404 -> when (otpType) {
                OtpType.EMAIL -> "Email not found"
                OtpType.RECOVERY -> "Email not found or no recovery request"
            }
            429 -> "Too many attempts. Please try again later."
            else -> "Verification failed: $message"
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
        _recoveryState.value = null
    }

    fun getResetToken(): String? {
        return _recoveryState.value?.resetToken
    }
}

enum class OtpType {
    EMAIL,
    RECOVERY
}

sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    data class Success(
        val type: OtpType,
        val data: Any? = null
    ) : VerificationState()
    data class Error(val message: String) : VerificationState()
}

data class RecoveryState(
    val resetToken: String?,
    val email: String
)