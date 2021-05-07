package com.rafaelboban.weatherapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locations: MutableList<Location>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: MutableList<Favorite>)

    @Query("SELECT * FROM Location")
    suspend fun getLocations(): MutableList<Location>

    @Query("SELECT Favorite.woeid, Favorite.id, latt_long, location_type, title, isRecent, favorite FROM Favorite JOIN Location ON Favorite.woeid=Location.woeid")
    suspend fun getFavorites(): MutableList<Location>

    @Query("SELECT * FROM Location WHERE isRecent = 1")
    suspend fun getRecent(): MutableList<Location>

    @Query("SELECT COUNT(*) FROM Location")
    suspend fun getCount(): Int

    @Query("DELETE FROM Location WHERE woeid = :woeid")
    suspend fun deleteLocation(woeid: String)

    @Query("DELETE FROM Favorite WHERE woeid = :woeid")
    suspend fun deleteFavorite(woeid: String)

    @Query("DELETE FROM Favorite")
    suspend fun deleteFavorites()

}
