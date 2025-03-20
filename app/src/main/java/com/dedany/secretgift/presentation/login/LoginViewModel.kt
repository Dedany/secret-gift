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
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val usersUseCase: UsersUseCase
) : ViewModel() {

    private var _isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private var _canDoLogin: MutableLiveData<Boolean> = MutableLiveData(false)
    val canDoLogin: LiveData<Boolean> = _canDoLogin

    private var _loginError: MutableLiveData<String> = MutableLiveData()
    val loginError: LiveData<String> = _loginError

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User> = _user

    private var _isLoginFormValid: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginFormValid: LiveData<Boolean> = _isLoginFormValid

    private var password: String = ""
    private var code: String = ""
    private var email: String = ""

    fun setEmail(text: String) {
        email = text.lowercase().trim()
        _canDoLogin.value = authUseCase.isLoginFormValid(email, password)
    }

    fun setPassword(text: String) {
        password = text
        _canDoLogin.value = authUseCase.isLoginFormValid(email, password)

    }

    fun login() {
        _isLoginFormValid.value = authUseCase.isLoginFormValid(email, password)
        if (_isLoginFormValid.value == true) {
            viewModelScope.launch {
                val isSuccess = authUseCase.login(email, password)
                if (isSuccess) {
                    _user.value = usersUseCase.getRegisteredUser()
                    _isLoginSuccess.value = true
                } else {
                    _loginError.value =
                        "Error en correo o contraseña" // Mostrar un mensaje de error
                    _isLoginSuccess.value = false
                }
            }
        }
    }

    fun setCode(code: String) {
        this.code = code
    }
}