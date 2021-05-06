package com.rafaelboban.weatherapp.data.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    private var INSTANCE: LocationDatabase? = null

    fun getInstance(context: Context): LocationDatabase {
        if (INSTANCE == null) {
            synchronized(LocationDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }

    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            "favorite-locations"
        ).build()
}
