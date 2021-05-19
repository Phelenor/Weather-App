package com.rafaelboban.weatherapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.repository.MainRepository

class ViewModelFactory(private val apiHelper: ApiHelper, private val dbHelper: DbHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(MainRepository(apiHelper, dbHelper)) as T
        } else if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(MainRepository(apiHelper, dbHelper)) as T
        } else if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(MainRepository(apiHelper, dbHelper)) as T
        } else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(MainRepository(apiHelper, dbHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}