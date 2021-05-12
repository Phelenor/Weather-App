package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.repository.MainRepository
import kotlinx.coroutines.launch

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