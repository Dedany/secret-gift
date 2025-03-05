package com.dedany.secretgift.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.entities.RegisteredUser
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

    private var _isLoginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess

    private var _canDoLogin: MutableLiveData<Boolean> = MutableLiveData(false)
    val canDoLogin: LiveData<Boolean> = _canDoLogin

    private val _registeredUser: MutableLiveData<RegisteredUser> = MutableLiveData()
    val registeredUser: LiveData<RegisteredUser> = _registeredUser

    private var _isLoginFormValid: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginFormValid: LiveData<Boolean> = _isLoginFormValid


    private var email: String = ""
    private var password: String = ""
    private var code: String = ""

    fun setEmail(text: String) {
        email = text
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
                _isLoginSuccess.value = isSuccess
                if (isSuccess) {
                    _registeredUser.value = usersUseCase.getRegisteredUser()
                }
            }
        }
    }

    fun setCode(code: String) {
        this.code = code
    }
}