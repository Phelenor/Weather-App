package com.rafaelboban.weatherapp.data.repository

import com.rafaelboban.weatherapp.data.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getLocations(query: String) = apiHelper.getLocations(query)

    suspend fun getWeather(woeid: Int) = apiHelper.getWeather(woeid.toString())

    suspend fun getWeatherDay(woeid: Int, date: String) = apiHelper.getWeatherDay(woeid, date)
}