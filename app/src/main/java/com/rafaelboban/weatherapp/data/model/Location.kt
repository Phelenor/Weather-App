package com.rafaelboban.weatherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Location(
    var latt_long: String,
    var location_type: String,
    var title: String,
    @PrimaryKey var woeid: Int
) : Serializable {
    var isRecent = false
    var id: Int? = null
    var favorite: Boolean? = null
}