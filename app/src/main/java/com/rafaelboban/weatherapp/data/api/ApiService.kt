package com.rafaelboban.weatherapp.data.api

import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/location/search/")
    suspend fun getLocations(@Query("query") query: String): ArrayList<Location>

    @GET("/api/location/{woeid}/")
    suspend fun getWeather(@Path("woeid") woeid: String): LocationWeather

    @GET("/api/location/{woeid}/{date}")
    suspend fun getWeatherDay(@Path("woeid") woeid: Int,
                              @Path("date") date: String): LocationWeather
}