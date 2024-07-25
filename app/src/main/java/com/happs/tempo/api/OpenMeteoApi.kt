package com.happs.tempo.api

import com.happs.tempo.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface OpenMeteoApi {
    @GET("/v1/forecast")
    suspend fun getWeatherData(
        @QueryMap options: Map<String, String>
    ): Response<WeatherModel>
}
