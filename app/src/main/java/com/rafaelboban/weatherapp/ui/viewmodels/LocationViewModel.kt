package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.repository.MainRepository
import com.rafaelboban.weatherapp.ui.adapters.db
import kotlinx.coroutines.async
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
            repository.insertLocation(location)
        }
    }

    fun insertFavorite(location: Location) {
        viewModelScope.launch {
            repository.insertFavorite(Favorite(null, location.woeid))
        }
    }
}
