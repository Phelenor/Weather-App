package com.rafaelboban.weatherapp.data.database

import androidx.room.*
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather

@Dao
interface LocationDao {
    @Query("SELECT * FROM Location")
    suspend fun getAll(): MutableList<Location>

    @Query("SELECT * FROM Location WHERE favorited = 1")
    suspend fun getFavorited(): MutableList<Location>

    @Query("SELECT COUNT(*) FROM Location")
    suspend fun getCount(): Int

    @Query("SELECT * FROM Location WHERE visited = 1 ORDER BY id DESC LIMIT 5")
    suspend fun getRecentFive(): MutableList<Location>

//    @Query("DELETE FROM Location WHERE favorited = 0 LIMIT 10")
//    suspend fun filterRecent()

    @Insert
    suspend fun insertAll(locations: MutableList<Location>)

    @Delete
    suspend fun delete(location: Location)
}
