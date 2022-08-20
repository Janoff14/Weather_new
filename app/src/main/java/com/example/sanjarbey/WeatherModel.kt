package com.example.sanjarbey

class WeatherModel {

    private lateinit var time: String
    private lateinit var temperature: String
    private lateinit var icon: String
    private lateinit var windSpeed: String

    constructor(time: String, temperature: String, icon: String, windSpeed: String) {
        this.time = time
        this.temperature = temperature
        this.icon = icon
        this.windSpeed = windSpeed
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