package com.happs.tempo.model

data class Hourly(
    val apparent_temperature: List<Double>,
    val cloud_cover_high: List<Int>,
    val precipitation_probability: List<Int>,
    val rain: List<Double>,
    val relative_humidity_2m: List<Int>,
    val temperature_2m: List<Double>,
    val time: List<String>
)