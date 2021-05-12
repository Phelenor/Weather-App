package com.rafaelboban.weatherapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

class SettingsViewModel(private val repository: MainRepository) : ViewModel() {

    fun clearFavorites() {
        viewModelScope.launch {
            repository.deleteFavorites()
            repository.resetKey()
        }
    }

    fun clearRecent() {
        viewModelScope.launch {
            repository.deleteRecent()
        }
    }
}