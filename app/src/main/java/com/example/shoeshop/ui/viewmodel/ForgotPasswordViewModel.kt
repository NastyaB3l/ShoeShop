package com.example.shoeshop.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoeshop.data.RetrofitInstance
import com.example.shoeshop.data.model.ForgotPasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.text.matches
import kotlin.text.toRegex

class ForgotPasswordViewModel : ViewModel() {

    private val _passwordRecoveryState =
        MutableStateFlow<PasswordRecoveryState>(PasswordRecoveryState.Idle)
    val passwordRecoveryState: StateFlow<PasswordRecoveryState> = _passwordRecoveryState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isEmailValid = MutableStateFlow(false)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid

    fun updateEmail(email: String) {
        _email.value = email
        validateEmail(email)
    }

    private fun validateEmail(email: String) {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        _isEmailValid.value = email.matches(emailPattern)
    }

    fun recoverPassword() {
        if (!_isEmailValid.value) {
            _passwordRecoveryState.value = PasswordRecoveryState.Error("Введите корректный email адрес")
            return
        }

        viewModelScope.launch {
            _passwordRecoveryState.value = PasswordRecoveryState.Loading

            try {
                Log.d("ForgotPasswordVM", "Отправка запроса восстановления для: ${_email.value}")

                // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: redirectTo = null для OTP
                val request = ForgotPasswordRequest(
                    email = _email.value,
                    redirectTo = null // Это заставляет Supabase отправлять OTP
                )

                val response = RetrofitInstance.userManagementService.recoverPassword(request)

                Log.d("ForgotPasswordVM", "Ответ: ${response.code()}")
                Log.d("ForgotPasswordVM", "Тело: ${response.body()}")

                if (response.isSuccessful) {
                    // Успех - OTP отправлен
                    _passwordRecoveryState.value = PasswordRecoveryState.Success(
                        "Код восстановления отправлен на ${_email.value}"
                    )
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: ""

                    Log.e("ForgotPasswordVM", "Ошибка $errorCode: $errorBody")

                    val errorMessage = when (errorCode) {
                        400 -> "Неверный запрос. Проверьте email."
                        422 -> "Неверный формат email."
                        429 -> "Слишком много попыток. Подождите 60 секунд."
                        else -> "Ошибка: ${response.message()}"
                    }

                    _passwordRecoveryState.value = PasswordRecoveryState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("ForgotPasswordVM", "Исключение: ${e.message}", e)
                _passwordRecoveryState.value = PasswordRecoveryState.Error(
                    when (e) {
                        is ConnectException -> "Нет подключения к интернету"
                        is SocketTimeoutException -> "Таймаут соединения"
                        else -> "Ошибка: ${e.message ?: "Неизвестная ошибка"}"
                    }
                )
            }
        }
    }

    fun resetState() {
        _passwordRecoveryState.value = PasswordRecoveryState.Idle
    }
}

sealed class PasswordRecoveryState {
    object Idle : PasswordRecoveryState()
    object Loading : PasswordRecoveryState()
    data class Success(val message: String) : PasswordRecoveryState()
    data class Error(val message: String) : PasswordRecoveryState()
}