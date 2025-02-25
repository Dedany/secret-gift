package com.dedany.secretgift.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor():ViewModel() {
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


}