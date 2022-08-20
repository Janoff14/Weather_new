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
import android.text.format.DateUtils
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
import com.example.sanjarbey.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
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
            Toast.makeText(this, "${location.latitude} ${location.longitude}", Toast.LENGTH_SHORT)

        } else {
            Log.d("tt", "onCreate: 2")
        }

        searchImgv.setOnClickListener {
            val cityName = cityedt.text.toString()
            if (cityName.isEmpty()){
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT)
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
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT)
            } else{
                Toast.makeText(this, "Please provide the permissions...", Toast.LENGTH_SHORT)
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
            url = "https://api.openweathermap.org/data/3.0/onecall?lat=" + adress_arr[1] + "&lon=" + adress_arr[0] +"&exclude=hourly,daily&units=metric&appid=bc5aa90e2855d62a4699c4ab8cc764eb"
        } else {
            url =
                "https://api.openweathermap.org/data/3.0/onecall?lat=" + latitude + "&lon=" + longitude + "&exclude=hourly&units=metric&appid=bc5aa90e2855d62a4699c4ab8cc764eb"
        }

        var requestQueue: RequestQueue = Volley.newRequestQueue(this)

        var jsonObjectRequest: JsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("TAG", "getWeatherInfo: ee")
                Toast.makeText(this, "ss", Toast.LENGTH_SHORT)
                loadingPB.visibility = View.GONE
                homeRvl.visibility = View.VISIBLE
                weatherModelArrayList.clear()

                try {
                    val temperatue = response.getJSONObject("current").getString("temp")
                    temperaturTxtV.text = temperatue
                    val time_unix = response.getJSONObject("current").getString("dt")
                    val time = getDateTime(time_unix)
                    Log.d("time", "getWeatherInfo: $time")
                    val condition = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0)
                        .getString("icon")
                    Picasso.get().load("http://openweathermap.org/img/wn/" + condition + "@2x.png").into(iconImgV)

                    if (condition[2].equals('d')){
                        Log.d("day", "getWeatherInfo: day")
                        Picasso.get().load("https://unsplash.com/photos/28ZbKOWiZfs").into(backImgV)
                    } else{
                        Picasso.get().load("https://steprimo.com/android/us/app/com.Sky_HD.Wallpaper.wallpapersapp/Sky-Wallpaper/#iPhone-img-6").into(backImgV)
                    }

                } catch (e: JSONException){

                }
            },
            {
                Toast.makeText(this, "22", Toast.LENGTH_SHORT)

            })

        requestQueue.add(jsonObjectRequest)
    }


    private fun getAdressfromLocation(location: String = "",  context: Context): ArrayList<String>{

            var longitude = ""
            var latitude = ""
            var res = arrayListOf<String>()
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


    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("HH:MM MM/dd/yyyy")
            val time = DateUtils.formatDateTime(this, (s.toLong()-18000)*1000, DateUtils.FORMAT_SHOW_TIME)
            Log.d("time", "getDateTime: $time")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val netDate = Date(s.toLong()*1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}