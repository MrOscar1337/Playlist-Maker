package ru.tagirov.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutRes = getLayoutForTheme()
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val track = intent.getParcelableExtra<Track>("TRACK")
        if (track != null) {
            displayTrackDetails(track)
        }
    }

    private fun displayTrackDetails(track: Track) {
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.collectionName).text = track.collectionName ?: ""
        findViewById<TextView>(R.id.releaseDate).text = track.releaseDate ?: ""
        findViewById<TextView>(R.id.primaryGenreName).text = track.primaryGenreName ?: ""
        findViewById<TextView>(R.id.country).text = track.country ?: ""
        findViewById<TextView>(R.id.trackTime).text = track.getFormattedTrackTime(track.trackTimeMillis)

        val coverArtwork = findViewById<ImageView>(R.id.coverArtwork)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .into(coverArtwork)
    }

    private fun getLayoutForTheme(): Int {
        val isDarkMode = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("DARK_MODE", false)
        return if (isDarkMode) R.layout.audioplayer_dark else R.layout.audioplayer
    }
}