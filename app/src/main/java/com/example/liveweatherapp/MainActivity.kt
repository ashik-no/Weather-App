package com.example.liveweatherapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.liveweatherapp.api.ApiHelper
import com.example.liveweatherapp.api.RetrofitInterface
import com.example.liveweatherapp.databinding.ActivityMainBinding
import com.example.liveweatherapp.repository.WeatherRepository
import com.example.liveweatherapp.viewmodel.FactoryViewModel
import com.example.liveweatherapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale





//241d040e6d6eca6a874cfd1641e93c92
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retorfit = ApiHelper.getInstance().create(RetrofitInterface::class.java)
        val repository = WeatherRepository(retorfit)
        viewModel = ViewModelProvider(this,FactoryViewModel(repository)).get(MainViewModel::class.java)
        viewModel.fetchWeather("Dhaka", "241d040e6d6eca6a874cfd1641e93c92", "metric")


        viewModel.weatherData.observe(this) { weather ->
            if (weather != null) {
                Log.d("WeatherDataCheck", "Temp: ${weather.main.temp}, Min: ${weather.main.temp_min}, Max: ${weather.main.temp_max}")

                binding.currentTemp.text = "${weather.main.temp}°C"
                binding.feelslike.text = "Feels Like: ${weather.main.feels_like}°C"



                val rainVolume = weather.rain?.`1h` ?: 0.0
                binding.conditionValue.text = "${rainVolume}mm"
                binding.humidityValue.text= weather.main.humidity.toString()
                binding.windValue.text = weather.main.pressure.toString()

                binding.sunriseValue.text = convertUnixToTime(weather.sys.sunrise.toLong())
                binding.sunsetValue.text = convertUnixToTime(weather.sys.sunset.toLong())
                binding.seaValue.text = weather.main.sea_level.toString()
                val condtition_ = weather.weather[0].main
                binding.condition.text = condtition_
                changeImageOnCondition(condtition_)

                val currentTime = System.currentTimeMillis() / 1000  // current time in seconds (Unix timestamp)
                val sunriseTime = weather.sys.sunrise
                val sunsetTime = weather.sys.sunset

                if (currentTime < sunriseTime || currentTime > sunsetTime) {

                    binding.root.setBackgroundResource(R.drawable.nightbackground)
                    binding.lottieAnimationView.pauseAnimation()

//                    binding.lottieAnimationView.setAnimation(R.raw.night_animation)

                } else {

                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }


            } else {
                binding.currentTemp.text = "No Data"
                Log.e("MainActivity", "Weather data is null")
            }
        }
        binding.locationSearch.isIconified = false
        binding.locationSearch.requestFocus()

        val locationName = binding.locationName

        binding.locationSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // API call
                    viewModel.fetchWeather(query.trim(), "241d040e6d6eca6a874cfd1641e93c92", "metric")
                    locationName.text = query
                    binding.locationSearch.clearFocus() // Keyboard hide
                } else {
                    Toast.makeText(this@MainActivity, "Please enter a city name", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can implement search suggestions here (optional)
                return false
            }
        })


        updateDateAndDay()



    }

    private fun changeImageOnCondition(con : String) {
        when(con) {
            "haze" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
            binding.lottieAnimationView.setAnimation(R.raw.snow)

        }
            "clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    fun convertUnixToTime(unixTime: Long): String {
        val date = Date(unixTime * 1000) // Convert to milliseconds
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault()) // e.g., 06:35 AM
        return format.format(date)
    }
    private fun updateDateAndDay() {
        val calendar = Calendar.getInstance()


        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        val currentDate = dateFormat.format(calendar.time)
        val currentDay = dayFormat.format(calendar.time)


        binding.onlyDay.text = currentDate
        binding.fullDate.text = currentDay
    }

}