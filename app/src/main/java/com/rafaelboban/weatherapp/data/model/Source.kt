package com.rafaelboban.weatherapp.data.model

import androidx.room.Entity
import java.io.Serializable

data class Source(
    val crawl_rate: Int,
    val slug: String,
    val title: String,
    val url: String
) : Serializable