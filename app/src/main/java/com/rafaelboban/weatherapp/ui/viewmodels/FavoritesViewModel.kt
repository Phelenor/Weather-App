package com.rafaelboban.weatherapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

class FavoritesViewModel(val repository: MainRepository) : ViewModel() {

    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
    }

    fun getLocations() {
        viewModelScope.launch {
            val locationsResponse = repository.getFavorited()
            val fetchWeather = locationsResponse.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }
            val weathersResponse = fetchWeather.awaitAll()

            for (i in 0 until locationsResponse.size) {
                weatherMap.value!![locationsResponse[i]] = weathersResponse[i]
                weatherMap.notifyObserver()
            }
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}