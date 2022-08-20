package com.example.sanjarbey

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat

class WeatherAdapter(context: Context, weatherModelArrayList: ArrayList<WeatherModel>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    private  var context: Context = context
    private  var weatherModelArrayList: ArrayList<WeatherModel> = weatherModelArrayList



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = weatherModelArrayList.get(position)
        holder.temperatureTxt = model.getTemperature() +"°C"
        Picasso.get().load("http://openweathermap.org/img/wn/" + model.getIcon() + "@2x.png").into(holder.conditionImg)
        holder.windTxt = model.getWindSpeed() + "km/h"
        var date = java.time.format.DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(1532358895))
        holder.timeTxt = date
    }

    override fun getItemCount(): Int {
        return  weatherModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var windTxt = itemView.findViewById<TextView>(R.id.idTxtWindSpeed).toString()
            var temperatureTxt = itemView.findViewById<TextView>(R.id.idTxtTemperature).toString()
            var timeTxt = itemView.findViewById<TextView>(R.id.idTxtTime).toString()
            var conditionImg = itemView.findViewById<ImageView>(R.id.idImgCondition)

    }
}