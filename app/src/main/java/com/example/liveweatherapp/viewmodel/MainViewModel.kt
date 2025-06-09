package com.example.liveweatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveweatherapp.model.weatherapp
import com.example.liveweatherapp.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: WeatherRepository) : ViewModel(){
    private val _weatherData = MutableLiveData<weatherapp?>()
    val weatherData : LiveData<weatherapp?> = _weatherData

    fun fetchWeather(city: String, apiKey: String, units: String){
        viewModelScope.launch {
            try {
                val result = repository.getWeather(city,apiKey,units)
                _weatherData.postValue(result)
            } catch (e : Exception){
                _weatherData.postValue(null)
                Log.e("ViewModel", "Error: ${e.message}")
            }
        }
    }

}