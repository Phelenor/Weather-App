package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.*
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.*
import java.util.*

class SearchViewModel(val repository: MainRepository) : ViewModel() {

    var jobLoc: Job? = null

    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
    }

    fun getLocations(query: String) {
        jobLoc = viewModelScope.launch {

            val locationsResponse = repository.getLocations(query)
            val locationsDb = repository.getAllDb()
            val fetchWeather = locationsResponse.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }
            val weathersResponse = fetchWeather.awaitAll()
            clearData()
            for (i in 0 until locationsResponse.size) {
                if (locationsResponse[i] in locationsDb) locationsResponse[i].favorited = true
                weatherMap.value!![locationsResponse[i]] = weathersResponse[i]
                weatherMap.notifyObserver()
            }
        }
    }

    fun cancelOps() {
        jobLoc?.cancel()
    }

    fun clearData() {
        weatherMap.value!!.clear()
        weatherMap.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
