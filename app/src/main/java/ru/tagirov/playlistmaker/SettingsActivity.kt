package ru.tagirov.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutRes = getLayoutForTheme()
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)


        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)

        themeSwitch.isChecked = isDarkMode

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveThemePreference(isChecked)

            recreate()
        }

        val intent = Intent("com.example.UPDATE_THEME")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun getLayoutForTheme(): Int {
        val isDarkMode = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("DARK_MODE", false)
        return if (isDarkMode) R.layout.settings_dark else R.layout.settings
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
        editor.putBoolean("DARK_MODE", isDarkMode)
        editor.apply()
    }
}