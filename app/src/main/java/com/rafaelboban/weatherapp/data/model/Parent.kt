package com.rafaelboban.weatherapp.data.model

import androidx.room.Entity
import java.io.Serializable

data class Parent(
    val latt_long: String,
    val location_type: String,
    val title: String,
    val woeid: Int
) : Serializable