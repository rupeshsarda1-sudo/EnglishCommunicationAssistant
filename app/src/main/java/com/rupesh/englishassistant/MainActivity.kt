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
        else Snackbar.make(binding.root, "Microphone permission is required for voice input.",
            Snackbar.LENGTH_INDEFINITE)
            .setAction("Grant") { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        speechManager = SpeechInputManager(this)
        setupSpeechManager()
        setupObservers()
        setupClickListeners()
    }

    private fun setupSpeechManager() {
        speechManager.setCallbacks(
            onResult = { text -> viewModel.processInput(text) },
            onError = { error -> viewModel.setError(error) },
            onStart = { viewModel.setListening() },
            onStop = { }
        )
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is AssistantState.Idle -> showIdleState()
                is AssistantState.Listening -> showListeningState()
                is AssistantState.Processing -> showProcessingState()
                is AssistantState.Result -> showResultState(state.result)
                is AssistantState.Error -> showErrorState(state.message)
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabMic.setOnClickListener { handleMicClick() }
        binding.btnReset.setOnClickListener {
            speechManager.stopListening()
            viewModel.reset()
        }
        binding.btnCopyCasual.setOnClickListener {
            copyToClipboard(binding.tvCasualSuggestion.text.toString())
        }
        binding.btnCopyProfessional.setOnClickListener {
            copyToClipboard(binding.tvProfessionalSuggestion.text.toString())
        }
        binding.btnShareCasual.setOnClickListener {
            shareText(binding.tvCasualSuggestion.text.toString())
        }
        binding.btnShareProfessional.setOnClickListener {
            shareText(binding.tvProfessionalSuggestion.text.toString())
        }
        binding.btnTypeInput.setOnClickListener { showTypeInputDialog() }
    }

    private fun handleMicClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
            fabMic.text = "🎤 Speak"
            cardResult.visibility = View.GONE
            btnReset.visibility = View.GONE
        }
    }

    private fun showListeningState() {
        binding.apply {
            tvStatusMessage.text = "🎧 Listening... Speak now!"
            fabMic.text = "⏹ Stop"
            cardResult.visibility = View.GONE
        }
    }

    private fun showProcessingState() {
        binding.apply {
            tvStatusMessage.text = "⏳ Processing..."
            fabMic.text = "🎤 Speak"
        }
    }

    private fun showResultState(result: TranslationResult) {
        binding.apply {
            tvStatusMessage.text = "Translation ready!"
            tvDetectedInput.text = "🎧 Detected: \"${result.originalText}\""
            tvDetectedLanguage.text = "🌐 Language: ${result.detectedLanguage}"
            tvCasualSuggestion.text = result.casualEnglish
            tvProfessionalSuggestion.text = result.professionalEnglish
            if (result.meaning.isNotEmpty()) {
                tvMeaning.text = "🌐 Meaning: ${result.meaning}"
                tvMeaning.visibility = View.VISIBLE
            } else {
                tvMeaning.visibility = View.GONE
            }
            if (result.alternative.isNotEmpty()) {
                tvAlternative.text = "🔁 Alternative: ${result.alternative}"
                tvAlternative.visibility = View.VISIBLE
            } else {
                tvAlternative.visibility = View.GONE
            }
            cardResult.visibility = View.VISIBLE
            btnReset.visibility = View.VISIBLE
            fabMic.text = "🎤 Speak"
        }
    }

    private fun showErrorState(message: String) {
        binding.apply {
            tvStatusMessage.text = "Error occurred"
            fabMic.text = "🎤 Speak"
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("English Suggestion", text))
        Toast.makeText(this, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun showTypeInputDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Type in Hindi, Marathi or Hinglish..."
        android.app.AlertDialog.Builder(this)
            .setTitle("Type your sentence")
            .setView(input)
            .setPositiveButton("Translate") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) viewModel.processInput(text)
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
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.destroy()
    }
}
