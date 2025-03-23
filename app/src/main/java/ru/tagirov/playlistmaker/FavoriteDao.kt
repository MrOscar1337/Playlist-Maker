package ru.tagirov.playlistmaker

import androidx.room.*

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavorite(trackId: Int)

    @Query("SELECT EXISTS (SELECT * FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Int): Boolean
}