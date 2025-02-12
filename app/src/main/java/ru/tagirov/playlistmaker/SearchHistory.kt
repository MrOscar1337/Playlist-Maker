package ru.tagirov.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        val json = gson.toJson(history)
        sharedPreferences.edit().putString("history", json).apply()
    }

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString("history", null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove("history").apply()
    }
}