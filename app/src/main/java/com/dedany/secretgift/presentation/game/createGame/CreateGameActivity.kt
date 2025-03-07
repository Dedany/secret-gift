package com.dedany.secretgift.presentation.game.createGame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityCreateGameBinding
import com.dedany.secretgift.presentation.login.LoginViewModel
import com.dedany.secretgift.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateGameActivity : AppCompatActivity() {

    private var binding: ActivityCreateGameBinding? = null
    private var viewModel: CreateGameViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[CreateGameViewModel::class.java]
        setContentView(binding?.root)


        initObservers()
        initListeners()


    }

    private fun initObservers() {
        viewModel?.isGameNameValid?.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Nombre del grupo creado correctamente", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "necesita un mínimo de 4 letras", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initListeners() {
        binding?.edNameRoom?.doOnTextChanged { text, start, before, count ->
            viewModel?.setName(text.toString())
        }
        binding?.btnCreateGame?.setOnClickListener {
            val gameName = binding?.edNameRoom?.text.toString()
            viewModel?.nameGameIsValid()


            }
        }
    }

