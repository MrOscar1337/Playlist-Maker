package ru.tagirov.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private var tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var filteredTracks: List<Track> = emptyList() // По умолчанию список пустой

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val context = parent.context
        val layoutRes = getLayoutForTheme(context)
        val view = LayoutInflater.from(context).inflate(layoutRes, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(filteredTracks[position])
    }

    override fun getItemCount(): Int = filteredTracks.size

    fun filter(query: String) {
        filteredTracks = if (query.isEmpty()) {
            emptyList() // При пустом запросе список пустой
        } else {
            tracks.filter {
                it.trackName.contains(query, ignoreCase = true) || it.artistName.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged() // Обновляем RecyclerView
    }

    private fun getLayoutForTheme(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        return if (isDarkMode) R.layout.item_track_dark else R.layout.item_track
    }
}