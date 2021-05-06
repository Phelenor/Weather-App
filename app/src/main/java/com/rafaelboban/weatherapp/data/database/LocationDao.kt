package com.rafaelboban.weatherapp.data.database

import androidx.room.*
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather

@Dao
interface LocationDao {
    @Query("SELECT * FROM Location")
    suspend fun getAll(): MutableList<Location>

    @Insert
    suspend fun insertAll(locations: MutableList<Location>)

    @Delete
    suspend fun delete(location: Location)
}
