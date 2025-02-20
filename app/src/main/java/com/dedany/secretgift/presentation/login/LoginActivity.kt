package com.dedany.secretgift.presentation.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityLoginBinding
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.presentation.main.MainActivityViewModel

class LoginActivity : AppCompatActivity() {

    private var binding : ActivityLoginBinding? = null
    private var viewModel : LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setContentView(binding?.root)
    }
        }
