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
        getRecent()
    }

    fun getLocations(query: String) {
        var favoritedDb: MutableList<Location>
        jobLoc = viewModelScope.launch {
            // ova dva poziva s async
            val locationsResponse = repository.getLocations(query)
            favoritedDb = repository.getFavorited()
            val fetchWeather = locationsResponse.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }
            val weathersResponse = fetchWeather.awaitAll()
            clearData()
            for (i in 0 until locationsResponse.size) {
                for (favorite in favoritedDb) {
                    if (locationsResponse[i].woeid == favorite.woeid) {
                        locationsResponse[i].favorited = true
                        break
                    }
                }
                weatherMap.value!![locationsResponse[i]] = weathersResponse[i]
                weatherMap.notifyObserver()
            }

//            val dbResponse = repository.getCount()
//            if (dbResponse - favoritedDb.size > 20) {
//                repository.filterRecent()
//            }
        }
    }

    fun getRecent() {
        jobLoc = viewModelScope.launch {
            // ova dva poziva s async
            val locationsResponse = repository.getRecentFive()
            val fetchWeather = locationsResponse.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }
            val weathersResponse = fetchWeather.awaitAll()
            clearData()
            for (i in 0 until locationsResponse.size) {
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
