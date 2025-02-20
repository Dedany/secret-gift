package com.dedany.secretgift.presentation.game.viewGame

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityViewGameBinding


class ViewGameActivity : AppCompatActivity() {
    private var binding: ActivityViewGameBinding? = null
    private var viewModel: ViewGameViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ViewGameViewModel::class.java]
        setContentView(binding?.root)

    }
}