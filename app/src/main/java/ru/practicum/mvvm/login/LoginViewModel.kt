package ru.practicum.mvvm.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(newValue: String) {
        updateState(email = newValue)
    }

    fun onPasswordChanged(newValue: String) {
        updateState(password = newValue)
    }

    fun enter() {

    }

    private fun updateState(
        email: String = _state.value.email,
        password: String = _state.value.password
    ) {
        viewModelScope.launch {
            val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValid = password.isBlank() || password.length >= 6
            _state.emit(
                LoginState(
                    email = email,
                    password = password,
                    isEmailValid = isEmailValid,
                    isPasswordValid = isPasswordValid,
                    isEnterButtonEnabled = email.isNotBlank() && isEmailValid
                            && password.isNotBlank() && isPasswordValid,
                    error = if (email.isNotBlank()
                        && password.isNotBlank()
                    ) {
                        val isFormValid = isEmailValid && isPasswordValid
                        if (isFormValid) {
                            ""
                        } else {
                            "Введите корректный email и пароль от 6 символов"
                        }
                    } else {
                        ""
                    }
                )
            )
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isEnterButtonEnabled: Boolean = false,
    val error: String = ""
)