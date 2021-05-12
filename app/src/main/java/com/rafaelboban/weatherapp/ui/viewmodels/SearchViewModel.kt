package com.rafaelboban.weatherapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.set

class SearchViewModel(private val repository: MainRepository) : ViewModel() {

    private var jobLoc: Job? = null
    val handler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            if (status.value!!) {
                status.value = false
            }
            Log.e("EXCEPTION", "$exception")
        }
    }

    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()
    var status = MutableLiveData<Boolean>()

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
        status.value = true
        getLocations(recent = true)
    }

    fun getLocations(query: String = "", recent: Boolean = false) {
        var favoritesDb: MutableList<Location>
        var locationsResponse: MutableList<Location>
        jobLoc = viewModelScope.launch(handler) {

            if (recent) {
                locationsResponse = repository.getRecentDb()
                for (location in locationsResponse)
                if (locationsResponse.size > 4) {
                    locationsResponse = locationsResponse.subList(locationsResponse.size - 4, locationsResponse.size)
                }
                locationsResponse.reverse()
            } else {
                locationsResponse = repository.getLocations(query)
                locationsResponse.forEach {
                    repository.insertLocation(it)
                }
            }

            favoritesDb = repository.getFavoritesDb()
            val fetchWeather = locationsResponse.map {location ->
                async {
                    repository.getWeather(location.woeid)
                }
            }

            val weathersResponse = fetchWeather.awaitAll()
            clearData()
            for (i in 0 until locationsResponse.size) {
                for (favorite in favoritesDb) {
                    if (locationsResponse[i].woeid == favorite.woeid) {
                        locationsResponse[i].favorite = true
                        break
                    }
                }
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
