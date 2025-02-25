package com.dedany.secretgift.presentation.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var binding : ActivityRegisterBinding? = null
    private var viewModel : RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        setContentView(binding?.root)

        initListeners()
        initObservers()


    }

    private fun initObservers() {
        viewModel?.nameError?.observe(this) {
            binding?.nameLayout?.error = it
        }
        viewModel?.emailError?.observe(this) {
            binding?.emailLayout?.error = it
        }
        viewModel?.passwordError?.observe(this) {
            binding?.passwordLayout?.error = it
        }
        viewModel?.confirmPasswordError?.observe(this) {
            binding?.confirmPasswordLayout?.error = it
        }
        viewModel?.isTermsAcceptedError?.observe(this) {
            binding?.tvCheckboxError?.isVisible = !it.isNullOrEmpty()
            binding?.tvCheckboxError?.text = it
        }
        viewModel?.isRegisterSuccessful?.observe(this) { isValid ->

            Toast.makeText(
                this,
                if (isValid) "Registro exitoso" else "Error en el registro",
                Toast.LENGTH_LONG
            ).show()
            if (isValid){
                startActivity(Intent(this, HomeActivity::class.java))
            }


        }
        viewModel?.formHasError?.observe(this) { hasError ->
            if (hasError) {
                Toast.makeText(this, "Error en el formulario", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initListeners() {
        TODO("Not yet implemented")
    }
}