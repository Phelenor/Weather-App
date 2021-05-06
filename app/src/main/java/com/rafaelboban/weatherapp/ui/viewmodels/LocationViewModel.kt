package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
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
}
