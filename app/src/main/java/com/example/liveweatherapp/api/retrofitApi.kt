package com.example.liveweatherapp.api

import com.example.liveweatherapp.model.weatherapp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface retrofitApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") city : String,
        @Query("appid") apikey : String,
        @Query("unites") unites : String
    ): weatherapp
}