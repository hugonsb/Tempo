package com.happs.tempo.model

data class HourlyUnits(
    val apparent_temperature: String,
    val cloud_cover_high: String,
    val precipitation_probability: String,
    val rain: String,
    val relative_humidity_2m: String,
    val temperature_2m: String,
    val time: String
)