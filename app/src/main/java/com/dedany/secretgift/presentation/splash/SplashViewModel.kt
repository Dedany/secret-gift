package com.dedany.secretgift.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel(
) {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _nextActivity = MutableLiveData<Class<*>>()
    val nextActivity: LiveData<Class<*>> = _nextActivity


    //verifica si está logueado
    suspend fun checkLoginStatus() {
        val isLoggedIn = authUseCase.isLoggedIn()
        _nextActivity.value = if (isLoggedIn) {
            MainActivity::class.java
        } else {
            LoginActivity::class.java
        }
    }


}

