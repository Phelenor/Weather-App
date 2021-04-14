package com.rafaelboban.weatherapp.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import com.rafaelboban.weatherapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import okhttp3.internal.wait

class SearchViewModel(val repository: MainRepository) : ViewModel() {

    val locations = MutableLiveData<ArrayList<Location>>()
    val weathers = MutableLiveData<ArrayList<LocationWeather>>()

    init {
        locations.value = arrayListOf()
        weathers.value = arrayListOf()
    }

    fun getLocations(query: String) {
        viewModelScope.launch {
            locations.value = repository.getLocations(query)
            getWeather()
        }
    }

    private fun getWeather() {
        viewModelScope.launch {
            for (location in locations.value!!) {
                weathers.value?.add(repository.getWeather(location.woeid))
                weathers.notifyObserver()
            }
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}