package com.happs.tempo.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.happs.tempo.R

@Composable
fun getWeatherIconPainter(isNightTime: Boolean, rain: Double, cloudCoverHigh: Int) = when {
    isNightTime && rain in 0.1..0.5 -> painterResource(id = R.drawable.chuva_noite)
    isNightTime && rain in 0.5..Double.MAX_VALUE -> painterResource(id = R.drawable.chuva_forte)
    isNightTime && cloudCoverHigh in 21..80 -> painterResource(id = R.drawable.nublado_noite)
    isNightTime && cloudCoverHigh in 81..100 -> painterResource(id = R.drawable.muitas_nuvens_noite)
    isNightTime -> painterResource(id = R.drawable.lua)
    rain in 0.1..0.5 -> painterResource(id = R.drawable.chuva_com_sol)
    rain in 0.5..Double.MAX_VALUE -> painterResource(id = R.drawable.chuva_forte)
    cloudCoverHigh in 21..80 -> painterResource(id = R.drawable.nublado)
    cloudCoverHigh in 81..100 -> painterResource(id = R.drawable.muitas_nuvens)
    else -> painterResource(id = R.drawable.sol)
}