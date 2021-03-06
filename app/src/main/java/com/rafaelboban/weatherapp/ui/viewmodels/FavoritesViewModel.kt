package com.rafaelboban.weatherapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.*

class FavoritesViewModel(val repository: MainRepository) : ViewModel() {

    val weatherMap = MutableLiveData<LinkedHashMap<Location, LocationWeather>>()
    var status = MutableLiveData<Boolean>()
    val handler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            if (status.value!!) {
                status.value = false
            }
            Log.e("EXCEPTION", "$exception")
        }
    }

    init {
        weatherMap.value = LinkedHashMap<Location, LocationWeather>()
        status.value = true
    }

    fun getLocations() {
        clearData()
        viewModelScope.launch(handler) {
            val favorites = repository.getFavoritesDb()

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
            repository.deleteFavorites()
            repository.resetKey()
            val favs = favorites.map {
                Favorite(null, it.woeid)
            }
            for (fav in favs)
                repository.insertFavorite(fav)
        }
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    private fun clearData() {
        weatherMap.value!!.clear()
        weatherMap.notifyObserver()
    }

    fun updateFavorites(favorites: MutableList<Location>) {
        viewModelScope.launch {
            repository.deleteFavorites()
            repository.resetKey()
            for (fav in favorites)
                repository.insertFavorite(Favorite(null, fav.woeid))
        }
    }
}