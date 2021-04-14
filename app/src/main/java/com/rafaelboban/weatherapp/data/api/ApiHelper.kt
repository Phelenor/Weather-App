package com.rafaelboban.weatherapp.data.api

class ApiHelper(private val apiService: ApiService) {

    suspend fun getLocations(query: String) = apiService.getLocations(query)

    suspend fun getWeather(woeid: String) = apiService.getWeather(woeid)

    suspend fun getWeatherDay(woeid: Int, date: String) = apiService.getWeatherDay(woeid, date)
}