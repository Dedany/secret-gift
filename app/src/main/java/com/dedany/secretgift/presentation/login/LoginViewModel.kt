package com.dedany.secretgift.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val usersUseCase: UsersUseCase
) : ViewModel() {

    private var _email: MutableLiveData<String> = MutableLiveData("")
    val email: LiveData<String> = _email

    private var _password: MutableLiveData<String> = MutableLiveData("")
    val password: LiveData<String> = _password

    private var _isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private var _canDoLogin: MutableLiveData<Boolean> = MutableLiveData(false)
    val canDoLogin: LiveData<Boolean> = _canDoLogin

    private var _loginError: MutableLiveData<String> = MutableLiveData()
    val loginError: LiveData<String> = _loginError

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private val _isResetPassworEmailSent: MutableLiveData<Boolean> = MutableLiveData()
    val isResetPassworEmailSent: LiveData<Boolean> = _isResetPassworEmailSent

    private var _isLoginFormValid: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginFormValid: LiveData<Boolean> = _isLoginFormValid

    private var code: String = ""

    fun setEmail(text: String) {
        _email.value = text.lowercase().trim()
        _canDoLogin.value = authUseCase.isLoginFormValid(_email.value ?: "", _password.value ?: "")
    }

    fun setPassword(text: String) {
        _password.value = text
        _canDoLogin.value = authUseCase.isLoginFormValid(_email.value ?: "", _password.value ?: "")
    }

    fun login() {
        _isLoginFormValid.value = authUseCase.isLoginFormValid(_email.value ?: "", _password.value ?: "")
        if (_isLoginFormValid.value == true) {
            viewModelScope.launch {
                try {
                    val isSuccess = authUseCase.login(_email.value ?: "", _password.value ?: "")
                    if (isSuccess) {
                        _user.value = usersUseCase.getRegisteredUser()
                        _isLoginSuccess.value = true
                    } else {
                        showLoginError("Error en correo o contraseña")
                    }
                } catch (e: Exception) {
                    showLoginError(e.message ?: "Error desconocido")
                }
            }
        }
    }

    private fun showLoginError(message: String) {
        _loginError.value = message
        _isLoginSuccess.value = false
    }

    fun setCode(code: String) {
        this.code = code
    }

    fun resetPassword() {
        val email=_email.value ?: ""
        if (authUseCase.isEmailFormatValid(email)) {
            viewModelScope.launch {
              _isResetPassworEmailSent.value =  authUseCase.sendResetPasswordEmail(email)
            }
        } else {
            _loginError.value = "Formato de correo incorrecto"
        }
    }
}
