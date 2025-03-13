package ru.tagirov.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.io.IOException

class PlayerActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var isPlaying = false
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutRes = getLayoutForTheme()
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            stopPlayback()
            finish()
        }

        val track = intent.getParcelableExtra<Track>("TRACK")
        if (track != null) {
            displayTrackDetails(track)
            setupMediaPlayer(track.previewUrl)
        }
    }

    private fun setupMediaPlayer(previewUrl: String?) {
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())
        val playButton = findViewById<MaterialButton>(R.id.playButton)
        val progressTime = findViewById<TextView>(R.id.progressTime)

        playButton.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback(previewUrl)
            }
        }

        runnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    currentPosition = mediaPlayer.currentPosition
                    progressTime.text = formatTime(currentPosition)
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun startPlayback(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return

        try {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.seekTo(currentPosition) // Воспроизведение с сохраненной позиции
                    mediaPlayer.start()
                    isPlaying = true
                    findViewById<MaterialButton>(R.id.playButton).setIconResource(R.drawable.ic_pause)
                    handler.post(runnable)
                }
            } else {
                mediaPlayer.start()
                isPlaying = true
                findViewById<MaterialButton>(R.id.playButton).setIconResource(R.drawable.ic_pause)
                handler.post(runnable)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun pausePlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
            findViewById<MaterialButton>(R.id.playButton).setIconResource(R.drawable.ic_play)
            handler.removeCallbacks(runnable)
        }
    }

    private fun stopPlayback() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            isPlaying = false
            findViewById<MaterialButton>(R.id.playButton).setIconResource(R.drawable.ic_play)
            handler.removeCallbacks(runnable)
            findViewById<TextView>(R.id.progressTime).text = "00:00"
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }

    private fun displayTrackDetails(track: Track) {
        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.collectionValue).text = track.collectionName ?: ""
        findViewById<TextView>(R.id.releaseValue).text = track.getFormattedReleaseDate() ?: ""
        findViewById<TextView>(R.id.genreValue).text = track.primaryGenreName ?: ""
        findViewById<TextView>(R.id.countryValue).text = track.country ?: ""
        findViewById<TextView>(R.id.timeValue).text = track.getFormattedTrackTime(track.trackTimeMillis)

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