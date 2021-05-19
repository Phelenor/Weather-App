package com.rafaelboban.weatherapp.ui.info

import android.annotation.SuppressLint
import android.icu.util.TimeZone
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.databinding.ActivityLocationBinding
import com.rafaelboban.weatherapp.databinding.InfoActivityBinding
import com.rafaelboban.weatherapp.ui.adapters.WeatherAdapter
import com.rafaelboban.weatherapp.ui.viewmodels.LocationViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: InfoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InfoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = ""

        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }

    }
}