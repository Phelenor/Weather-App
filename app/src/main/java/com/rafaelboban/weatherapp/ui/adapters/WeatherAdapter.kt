package com.rafaelboban.weatherapp.ui.adapters

import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.rafaelboban.weatherapp.R
import com.rafaelboban.weatherapp.data.model.ConsolidatedWeather
import com.rafaelboban.weatherapp.databinding.WeatherItemBinding
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

// Reusing the adapter for both RVs in LocationActivity
// true for hourly, false for daily
var CARD_SWITCH = true
var unit = "metric"

class WeatherAdapter(
    private val weather_list: MutableList<ConsolidatedWeather>,
    private val hours_now: String = ""
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    inner class WeatherViewHolder(val binding: WeatherItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = WeatherItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val sp = PreferenceManager.getDefaultSharedPreferences(parent.context)
        unit = sp.getString("unit", "metric")!!

        CARD_SWITCH = hours_now.isNotBlank()
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weather_list[position]
        val context = holder.binding.root.context

        if (CARD_SWITCH) {
            var hours = position.toString()
            if (hours.toInt() < 10) hours = "0$hours"
            holder.binding.timeTv.text = context.resources.getString(R.string.time,
                hours)
            if (position == hours_now.toInt()) {
                Log.d("HOURS", "${position} == ${hours_now.toInt()}")
                holder.binding.weatherInfoCard.setBackgroundTintList(context.resources.getColorStateList(R.color.surface_2))
            } else {
                // ?????????????????????????????? bez ovoga svaka n-ta prognoza bude siva
                holder.binding.weatherInfoCard.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        } else {
            holder.binding.timeTv.text =
                LocalDate.parse(weather.applicable_date).dayOfWeek.toString().toUpperCase(Locale.ROOT)
                    .substring(0, 3)
        }
        val icon = context.resources.getIdentifier(
            "ic_" + weather.weather_state_abbr,
            "drawable", context.packageName
        )
        holder.binding.weatherIcon.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                icon,
                null
            )
        )
        var temp = weather.the_temp
        if (unit == "imperial") {
            temp = temp * 1.8 + 32
        }
        holder.binding.temperatureTv.text = context.resources.getString(
            R.string.temperature_celsius_sign,
            temp.roundToInt().toString(), "°"
        )
    }

    override fun getItemCount(): Int {
        return weather_list.size
    }

    fun addHourly(hourly: MutableList<ConsolidatedWeather>) {
        if (hourly.size >= 24)
            weather_list.addAll(hourly.subList(hourly.size - 24, hourly.size))
    }
}
