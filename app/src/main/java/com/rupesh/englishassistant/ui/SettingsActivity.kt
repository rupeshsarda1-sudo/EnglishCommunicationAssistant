package com.rupesh.englishassistant.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rupesh.englishassistant.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Settings"

        // Load saved preferences
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        binding.switchAutoDetect.isChecked = prefs.getBoolean("auto_detect", true)
        binding.etApiKey.setText(prefs.getString("gemini_api_key", ""))

        binding.btnSave.setOnClickListener {
            prefs.edit().apply {
                putBoolean("auto_detect", binding.switchAutoDetect.isChecked)
                putString("gemini_api_key", binding.etApiKey.text.toString().trim())
                apply()
            }
            android.widget.Toast.makeText(this, "Settings saved!", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressed(); return true }
}
