package com.rupesh.englishassistant

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.rupesh.englishassistant.databinding.ActivityMainBinding
import com.rupesh.englishassistant.model.TranslationResult
import com.rupesh.englishassistant.service.SpeechInputManager
import com.rupesh.englishassistant.ui.HistoryActivity
import com.rupesh.englishassistant.ui.SettingsActivity
import com.rupesh.englishassistant.viewmodel.AssistantState
import com.rupesh.englishassistant.viewmodel.AssistantViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AssistantViewModel by viewModels()
    private lateinit var speechManager: SpeechInputManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startListening()
        else Snackbar.make(binding.root, "Microphone permission is required for voice input.", Snackbar.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        speechManager = SpeechInputManager(this)
        setupSpeechCallbacks()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupSpeechCallbacks() {
        speechManager.setCallbacks(
            onResult = { spokenText ->
                runOnUiThread { viewModel.onSpeechReceived(spokenText) }
            },
            onError = { errorMessage ->
                runOnUiThread { viewModel.onError(errorMessage) }
            },
            onStart = {
                runOnUiThread { viewModel.onListeningStarted() }
            },
            onStop = {}
        )
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is AssistantState.Idle -> showIdleState()
                is AssistantState.Listening -> showListeningState()
                is AssistantState.Processing -> showProcessingState()
                is AssistantState.ResultReady -> showResult(state.result)
                is AssistantState.Error -> showError(state.message)
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabMic.setOnClickListener { checkPermissionAndListen() }
        binding.btnCopyCasual.setOnClickListener { copyToClipboard(binding.tvCasualSuggestion.text.toString(), "Casual") }
        binding.btnCopyProfessional.setOnClickListener { copyToClipboard(binding.tvProfessionalSuggestion.text.toString(), "Professional") }
        binding.btnShareCasual.setOnClickListener { shareText(binding.tvCasualSuggestion.text.toString()) }
        binding.btnReset.setOnClickListener { viewModel.resetState() }
        binding.btnTypedInput.setOnClickListener { showTypedInputDialog() }
    }

    private fun checkPermissionAndListen() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> startListening()
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                Snackbar.make(binding.root, "Microphone access needed to hear your speech.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant") { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                    .show()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startListening() {
        if (!speechManager.isAvailable()) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }
        speechManager.startListening()
    }

    private fun showIdleState() {
        binding.apply {
            tvStatusMessage.text = "Tap the mic and speak in Hindi, Marathi, or Hinglish"
            fabMic.setImageResource(R.drawable.ic_mic)
            progressBar.visibility = View.GONE
            cardResult.visibility = View.GONE
            lottieListening.visibility = View.GONE
            btnReset.visibility = View.GONE
        }
    }

    private fun showListeningState() {
        binding.apply {
            tvStatusMessage.text = "🎧 Listening... Speak now!"
            fabMic.setImageResource(R.drawable.ic_mic_active)
            lottieListening.visibility = View.VISIBLE
            lottieListening.playAnimation()
            cardResult.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun showProcessingState() {
        binding.apply {
            tvStatusMessage.text = "⚡ Processing your speech..."
            lottieListening.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            cardResult.visibility = View.GONE
        }
    }

    private fun showResult(result: TranslationResult) {
        binding.apply {
            progressBar.visibility = View.GONE
            lottieListening.visibility = View.GONE
            cardResult.visibility = View.VISIBLE
            btnReset.visibility = View.VISIBLE

            tvDetectedInput.text = "🎧 "${result.detectedInput}""
            tvDetectedLanguage.text = "🌐 ${result.detectedLanguage} • ${result.context.name.lowercase().replaceFirstChar { it.uppercase() }}"
            tvCasualSuggestion.text = result.casualSuggestion
            tvProfessionalSuggestion.text = result.professionalSuggestion

            if (result.alternativeSuggestion.isNotEmpty()) {
                tvAlternative.text = "🔁 ${result.alternativeSuggestion}"
                tvAlternative.visibility = View.VISIBLE
            } else {
                tvAlternative.visibility = View.GONE
            }

            if (result.meaning.isNotEmpty()) {
                tvMeaning.text = "📝 ${result.meaning}"
                tvMeaning.visibility = View.VISIBLE
            } else {
                tvMeaning.visibility = View.GONE
            }

            tvStatusMessage.text = "✅ Suggestions ready!"
        }
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.lottieListening.visibility = View.GONE
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Try Again") { checkPermissionAndListen() }
            .show()
        viewModel.resetState()
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(this, "$label suggestion copied!", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun showTypedInputDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Type in Hindi/Marathi/Hinglish..."
        android.app.AlertDialog.Builder(this)
            .setTitle("Type your sentence")
            .setView(editText)
            .setPositiveButton("Translate") { _, _ ->
                val input = editText.text.toString().trim()
                if (input.isNotEmpty()) viewModel.onSpeechReceived(input)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> { startActivity(Intent(this, HistoryActivity::class.java)); true }
            R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.destroy()
    }
}
