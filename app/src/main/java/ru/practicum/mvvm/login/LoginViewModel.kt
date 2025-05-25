package ru.practicum.mvvm.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    val isEmailValid: Flow<Boolean> = _email.map {
        it.isBlank() || Patterns.EMAIL_ADDRESS.matcher(it).matches()
    }
    val isPasswordValid: Flow<Boolean> = _password.map {
        it.isBlank() || it.length >= 6
    }
    val isEnterButtonEnabled =
        combine(_email, isEmailValid, _password, isPasswordValid) { email, isEmailValid, password, isPasswordValid ->
            email.isNotBlank() && isEmailValid
                    && password.isNotBlank() && isPasswordValid
        }
    val error: Flow<String> = combine(isEmailValid, isPasswordValid) { isEmailValid, isPasswordValid ->
        if ( _email.value.isNotBlank()
            && _password.value.isNotBlank()) {
            val isFormValid = isEmailValid && isPasswordValid
            if (isFormValid) {
                ""
            } else {
                "Введите корректный email и пароль от 6 символов"
            }
        } else {
            ""
        }
    }

    fun onEmailChanged(newValue: String) {
        viewModelScope.launch {
            _email.emit(newValue)
        }
    }

    fun onPasswordChanged(newValue: String) {
        viewModelScope.launch {
            _password.emit(newValue)
        }
    }

    fun enter() {

    }


}