package com.rafaelboban.weatherapp.data.database

import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location

class DbHelper(private val db: LocationDatabase) {

    suspend fun getLocations() = db.locationDao().getLocations()

    suspend fun getFavorites(): MutableList<Location> = db.locationDao().getFavorites()

    suspend fun getRecent(): MutableList<Location> = db.locationDao().getRecent()

    suspend fun insertFavorite(favs: MutableList<Favorite>) = db.locationDao().insertFavorite(favs)

    suspend fun insertLocation(locations: MutableList<Location>) = db.locationDao().insertLocation(locations)

    suspend fun deleteLocation(woeid: String) = db.locationDao().deleteLocation(woeid)

    suspend fun deleteFavorite(woeid: String) = db.locationDao().deleteFavorite(woeid)

    suspend fun deleteFavorites() = db.locationDao().deleteFavorites()

    fun resetKey() {
        db.openHelper.writableDatabase.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='Favorite'")
    }
}
