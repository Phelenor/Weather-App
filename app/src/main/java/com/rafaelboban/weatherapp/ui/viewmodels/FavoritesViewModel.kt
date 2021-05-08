package com.rafaelboban.weatherapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.ApiService
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.*

class FavoritesViewModel(val repository: MainRepository) : ViewModel() {

    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
    }

    fun getLocations() {
        viewModelScope.launch {
            val favorites = repository.getFavoritesDb()
            repository.deleteFavorites()
            repository.resetKey()
            val favs = favorites.map {
                Favorite(null, it.woeid)
            }
            for (fav in favs)
                repository.insertFavorite(fav)


            val fetchWeather = favorites.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }
            val weathersResponse = fetchWeather.awaitAll()

            for (i in 0 until favorites.size) {
                favorites[i].favorite = true
                weatherMap.value!![favorites[i]] = weathersResponse[i]
                weatherMap.notifyObserver()
            }
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}