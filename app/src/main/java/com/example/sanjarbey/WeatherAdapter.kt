package com.example.sanjarbey

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter(val context: Context, val weatherModelArrayList: ArrayList<WeatherModel>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = weatherModelArrayList[position]
        val temperature = model.getTemperature() + "°c"
        val wind = model.getWindSpeed() + "m/s"
        Log.d("check", "onBindViewHolder: still working in adapter")
        holder.temperatureTxt.text = temperature
        val icon_url = "https://openweathermap.org/img/wn/${model.getIcon()}@2x.png"
        Glide.with(context)
            .load(icon_url)
            .override(300, 200)
            .into(holder.conditionImg)
        Log.d("check", "$")

        holder.windTxt.text = wind
        val date = getDateTime(model.getTime(), model.getTimeZone())
        holder.timeTxt.text = date.toString()
        Log.d("check", "onBindViewHolder: still working in adapter end")

    }

    override fun getItemCount(): Int {
        return  weatherModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var windTxt = itemView.findViewById<TextView>(R.id.idTxtWindSpeed)
            var temperatureTxt = itemView.findViewById<TextView>(R.id.idTxtTemperature)
            var timeTxt = itemView.findViewById<TextView>(R.id.idTxtTime)
            var conditionImg = itemView.findViewById<ImageView>(R.id.idImgCondition)

    }


    private fun getDateTime(s: String = "1665237046", timezone: String, format: String = "LLL d hh:mm a"): String {
        try {
            val timestamp = s.toInt()
            val zoneId = ZoneId.of(timezone)
            val instant = Instant.ofEpochSecond(timestamp.toLong())
            val formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
            return instant.atZone(zoneId).format(formatter)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}