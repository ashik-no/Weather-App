package com.example.liveweatherapp.repository

import com.example.liveweatherapp.api.RetrofitInterface
import com.example.liveweatherapp.model.weatherapp

class WeatherRepository(private val api: RetrofitInterface) {
    suspend fun getWeather(city: String, apiKey: String, units: String) : weatherapp{
        return api.getWeather(city,apiKey,units)


    }
}