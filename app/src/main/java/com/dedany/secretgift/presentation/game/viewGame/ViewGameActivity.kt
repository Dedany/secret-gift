package com.dedany.secretgift.presentation.game.viewGame

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dedany.secretgift.R
import com.dedany.secretgift.databinding.ActivityViewGameBinding
import com.dedany.secretgift.databinding.RegisterGamePlayerBinding
import com.dedany.secretgift.domain.entities.User
import com.dedany.secretgift.presentation.helpers.Constants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ViewGameActivity : AppCompatActivity() {
    private var binding: ActivityViewGameBinding? = null
    private var viewModel: ViewGameViewModel? = null
    private lateinit var playersAdapter: PlayersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                resources.getColor(R.color.transparent, null),
            )
        )
        binding = ActivityViewGameBinding.inflate(layoutInflater)


        binding?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it.root) { v, insets ->
                val bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(
                    left = bars.left,
                    right = bars.right,
                    bottom = bars.bottom,
                )
                WindowInsetsCompat.CONSUMED
            }
        }
        binding?.iconBack?.setOnClickListener {
            finish()
        }


        viewModel = ViewModelProvider(this)[ViewGameViewModel::class.java]
        setContentView(binding?.root)

        setAdapter()
        loadGame()
        setObservers()
        initAd()
    }

    private fun setAdapter() {
        playersAdapter = PlayersAdapter(
            onSendEmail = { player, position ->
                showSendEmailDialog(player)
            }
        )
        binding?.playersRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@ViewGameActivity)
            adapter = playersAdapter
        }

    }

    private fun showSendEmailDialog(player: User) {
        val dialogBinding = RegisterGamePlayerBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)


        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = resources.getDimensionPixelSize(R.dimen.dialog_height)
        dialog.window?.setLayout(width, height)
        dialog.window?.setGravity(Gravity.CENTER)

        dialogBinding.btnConfirm.text = getString(R.string.send)
        dialogBinding.tvAddPlayer.text = getString(R.string.send_email)
        dialogBinding.nameEditText.setText(player.name)
        dialogBinding.nameEditText.apply {
            isFocusable = false
            isCursorVisible = false
            isClickable = false

            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        dialogBinding.emailEditText.setText(player.email)

        dialogBinding.btnConfirm.setOnClickListener {

            val newEmail = dialogBinding.emailEditText.text.toString().trim()

            if (newEmail.isNotEmpty()) {
                viewModel?.sendMailToPlayer(player.id, newEmail)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "El email no puede estar vacío", Toast.LENGTH_LONG).show()
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadGame() {
        val intent = intent
        if (intent.hasExtra(Constants.KEY_ACCESS_CODE)) {
            val gameCode = intent.getStringExtra(Constants.KEY_ACCESS_CODE)
            if (gameCode != null) {
                viewModel?.fetchGaMeData(gameCode)
            } else {
                Toast.makeText(this, "Error al cargar el juego", Toast.LENGTH_LONG).show()
                finish()
            }
        }


    }

    private fun setObservers() {

        viewModel?.isLoading?.observe(this) {
            if (it) {
                binding?.loader?.isVisible = true
                binding?.coordinatorLayout?.isVisible = false
                binding?.viewGameSettings?.isVisible = false
            } else {
                binding?.loader?.isVisible = false
                binding?.coordinatorLayout?.isVisible = true
                binding?.viewGameSettings?.isVisible = true
            }
        }

        viewModel?.gameCodeError?.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            finish()
        }


        viewModel?.game?.observe(this) { gameData ->
            binding?.tvGameName?.text = gameData.name

            binding?.currentPlayerName?.text = gameData.currentPlayer
            binding?.matchedPlayerName?.text = gameData.matchedPlayer
            binding?.tvPlayersNumber?.text = (gameData.players.size + 1).toString()
            binding?.tvMinMoney?.text = gameData.minCost.toString()

            playersAdapter?.submitList(gameData.players)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding?.tvGameDate?.text = dateFormat.format(gameData.gameDate)
        }
        viewModel?.formattedMaxCost?.observe(this) { formattedMaxCost ->
            if (formattedMaxCost == "∞") {
                binding?.tvMaxMoney?.text = ""
                binding?.tvMaxMoney?.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.infinite),
                    null,
                    null,
                    null
                )
            } else {
                binding?.tvMaxMoney?.text = formattedMaxCost
                binding?.tvMaxMoney?.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
            }
        }
        viewModel?.isMailSent?.observe(this) {
            if (it) {
                Toast.makeText(this, "Mail enviado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Error al enviar el mail", Toast.LENGTH_LONG).show()
            }
        }


    }

    fun initAd() {
        //iniciar adMob
        MobileAds.initialize(this) { initialAd ->
            val statusMap = initialAd.adapterStatusMap
            for ((adapter, satatus) in statusMap) {
                Log.d("AdMob", "Adapter: $adapter Status: ${satatus.description}")
            }
        }
        binding?.adView?.apply {
            //asigna tamaño

            // Carga el anuncio
            val adRequest = AdRequest.Builder().setContentUrl("https://www.amazon.es")
            loadAd(adRequest.build())
        }


    }
}