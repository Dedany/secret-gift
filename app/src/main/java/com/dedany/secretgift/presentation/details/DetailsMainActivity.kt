package com.dedany.secretgift.presentation.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dedany.secretgift.databinding.ActivityDetailsMainBinding
import com.dedany.secretgift.domain.entities.Game
import com.dedany.secretgift.presentation.helpers.Constants
import com.dedany.secretgift.presentation.helpers.getCustomSerializable

class DetailsMainActivity : AppCompatActivity() {

    private var binding: ActivityDetailsMainBinding? = null
    private var viewModel: DetailsMainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this).get(DetailsMainViewModel::class.java)

        getIntentData()
        setUpListeners()
        setUpObservers()
    }


    private fun getIntentData() {
        if (intent.hasExtra(Constants.KEY_ACCESS_CODE)) {
            (intent?.getCustomSerializable<Game>(Constants.KEY_ACCESS_CODE))?.let {
                viewModel?.setGameValue(it)
            }
        }

        if (intent.hasExtra(Constants.KEY_GAME_POSITION)) {
            (intent?.extras?.getInt(Constants.KEY_GAME_POSITION))?.let {
                viewModel?.setPositionValue(it)
            }
        }
    }


    private fun setUpListeners() {
        binding?.buttonSave?.setOnClickListener {
            intent.apply {
                putExtra(Constants.KEY_ACCESS_CODE, viewModel?.game?.value)
                putExtra(Constants.KEY_GAME_POSITION, viewModel?.position?.value)

            }
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setUpObservers() {
        viewModel?.game?.observe(this) { game ->
            binding?.textName?.text = game.name
        }
        viewModel?.game?.observe(this) { game ->
            binding?.textStatus?.text = game.status
        }
        viewModel?.game?.observe(this) { game ->
            binding?.textGameCode?.text = game.gameCode
        }
        viewModel?.game?.observe(this) { game ->
            binding?.textMaxCost?.text = game.maxCost.toString()
        }
        viewModel?.game?.observe(this) { game ->
            binding?.textGameDate?.text = game.gameDate.toString()
        }

    }


}


