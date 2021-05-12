package com.rafaelboban.weatherapp.data.repository

import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location

class MainRepository(private val apiHelper: ApiHelper, private val dbHelper: DbHelper) {

    suspend fun getLocations(query: String) = apiHelper.getLocations(query)

    suspend fun getWeather(woeid: Int) = apiHelper.getWeather(woeid.toString())

    suspend fun getWeatherDay(woeid: Int, date: String) = apiHelper.getWeatherDay(woeid, date)

    suspend fun getLocationsDb() = dbHelper.getLocations()

    suspend fun getFavoritesDb() = dbHelper.getFavorites()

    suspend fun getRecentDb(): MutableList<Location> = dbHelper.getRecent()

    suspend fun insertLocation(vararg locations: Location) = dbHelper.insertLocation(arrayListOf(*locations))

    suspend fun insertFavorite(vararg favs: Favorite) = dbHelper.insertFavorite(arrayListOf(*favs))

    suspend fun deleteLocationDB(woeid: String) = dbHelper.deleteLocation(woeid)

    suspend fun deleteFavoriteDb(woeid: String) = dbHelper.deleteFavorite(woeid)

    suspend fun deleteFavorites() = dbHelper.deleteFavorites()

    suspend fun deleteRecent() = dbHelper.deleteRecent()

    suspend fun resetKey() = dbHelper.resetKey()

}