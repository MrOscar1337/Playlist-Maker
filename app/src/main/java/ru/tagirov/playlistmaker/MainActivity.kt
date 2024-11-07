package ru.tagirov.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.light_mode_)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsButton = findViewById<Button>(R.id.button)
        val buttonClickListener : View.OnClickListener = object : View.OnClickListener{
                override fun onClick(v:View?){
                    val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(settingsIntent)
                }
    }
        settingsButton.setOnClickListener(buttonClickListener)
        val mediaButton = findViewById<Button>(R.id.button2)
        mediaButton.setOnClickListener{
            val mediaIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val searchButton = findViewById<Button>(R.id.button3)
        searchButton.setOnClickListener{
            val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }
}
}