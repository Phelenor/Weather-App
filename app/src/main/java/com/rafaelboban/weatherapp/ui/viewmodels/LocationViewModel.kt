package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: MainRepository) : ViewModel() {

    val weather = MutableLiveData<MutableList<ConsolidatedWeather>>()

    init {
        weather.value = ArrayList()
    }

    fun getHourly(woeid: Int, date: String) {
        viewModelScope.launch {
            weather.value = repository.getWeatherDay(woeid, date)
        }
    }

    fun updateRecent(location: Location) {
        viewModelScope.launch {
            repository.deleteLocationDB(location.woeid.toString())
            repository.insertLocation(location)
        }
    }

    fun deleteFavorite(location: Location) {
        viewModelScope.launch {
            repository.deleteFavoriteDb(location.woeid.toString())
            favoritesEqualize()
        }
    }

    fun insertFavorite(location: Location) {
        viewModelScope.launch {
            repository.insertFavorite(Favorite(null, location.woeid))
        }
    }

    fun favoritesEqualize() {
        viewModelScope.launch {
            val favoritesResponse = repository.getFavoritesDb()
            repository.deleteFavorites()
            repository.resetKey()
            val favs = favoritesResponse.map {
                Favorite(null, it.woeid)
            }
            for (fav in favs)
                repository.insertFavorite(fav)
        }
    }
}
