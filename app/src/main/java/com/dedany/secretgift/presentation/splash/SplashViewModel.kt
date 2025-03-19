package com.dedany.secretgift.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel(
) {

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


    //verifica si está logueado
    fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = authUseCase.isLoggedIn()
            _isLoggedIn.value = isLoggedIn
        }
    }
}

