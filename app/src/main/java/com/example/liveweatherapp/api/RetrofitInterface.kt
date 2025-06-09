package com.example.liveweatherapp.api

import com.example.liveweatherapp.model.weatherapp
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city : String,
        @Query("appid") apikey : String,
        @Query("units") units : String
    ): weatherapp
}