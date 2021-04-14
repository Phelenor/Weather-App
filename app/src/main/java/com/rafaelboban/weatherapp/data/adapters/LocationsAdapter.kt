package com.rafaelboban.weatherapp.data.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.databinding.CityItemCardBinding
import java.lang.Exception
import kotlin.math.roundToInt


class LocationsAdapter(
    private val locations: ArrayList<Location>,
    private val weathers: ArrayList<LocationWeather>
) : RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    inner class LocationsViewHolder(val binding: CityItemCardBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        val binding = CityItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val location = locations[position]
        val context = holder.binding.root.context


        holder.binding.cityNameTv.text = location.title
        holder.binding.cityCoordinatesTv.text = location.latt_long

        try {
            val weather = weathers[position].consolidated_weather[0]

            if (location.woeid != weathers[position].woeid) {
                Log.e("ERRoR", "BRRRR")
            }

            holder.binding.temperatureTv.text = context.
            resources.getString(
                R.string.temperature_celsius_sign,
                weather.the_temp.roundToInt().toString(), "°"
            )

            val icon = context.resources.getIdentifier("ic_" + weather.weather_state_abbr,
                "drawable", context.packageName)
            holder.binding.weatherIcon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, icon, null))
        } catch (e:Exception) {}
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    fun addLocations(locations: ArrayList<Location>) {
        this.locations.apply {
            clear()
            addAll(locations)
        }
    }

    fun addWeathers(weathers: ArrayList<LocationWeather>) {
        this.weathers.apply {
            clear()
            addAll(weathers)
        }
    }
}