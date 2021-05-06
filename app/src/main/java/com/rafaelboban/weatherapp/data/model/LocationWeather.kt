package com.rafaelboban.weatherapp.data.model

import java.io.Serializable

data class LocationWeather(
    val consolidated_weather: ArrayList<ConsolidatedWeather>,
    val latt_long: String,
    val location_type: String,
    val parent: Parent,
    val sources: ArrayList<Source>,
    val sun_rise: String,
    val sun_set: String,
    val time: String,
    val timezone: String,
    val timezone_name: String,
    val title: String,
    val woeid: Int
) : Serializable