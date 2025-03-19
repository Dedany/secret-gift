package com.dedany.secretgift.presentation.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityRegisterBinding
import com.dedany.secretgift.presentation.login.LoginActivity
import com.dedany.secretgift.presentation.main.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var binding : ActivityRegisterBinding? = null
    private var viewModel : RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                resources.getColor(R.color.transparent, null),
            )
        )
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        setContentView(binding?.root)

        initListeners()
        initObservers()
        initAd()


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
            binding?.tvCheckboxError?.isInvisible = it.isNullOrEmpty()
            binding?.tvCheckboxError?.text = it
        }
        viewModel?.isRegisterSuccessful?.observe(this) { isValid ->

            Toast.makeText(
                this,
                if (isValid) "Registro exitoso" else "Error en el registro",
                Toast.LENGTH_LONG
            ).show()
            if (isValid){
                startActivity(Intent(this, LoginActivity::class.java))
            }


        }
        viewModel?.formHasError?.observe(this) { hasError ->
            if (hasError) {
                Toast.makeText(this, "Error en el formulario", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun initListeners() {
        binding?.iconBack?.setOnClickListener {
            finish()}

        with(binding) {
            this?.nameEditText?.doOnTextChanged  { text, start, before, count ->
                clearErrorState(this.nameLayout)
                viewModel?.setName(text?.toString() ?: "")
            }
            this?.nameEditText?.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    viewModel?.setName(nameEditText.text?.toString() ?: "")
                }
            }

            this?.emailEditText?.doOnTextChanged { text, start, before, count ->
                clearErrorState(this.emailLayout)
                viewModel?.setEmail(text.toString())
            }
            this?.passwordEditText?.doOnTextChanged { text, start, before, count ->
                clearErrorState(this.passwordLayout)
                viewModel?.setPassword(text.toString())
            }
            this?.confirmPasswordEditText?.doOnTextChanged { text, start, before, count ->
                clearErrorState(this.confirmPasswordLayout)
                viewModel?.setConfirmPassword(text.toString())
            }
            this?.checkboxPrivacyPolicy?.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel?.setTermsAccepted(isChecked)
            }
            this?.registerButton?.setOnClickListener {
                viewModel?.register()
            }

        }
    }
    private fun clearErrorState(layout: TextInputLayout?) {
        layout?.error = null
    }
    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }
        binding?.adView?.let { adView ->

            //asigna tamaño
            /*adView.setAdSize(adSize)

            // Configurar el ID
            adView.adUnitId =
                getString(R.string.admob_banner_id)*/

            // Carga el anuncio
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } ?: run {
            Log.e("AdMob", "adView es nulo.no hay anuncio")
        }
    }
}