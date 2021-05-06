package com.rafaelboban.weatherapp.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Location(
    var latt_long: String,
    var location_type: String,
    var title: String,
    var woeid: Int
) : Serializable {
    var favorited = false
    var visited = false
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}