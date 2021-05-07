package com.rafaelboban.weatherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var woeid: Int
) : Serializable