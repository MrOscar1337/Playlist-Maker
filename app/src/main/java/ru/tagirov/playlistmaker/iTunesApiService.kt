package ru.tagirov.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApiService {
    @GET("/search?entity=song")
    fun searchTracks(
        @Query("term") query: String
    ): Call<iTunesSearchResponse>
}