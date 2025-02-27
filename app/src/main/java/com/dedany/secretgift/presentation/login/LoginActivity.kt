package com.dedany.secretgift.presentation.login

import android.app.Dialog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityLoginBinding
import com.dedany.secretgift.databinding.ActivityMainBinding
import com.dedany.secretgift.presentation.game.viewGame.ViewGameActivity
import com.dedany.secretgift.presentation.main.MainActivity
import com.dedany.secretgift.presentation.main.MainActivityViewModel
import com.dedany.secretgift.presentation.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private var viewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setContentView(binding?.root)

        initObservers()
        initListeners()
    }

    private fun initObservers() {
        viewModel?.isLoginSuccess?.observe(this) { isSucces ->
            if (isSucces) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel?.canDoLogin?.observe(this) { isEnabled ->
        }
        viewModel?.isLoginFormValid?.observe(this) { isValid ->
            if (!isValid) {
                Toast.makeText(
                    this@LoginActivity,
                    "Rectifica correo y contraseña",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initListeners() {
        binding?.etEmail?.doOnTextChanged { text, start, before, count ->
            viewModel?.setEmail(text.toString())
        }

        binding?.etPassword?.doOnTextChanged { text, start, before, count ->
            viewModel?.setPassword(text.toString())
        }

        binding?.btnLogin?.setOnClickListener {
            viewModel?.login()
        }

        binding?.tvLinkCreateUser?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding?.btnEvent?.setOnClickListener {
            // Crear un nuevo objeto Dialog
            val dialog = Dialog(this)

            // Configurar el layout personalizado
            dialog.setContentView(R.layout.code_input_dialog)

            // Mostrar el modal
            dialog.show()
        }
    }
}