package com.rafaelboban.weatherapp.data.database

import com.rafaelboban.weatherapp.data.model.Location

class DbHelper(private val db: LocationDatabase) {

    suspend fun getAll() = db.locationDao().getAll()

    suspend fun getFavorited(): MutableList<Location> = db.locationDao().getFavorited()

    suspend fun getRecentFive(): MutableList<Location> = db.locationDao().getRecentFive()

    suspend fun getCount(): Int = db.locationDao().getCount()

//    suspend fun filterRecent() = db.locationDao().filterRecent()

    suspend fun insertAll(vararg locations: Location) = db.locationDao().insertAll(arrayListOf(*locations))

    suspend fun delete(location: Location) = db.locationDao().delete(location)
}
