package com.rafaelboban.weatherapp.ui.adapters

import android.content.Intent
import android.icu.util.TimeZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.database.DatabaseBuilder
import com.rafaelboban.weatherapp.data.database.DbHelper
import com.rafaelboban.weatherapp.data.model.Favorite
import com.rafaelboban.weatherapp.data.model.Location
import com.rafaelboban.weatherapp.data.model.LocationWeather
import com.rafaelboban.weatherapp.databinding.CityItemCardBinding
import com.rafaelboban.weatherapp.ui.location.EXTRA_LOCATION
import com.rafaelboban.weatherapp.ui.location.EXTRA_WEATHER
import com.rafaelboban.weatherapp.ui.location.LocationActivity
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


lateinit var db: DbHelper

class LocationsAdapter(
    private val weatherMap: LinkedHashMap<Location, LocationWeather>,
    private var favoritesFragment: Boolean = false
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

        db = DbHelper(DatabaseBuilder.getInstance(parent.context))

        return LocationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        val location = weatherMap.keys.toMutableList()[holder.adapterPosition]
        val weather = weatherMap[location]!!.consolidated_weather[0]
        val context = holder.binding.root.context

        holder.binding.cityNameTv.text = location.title
        holder.binding.temperatureTv.text = context.
        resources.getString(
            R.string.temperature_celsius_sign,
            weather.the_temp.roundToInt().toString(), "°"
        )
        val icon = context.resources.getIdentifier("ic_" + weather.weather_state_abbr,
            "drawable", context.packageName)
        holder.binding.weatherIcon.setImageDrawable(ResourcesCompat.getDrawable(context.resources, icon, null))

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java).apply {
                putExtra(EXTRA_LOCATION, location)
            }
            intent.putExtra(EXTRA_WEATHER, weatherMap[location])
            context.startActivity(intent)
        }

        holder.binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(context.resources,
            R.drawable.ic_baseline_star0, null))

        if (location.favorite == true) {
            holder.binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(context.resources,
                R.drawable.ic_baseline_star1, null))
        }

        if (favoritesFragment) {
            val time = weatherMap[location]!!.time.split("T")[1].split(".")[0].split(":")
            var hours = time[0]
            var marker = "AM"
            if (hours.toInt() - 12 > 0) {
                marker = "PM"
                hours = (hours.toInt() - 12).toString()
                if (hours.toInt() < 10) hours = "0$hours"
            }
            var timeString = "${hours}:${time[1]} $marker"
            val tz = TimeZone.getTimeZone(weatherMap[location]!!.timezone)
                .getDisplayName(false, TimeZone.SHORT)

            holder.binding.cityCoordinatesTv.text = tz
            holder.binding.cityTimeTv.text = timeString
        } else {
            holder.binding.cityCoordinatesTv.text = location.latt_long
        }

        holder.binding.favoriteButton.setOnClickListener {
            if (location.favorite == true) {
                location.favorite = false
                runBlocking {
                    db.deleteFavorite(location.woeid.toString())
                    db.insertLocation(mutableListOf(location))
                }
                if (favoritesFragment) {
                    weatherMap.remove(weatherMap.keys.toMutableList()[holder.adapterPosition])
                    notifyItemRemoved(holder.adapterPosition)
                }

                holder.binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(context.resources,
                    R.drawable.ic_baseline_star0, null))
            } else {
                location.favorite = true
                runBlocking {
                    db.insertFavorite(mutableListOf(Favorite(null, location.woeid)))
                }
                holder.binding.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(context.resources,
                    R.drawable.ic_baseline_star1, null))
            }
        }
    }

    override fun getItemCount(): Int {
        return weatherMap.size
    }

    fun addWeathers(weathers: LinkedHashMap<Location, LocationWeather>) {
        this.weatherMap.clear()
        this.weatherMap.putAll(weathers)
    }
}