package com.dedany.secretgift.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
):ViewModel() {
    private var _isRegisterSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    val isRegisterSuccessful: LiveData<Boolean> = _isRegisterSuccessful
    private var _formHasError: MutableLiveData<Boolean> = MutableLiveData()
    val formHasError: LiveData<Boolean> = _formHasError

    private var _name = MutableLiveData<String>("")
    val name: LiveData<String> = _name
    private var _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email
    private var _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password
    private var _confirmPassword = MutableLiveData<String>("")
    val confirmPassword: LiveData<String> = _confirmPassword
    private var _isTermsAccepted = MutableLiveData<Boolean>(false)
    val isTermsAccepted: LiveData<Boolean> = _isTermsAccepted


    private var _nameError: MutableLiveData<String?> = MutableLiveData()
    val nameError: MutableLiveData<String?> = _nameError
    private var _emailError: MutableLiveData<String?> = MutableLiveData()
    val emailError: MutableLiveData<String?> = _emailError
    private var _passwordError: MutableLiveData<String?> = MutableLiveData()
    val passwordError: MutableLiveData<String?> = _passwordError
    private var _confirmPasswordError: MutableLiveData<String?> = MutableLiveData()
    val confirmPasswordError: MutableLiveData<String?> = _confirmPasswordError
    private var _isTermsAcceptedError: MutableLiveData<String?> = MutableLiveData()
    val isTermsAcceptedError: MutableLiveData<String?> = _isTermsAcceptedError

    fun setName(text: String) {
        _name.value = text

        checkName(text)
    }

    private fun checkName(name: String): Boolean {
        if (authUseCase.isNameValid(name)) {
            _nameError.value = null
            return true

        } else {
            _nameError.value = "El nombre no puede estar vacío"
            return false
        }
    }



    fun setEmail(text: String) {
        _email.value = text
        checkEmail(text)
    }

    private fun checkEmail(email: String): Boolean {
        if (authUseCase.isEmailFormatValid(email)) {
            _emailError.value = null
            return true

        } else {
            _emailError.value = "El email no es válido"
            return false
        }
    }

    fun setPassword(text: String) {
        _password.value = text
        checkPasswordFormat(text)
    }

    private fun checkPasswordFormat(password: String): Boolean {
        if (authUseCase.isPasswordFormatValid(password)) {
            _passwordError.value = null
            return true
        } else {
            _passwordError.value =
                "La contraseña debe tener al menos 6 caracteres, una mayúscula, una minúscula y un número"
            return false

        }
    }

    fun setConfirmPassword(text: String) {
        _confirmPassword.value = text
        _confirmPasswordError.value?.let {
            checkConfirmPassword(it, text)
        }

    }

    private fun checkConfirmPassword(password: String, confirmPassword: String): Boolean {
        if (authUseCase.isPasswordMatching(password, confirmPassword)) {
            _confirmPasswordError.value = null
            return true

        } else {
            _confirmPasswordError.value = "Las contraseñas no coinciden"
            return false

        }
    }

    fun setTermsAccepted(checked: Boolean) {
        _isTermsAccepted.value = checked
        _isTermsAcceptedError.value =
            if (isTermsAccepted.value == true) null else "Debe aceptar los términos y condiciones"
    }

    fun register() {
        viewModelScope.launch {
            val name = name.value ?: ""

            val email = email.value ?: ""
            val password = password.value ?: ""
            val confirmPassword = confirmPassword.value ?: ""
            val isTermsAccepted = isTermsAccepted.value ?: false

            if (authUseCase.isRegisterFormValid(
                    name,
                    email,
                    password,
                    confirmPassword,
                    isTermsAccepted
                )
            ) {


                _isRegisterSuccessful.value =
                    authUseCase.register(name,  email, password)
            } else {
                _formHasError.value = true
                checkInputs()
            }


        }

    }

    private fun checkInputs() {
        _nameError.value = if (name.value.isNullOrEmpty()) "El nombre no puede estar vacío" else null
        _emailError.value = if (email.value.isNullOrEmpty()) "El email no puede estar vacío" else null
        _passwordError.value = if (password.value.isNullOrEmpty()) "La contraseña no puede estar vacía" else null
        _confirmPasswordError.value = if (confirmPassword.value.isNullOrEmpty()) "La confirmación de contraseña no puede estar vacía" else null
        _isTermsAcceptedError.value = if (!isTermsAccepted.value!!) "Debe aceptar los términos y condiciones" else null

    }
}