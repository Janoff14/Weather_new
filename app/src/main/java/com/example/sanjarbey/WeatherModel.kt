package com.example.sanjarbey

class WeatherModel(
    private var time: String,
    private var temperature: String,
    private var icon: String,
    private var windSpeed: String,
    private var timeZone: String
) {

    fun getTimeZone(): String{
        return timeZone
    }

    fun setTimeZone(timeZone: String){
        this.timeZone = timeZone
    }
    fun getTime(): String {
        return time
    }

    fun getTemperature(): String{
        return temperature
    }

    fun getIcon(): String{
        return icon
    }

    fun getWindSpeed(): String{
        return windSpeed
    }

    fun setTime(time: String){
        this.time = time
    }

    fun setTemperature(temperature: String){
        this.temperature = temperature
    }

    fun setIcon(icon: String){
        this.icon = icon
    }

    fun setWindSpeed(windSpeed: String){
        this.windSpeed = windSpeed
    }

}