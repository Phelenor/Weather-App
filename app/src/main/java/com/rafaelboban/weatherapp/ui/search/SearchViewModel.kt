package com.rafaelboban.weatherapp.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import com.rafaelboban.weatherapp.utils.Resource
import kotlinx.coroutines.*
import okhttp3.internal.notify
import okhttp3.internal.wait
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel(val repository: MainRepository) : ViewModel() {

    var jobLoc: Job? = null
    var jobWeather: Job? = null


    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()
    var locations = ArrayList<Location>()

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
    }

    fun getLocations(query: String) {
        jobLoc = viewModelScope.launch {
            locations = repository.getLocations(query)
            getWeather()
        }
    }

    fun getWeather() {
        clear()
        jobWeather = viewModelScope.launch {
            for (location in locations) {
                weatherMap.value?.set(location, repository.getWeather(location.woeid))
                weatherMap.notifyObserver()
            }
        }
    }

    fun cancelOps() {
        jobLoc?.cancel()
        jobWeather?.cancel()
    }

    fun clear() {
        weatherMap.value!!.clear()
        weatherMap.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}