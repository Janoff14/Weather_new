package com.example.sanjarbey

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sanjarbey.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import jp.wasabeef.glide.transformations.BlurTransformation
import org.json.JSONException
import java.io.IOException
import java.time.Instant.ofEpochSecond
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var homeRvl: RelativeLayout
    private lateinit var loadingPB: ProgressBar
    private lateinit var cityNameTxtV: TextView
    private lateinit var temperaturTxtV: TextView
    private lateinit var conditionTxtV: TextView
    private lateinit var cityedt: TextInputEditText
    private lateinit var backImgV: ImageView
    private lateinit var iconImgV: ImageView
    private lateinit var searchImgv: ImageView
    private lateinit var weatherRcV: RecyclerView
    private lateinit var weatherModelArrayList: ArrayList<WeatherModel>
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var locationManager: LocationManager
    val PERMISSION_CODE = 1

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        homeRvl = binding.idRLHome
        loadingPB = binding.idPBloading
        cityNameTxtV = binding.idTxtCityName
        temperaturTxtV = binding.idTxtTemperature
        conditionTxtV = binding.idTxtCondition
        cityedt = binding.edtCity
        backImgV = binding.idImVBack
        iconImgV = binding.idImgVIcon
        searchImgv = binding.idImgVSearch
        weatherRcV = binding.idRcVWeather

        weatherModelArrayList = ArrayList()
        weatherAdapter = WeatherAdapter(this, weatherModelArrayList)
        weatherRcV.adapter = weatherAdapter

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(this as Activity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this as Activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE)
        }

        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        Log.d("tt", "onCreate: ")
        if (location != null) {
            Log.d("tt", "onCreate: o")
            getWeatherInfo("", location.latitude, location.longitude)
            Toast.makeText(this, "${location.latitude} ${location.longitude}", Toast.LENGTH_SHORT).show()

        } else {
            Log.d("tt", "onCreate: 2")
        }

        searchImgv.setOnClickListener {
            var cityName = cityedt.text.toString()
            cityName = cityName.replace("\\s".toRegex(), "")

            if (cityName.isEmpty()){
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
            } else{
                cityNameTxtV.text = cityName
                getWeatherInfo(cityName)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Please provide the permissions...", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    private fun getWeatherInfo(cityName: String = "", latitude: Double = .0, longitude: Double = .0) {
        var adress_arr = arrayListOf<String>()
        var url = ""
        Log.d("TAG", "getWeatherInfo: $latitude $longitude")
        if (!cityName.isEmpty()){

            adress_arr = getAdressfromLocation(cityName, this)
            Log.d("adress", "getWeatherInfo: $adress_arr")
            url = "https://api.openweathermap.org/data/3.0/onecall?lat=" + adress_arr[0] + "&lon=" + adress_arr[1] +"&exclude=daily,minutely,alerts&units=metric&appid=bc5aa90e2855d62a4699c4ab8cc764eb"
        } else {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val name = addresses[0].getAddressLine(0)
            Log.d("city", "getWeatherInfo: $name")
            cityNameTxtV.text = name
            url =
                "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&exclude=daily,minutely,alerts&units=metric&appid=bc5aa90e2855d62a4699c4ab8cc764eb"
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->

                Log.d("TAG", "getWeatherInfo: ee")
                Toast.makeText(this, "ss", Toast.LENGTH_SHORT).show()
                loadingPB.visibility = View.GONE
                homeRvl.visibility = View.VISIBLE
                if (weatherModelArrayList.size != 0){
                    runOnUiThread {
                        weatherAdapter.notifyItemRangeRemoved(0, weatherModelArrayList.size)
                    }
                    weatherModelArrayList.clear()

                }

                try {
                    var temperatue = response.getJSONObject("current").getString("temp")
                    temperatue = "$temperatue°c"
                    temperaturTxtV.text = temperatue
                    conditionTxtV.text = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main")
                    val time_unix = response.getJSONObject("current").getString("dt")
                    val timezone = response.getString("timezone")
                    val time = getDateTime(time_unix, timezone)
                    Log.d("timenow", "getWeatherInfo: $time")
                    val condition = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0)
                        .getString("icon")
                    val icon_url = "https://openweathermap.org/img/wn/$condition@2x.png"

                    Glide.with(this)
                        .load(icon_url)
                        .override(300, 200)
                        .into(iconImgV)


                    if (condition[2] == 'd'){
                        Glide.with(this)
                            .load(R.drawable.day)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
                            .into(backImgV)
                    } else{
                        Glide.with(this)
                            .load(R.drawable.night)
                            .apply(RequestOptions.bitmapTransform(BlurTransformation(25,3)))
                            .into(backImgV)

                    }


                    //daily recycler view

                    val dailyArray = response.getJSONArray("hourly")
                    for (i in 0 until dailyArray.length()){
                        Log.d("TAG", "getWeatherInfo: $cityName")

                        val hour = dailyArray.getJSONObject(i)
                        val time1 = hour.getString("dt")
                        val temperature = hour.getString("temp")
                        val icon = hour.getJSONArray("weather").getJSONObject(0).getString("icon")
                        val windspeed = hour.getString("wind_speed")

                        val weatherModel = WeatherModel(time1, temperature, icon, windspeed, timezone)
                        weatherModelArrayList.add(weatherModel)
                        runOnUiThread {
                            weatherAdapter.notifyItemInserted(weatherModelArrayList.size-1)
                        }
                    }

                } catch (e: JSONException){
                    Log.d("check", "getWeatherInfo: $e")
                }
            },
            {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()

            })

        requestQueue.add(jsonObjectRequest)
    }


    private fun getAdressfromLocation(location: String = "",  context: Context): ArrayList<String>{

            var longitude = ""
            var latitude = ""
            val res = arrayListOf<String>()
            val geoCoder = Geocoder(
                context,
                Locale.getDefault()
            )
            try {
                val addressList = geoCoder.getFromLocationName(location, 1)
                if (addressList != null && addressList.size > 0) {
                    val address = addressList.get(0) as Address

                    longitude = address.longitude.toString()
                    latitude = address.latitude.toString()
                    res.add(latitude)
                    res.add(longitude)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Unable to connect to GeoCoder", e)
            }
        return res
    }


    private fun getDateTime(s: String = "1665237046", timezone: String, format: String = "EEE, LLL d hh:mm a"): String {
        try {
            val timestamp = s.toInt()
            val zoneId = ZoneId.of(timezone)
            val instant = ofEpochSecond(timestamp.toLong())
            val formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
            return instant.atZone(zoneId).format(formatter)
        } catch (e: Exception) {
            return e.toString()
        }
    }

}