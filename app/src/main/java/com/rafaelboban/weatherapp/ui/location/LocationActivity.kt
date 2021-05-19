package com.rafaelboban.weatherapp.ui.location

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.TimeZone
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
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
import com.google.android.material.snackbar.Snackbar
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.api.ApiHelper
import com.rafaelboban.weatherapp.data.api.RetrofitBuilder
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.databinding.ActivityLocationBinding
import com.rafaelboban.weatherapp.databinding.SnackbarBinding
import com.rafaelboban.weatherapp.ui.adapters.WeatherAdapter
import com.rafaelboban.weatherapp.ui.viewmodels.LocationViewModel
import com.rafaelboban.weatherapp.ui.viewmodels.ViewModelFactory
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

const val EXTRA_LOCATION = "EXTRA_LOCATION"
const val EXTRA_WEATHER = "EXTRA_WEATHER"

private lateinit var viewModel: LocationViewModel
private lateinit var recyclerViewWeek: RecyclerView
private lateinit var recyclerViewToday: RecyclerView
private lateinit var recyclerAdapterToday: WeatherAdapter
private lateinit var recyclerAdapterWeek: WeatherAdapter

lateinit var location: Location
lateinit var weather: LocationWeather
var unit = "metric"
var TIME_HOURS: Int = 0

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.backButton.setOnClickListener {
            this.onBackPressed()
        }

        getPreferences()

        location = intent.getSerializableExtra(EXTRA_LOCATION) as Location
        weather = intent.getSerializableExtra(EXTRA_WEATHER) as LocationWeather
        binding.collapsingToolbar.title = location.title

        binding.mapView.getMapAsync(this)
        binding.mapView.onCreate(savedInstanceState)

        if (location.favorite == true) {
            binding.favoriteButton.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_star1, null
                )
            )
        }

        val date = LocalDate.parse(weather.time.split("T")[0])

        setupViewModel()
        setupObservers()
        setupListeners()

        viewModel.getHourly(location.woeid,
            "${date.year}/${date.monthValue}/${date.dayOfMonth}")

        location.isRecent = true
        // update db location var isRecent
        viewModel.updateRecent(location)

        // Next Week RV
        recyclerViewWeek = binding.nextRv
        recyclerViewWeek.setHasFixedSize(true)
        recyclerViewWeek.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerAdapterWeek = WeatherAdapter(
            weather.consolidated_weather.subList(
                1,
                weather.consolidated_weather.size
            )
        )
        recyclerViewWeek.adapter = recyclerAdapterWeek

        // Today RV
        recyclerViewToday = binding.todayRv
        recyclerViewToday.setHasFixedSize(true)
        recyclerViewToday.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerAdapterToday = WeatherAdapter(
            viewModel.weather.value?.subList(
                0,
                viewModel.weather.value?.size!!
            ) as MutableList<ConsolidatedWeather>,
            weather.time.split("T")[1].split(".")[0].split(":")[0]
        )
        recyclerViewToday.adapter = recyclerAdapterToday

        setInfoBoard()
        setInfoGrid()
    }

    private fun setupListeners() {
        binding.favoriteButton.setOnClickListener {
            if (location.favorite == true) {
                location.favorite = false
                viewModel.deleteFavorite(location)
                binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_baseline_star0, null))
            } else {
                location.favorite = true
                viewModel.insertFavorite(location)
                binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(resources,
                    R.drawable.ic_baseline_star1, null))
            }
        }
    }

        private fun getPreferences() {
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            unit = sp.getString("unit", "metric")!!
        }

    private fun setupObservers() {
        viewModel.weather.observe(this, {
            recyclerAdapterToday.apply {
                addHourly(it)
                notifyDataSetChanged()
                recyclerViewToday.layoutManager!!.scrollToPosition(TIME_HOURS)
            }
        })

        viewModel.status.observe(this, {
            val snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.TRANSPARENT);
            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
            snackbarLayout.setPadding(0, 0, 0, 0);
            val snackBinding = SnackbarBinding.inflate(LayoutInflater.from(this))
            if (!it) {
                snackBinding.snackbarClose.setOnClickListener {
                    snackbar.dismiss()
                }
                snackBinding.message.text = getString(R.string.network_error)
                snackBinding.message.backgroundTintList = ColorStateList.valueOf(resources.getColor(
                    R.color.error))
                snackbarLayout.addView(snackBinding.root, 0)
                snackbar.show()
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService), DbHelper(DatabaseBuilder.getInstance(this)))
        ).get(LocationViewModel::class.java)
    }

    private fun setInfoBoard() {
        val dateString = weather.time.split("T")[0]
        val time = weather.time.split("T")[1].split(".")[0].split(":")
        var hours = time[0]
        TIME_HOURS = hours.toInt()
        var marker = "AM"
        if (hours.toInt() - 12 > 0) {
            marker = "PM"
            hours = (hours.toInt() - 12).toString()
            if (hours.toInt() < 10) hours = "0$hours"
        }

        var timeString = "${hours}:${time[1]} $marker (${
            TimeZone.getTimeZone(weather.timezone)
                .getDisplayName(false, TimeZone.SHORT)
        })"
        binding.time.text = timeString
        var date = LocalDate.parse(dateString)
        binding.date.text = resources.getString(
            R.string.date,
            date.dayOfWeek.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                .substring(0, 3),
            date.month.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT),
            date.dayOfMonth.toString()
        )

        val consolidatedWeather = weather.consolidated_weather[0]
        var temp = consolidatedWeather.the_temp
        if (unit == "imperial") temp = temp * 1.8 + 32
        binding.temperatureTv.text = resources.getString(
            R.string.temperature_celsius_sign,
            temp.roundToInt().toString(), "°"
        )

        val icon = resources.getIdentifier(
            "ic_" + consolidatedWeather.weather_state_abbr,
            "drawable", packageName
        )
        binding.weatherIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, icon, null))

        binding.weatherDesc.text = when (consolidatedWeather.weather_state_abbr) {
            "c" -> "Clear"
            "h" -> "Hail"
            "sn" -> "Snow"
            "sl" -> "Sleet"
            "t" -> "Thunderstorm"
            "hr" -> "Heavy Rain"
            "lr" -> "Light Rain"
            "s" -> "Showers"
            "hc" -> "Heavy Cloud"
            "lc" -> "Light Cloud"
            else -> "c"
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun setInfoGrid() {
        val consWeather = weather.consolidated_weather[0]
        var tempMax = consWeather.max_temp
        var tempMin = consWeather.min_temp
        var windSpeed = consWeather.wind_speed
        var visibility = consWeather.visibility
        var airPressure = consWeather.air_pressure
        var speedUnit = "km/h"
        var airPressureUnit = "hPa"
        var distanceUnit = "km"

        if (unit == "imperial") {
            tempMax = tempMax * 1.8 + 32
            tempMin = tempMin * 1.8 + 32
            windSpeed /= 1.609
            airPressure /= 69
            visibility /= 1.609
            speedUnit = "mp/h"
            airPressureUnit = "psi"
            distanceUnit = "mi"
        }

        binding.grid.minmaxTemp.text = resources.getString(
            R.string.grid_minmax,
            tempMin.roundToInt().toString(),
            tempMax.roundToInt().toString()
        )
        binding.grid.windSpeed.text = resources.getString(
            R.string.grid_wind,
            windSpeed.roundToInt().toString(), speedUnit, consWeather.wind_direction_compass
        )
        binding.grid.humidity.text = resources.getString(
            R.string.grid_humidity,
            consWeather.humidity.roundToInt().toString()
        )
        binding.grid.pressure.text = resources.getString(
            R.string.grid_pressure,
            airPressure.roundToInt().toString(), airPressureUnit
        )
        binding.grid.visibility.text = resources.getString(
            R.string.grid_visibility,
            visibility.roundToInt().toString(), distanceUnit
        )
        binding.grid.accuracy.text = resources.getString(
            R.string.grid_accuracy,
            consWeather.predictability.toString()
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lattLongString = location.latt_long.split(",")
        val lattLong = LatLng(lattLongString[0].toDouble(), lattLongString[1].toDouble())
        googleMap.addMarker(
            MarkerOptions()
                .position(lattLong)
                .title(location.title)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lattLong, 10.0f))
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}