package ru.practicum.mvvm.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEmailChanged(newValue: String) {
        updateState(email = newValue)
    }

    fun onPasswordChanged(newValue: String) {
        updateState(password = newValue)
    }

    fun enter() {
        viewModelScope.launch {
            // if email && password is OK
            _uiEvent.emit(LoginUiEvent.NavigateToNews)
        }
    }

    private fun updateState(
        email: String = _state.value.email,
        password: String = _state.value.password
    ) {
        viewModelScope.launch {
            val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isPasswordValid = password.isBlank() || password.length >= 6
            val error = if (email.isNotBlank()
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
            if (error.isNotBlank()) {
                _uiEvent.emit(LoginUiEvent.ShowError(error))
            }
            _state.emit(
                LoginState(
                    email = email,
                    password = password,
                    isEmailValid = isEmailValid,
                    isPasswordValid = isPasswordValid,
                    isEnterButtonEnabled = email.isNotBlank() && isEmailValid
                            && password.isNotBlank() && isPasswordValid,

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
)

sealed class LoginUiEvent {
    data object NavigateToNews : LoginUiEvent()
    data class ShowError(val message: String) : LoginUiEvent()
}