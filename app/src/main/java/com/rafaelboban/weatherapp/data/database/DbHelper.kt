package com.rafaelboban.weatherapp.data.database

import com.rafaelboban.weatherapp.data.model.Location

class DbHelper(private val db: LocationDatabase) {

    suspend fun getAll() = db.locationDao().getAll()

    suspend fun insertAll(vararg locations: Location) = db.locationDao().insertAll(arrayListOf(*locations))

    suspend fun delete(location: Location) = db.locationDao().delete(location)
}
