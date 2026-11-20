package com.example.monitoreossilos.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double
)

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun obtenerClimaMelipilla(
        @Query("latitude") lat: Double = -33.6891,
        @Query("longitude") lon: Double = -71.2153,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}

object WeatherClient {
    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}