import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstproject.data.model.SignUpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.shoeshop.data.RetrofitInstance

class SignUpViewModel : ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    // Два варианта метода signUp для гибкости

    // 1. Метод с отдельными параметрами (для использования в UI)
    fun signUp(name: String, email: String, password: String) {
        // Создаем SignUpRequest с именем
        val signUpRequest = SignUpRequest(
            name = name,
            email = email,
            password = password
        )
        signUp(signUpRequest)
    }

    // 2. Существующий метод с SignUpRequest (для обратной совместимости)
    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                Log.d("SignUpViewModel", "Attempting registration for: ${signUpRequest.email}")

                val response = RetrofitInstance.userManagementService.signUp(signUpRequest)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.v("signUp", "Registration successful! User id: ${it.id}, name: ${it.name}")
                        _signUpState.value = SignUpState.Success
                    } ?: run {
                        _signUpState.value = SignUpState.Error("Empty response from server")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Неверный email или пароль"
                        422 -> "Некорректный формат email"
                        429 -> "Слишком много запросов. Попробуйте позже"
                        409 -> "Пользователь с таким email уже существует"
                        else -> "Ошибка регистрации: ${response.code()}"
                    }
                    _signUpState.value = SignUpState.Error(errorMessage)
                    Log.e("SignUpViewModel", "Registration failed: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.ConnectException -> "Нет подключения к интернету"
                    is java.net.SocketTimeoutException -> "Таймаут подключения"
                    is java.net.UnknownHostException -> "Сервер недоступен"
                    else -> "Ошибка сети: ${e.message}"
                }
                _signUpState.value = SignUpState.Error(errorMessage)
                Log.e("SignUpViewModel", "Exception during registration: ${e.message}", e)
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}