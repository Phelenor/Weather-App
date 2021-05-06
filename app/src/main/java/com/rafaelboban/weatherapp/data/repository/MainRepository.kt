package com.rafaelboban.weatherapp.data.repository

import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather

class MainRepository(private val apiHelper: ApiHelper, private val dbHelper: DbHelper) {

    suspend fun getLocations(query: String) = apiHelper.getLocations(query)

    suspend fun getWeather(woeid: Int) = apiHelper.getWeather(woeid.toString())

    suspend fun getWeatherDay(woeid: Int, date: String) = apiHelper.getWeatherDay(woeid, date)

    suspend fun getAllDb() = dbHelper.getAll()

    suspend fun insertAllDb(vararg locations: Location) = dbHelper.insertAll(*locations)

    suspend fun deleteDb(location: Location) =dbHelper.delete(location)
}