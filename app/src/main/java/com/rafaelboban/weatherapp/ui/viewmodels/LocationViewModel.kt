package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LocationViewModel(val repository: MainRepository) : ViewModel() {

    val weather = MutableLiveData<MutableList<ConsolidatedWeather>>()

    init {
        weather.value = ArrayList()
    }

    fun getHourly(woeid: Int, date: String) {
        viewModelScope.launch {
            weather.value = repository.getWeatherDay(woeid, date)
        }
    }

    fun storeLocation(location: Location) {
        viewModelScope.launch {
            val storedResponse = repository.getAllDb()
            for (storedLocation in storedResponse) {
                if (storedLocation.woeid == location.woeid) {
                    repository.deleteDb(storedLocation)
                    break
                }
            }
            repository.insertAllDb(location)
        }
    }
}
