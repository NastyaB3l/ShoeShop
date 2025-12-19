package com.example.shoeshop.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.VerifyOtpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmailVerificationViewModel : ViewModel() {

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    fun verifyRecoveryOtp(email: String, otp: String) {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading

            try {
                Log.d("EmailVerificationVM", "Верификация recovery OTP: $email, код: $otp")

                // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: type = "recovery"
                val request = VerifyOtpRequest(
                    email = email,
                    token = otp,
                    type = "recovery"
                )

                val response = RetrofitInstance.userManagementService.verifyOtp(request)

                Log.d("EmailVerificationVM", "Код ответа: ${response.code()}")
                Log.d("EmailVerificationVM", "Ответ: ${response.body()}")

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val accessToken = signInResponse?.access_token

                    Log.d("EmailVerificationVM", "Получен access_token: ${accessToken?.take(30)}...")

                    if (!accessToken.isNullOrEmpty()) {
                        _verificationState.value = VerificationState.Success(
                            type = OtpType.RECOVERY,
                            data = VerificationData(
                                access_token = accessToken,
                                user = signInResponse.user
                            )
                        )
                    } else {
                        _verificationState.value = VerificationState.Error("Токен не найден в ответе")
                    }
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: ""

                    Log.e("EmailVerificationVM", "Ошибка $errorCode: $errorBody")

                    val errorMessage = when (errorCode) {
                        400 -> "Неверный OTP код"
                        401 -> "Код истек или недействителен"
                        404 -> "Пользователь не найден"
                        422 -> "Неверный формат запроса"
                        429 -> "Слишком много попыток"
                        else -> "Ошибка верификации: $errorBody"
                    }

                    _verificationState.value = VerificationState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("EmailVerificationVM", "Исключение: ${e.message}", e)
                _verificationState.value = VerificationState.Error(
                    "Ошибка сети: ${e.message ?: "Неизвестная ошибка"}"
                )
            }
        }
    }

    fun verifyEmailOtp(email: String, otp: String) {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading

            try {
                val request = VerifyOtpRequest(
                    email = email,
                    token = otp,
                    type = "signup"
                )

                val response = RetrofitInstance.userManagementService.verifyOtp(request)

                if (response.isSuccessful) {
                    val signInResponse = response.body()
                    val accessToken = signInResponse?.access_token

                    if (!accessToken.isNullOrEmpty()) {
                        _verificationState.value = VerificationState.Success(
                            type = OtpType.EMAIL,
                            data = VerificationData(
                                access_token = accessToken,
                                user = signInResponse.user
                            )
                        )
                    } else {
                        _verificationState.value = VerificationState.Error("Токен не найден в ответе")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Ошибка верификации"
                    _verificationState.value = VerificationState.Error(errorBody)
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(
                    "Ошибка сети: ${e.message ?: "Неизвестная ошибка"}"
                )
            }
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
    }
}

enum class OtpType {
    EMAIL, RECOVERY
}

data class VerificationData(
    val access_token: String? = null,
    val user: com.example.shoeshop.data.model.User? = null
)

sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    data class Success(val type: OtpType, val data: VerificationData? = null) : VerificationState()
    data class Error(val message: String) : VerificationState()
}