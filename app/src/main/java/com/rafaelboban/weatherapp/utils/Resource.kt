package com.rafaelboban.weatherapp.utils

import com.rafaelboban.weatherapp.utils.Status.SUCCESS
import com.rafaelboban.weatherapp.utils.Status.ERROR
import com.rafaelboban.weatherapp.utils.Status.LOADING

// Class responsible for communicating the current state of Network Call to the UI Layer
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> = Resource(status = LOADING, data = data, message = null)
    }
}
