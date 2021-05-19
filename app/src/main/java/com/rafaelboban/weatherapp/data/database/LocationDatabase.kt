package com.rafaelboban.weatherapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location

@Database(entities = [Location::class, Favorite::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
