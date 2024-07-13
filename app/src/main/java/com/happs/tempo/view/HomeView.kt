package com.happs.tempo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.happs.tempo.R
import com.happs.tempo.model.WeatherModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeView(data: WeatherModel, navHostController: NavHostController) {
    val currentHour = LocalTime.now().hour

    // Encontrar o índice do item da hora atual na lista de dados
    val currentIndex = data.hourly.time.indexOfFirst {
        it.substring(11, 13).toInt() == currentHour
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF5B4694),
                        Color(0xFF623E6F)
                    )
                )
            )
            .padding(10.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            CurrentWeatherHourItem(
                time = data.hourly.time[currentIndex],
                temperature = data.hourly.temperature_2m[currentIndex],
                humidity = data.hourly.relative_humidity_2m[currentIndex],
                apparentTemperature = data.hourly.apparent_temperature[currentIndex],
                precipitationProbability = data.hourly.precipitation_probability[currentIndex],
                rain = data.hourly.rain[currentIndex],
                cloudCoverHigh = data.hourly.cloud_cover_high[currentIndex]
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Hoje", fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDD302)
            )

            Text(
                modifier = Modifier.clickable { navHostController.navigate("previsaoView") {launchSingleTop = true} },
                text = "Próximos 6 dias >", fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow {
            items(data.hourly.time.take(24).size) { index ->
                val hour = data.hourly.time[index].substring(11, 13).toInt()
                val isCurrentHour = (hour == currentHour)

                WeatherHourItem(
                    time = data.hourly.time[index],
                    temperature = data.hourly.temperature_2m[index],
                    rain = data.hourly.rain[index],
                    cloudCoverHigh = data.hourly.cloud_cover_high[index],
                    isCurrentHour = isCurrentHour,
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherHourItem(
    time: String,
    temperature: Double,
    humidity: Int,
    apparentTemperature: Double,
    precipitationProbability: Int,
    rain: Double,
    cloudCoverHigh: Int
) {

    val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MM | HH:mm"))
    val currentHour = dateTime.hour

    val isNightTime = currentHour in 18..23 || currentHour in 0..4

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val painter = getWeatherIconPainter(isNightTime, rain, cloudCoverHigh)

        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painter,
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "$temperature°C",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = formattedDateTime, fontSize = 20.sp, color = Color.White)

        Spacer(modifier = Modifier.height(60.dp))

        Card {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF331763))
                    .padding(vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.chance_chuva),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Chance de chuva",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$precipitationProbability%",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.humidade_relativa),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Humidade relativa",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$humidity%",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.sensacao_termica),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Sensação térmica",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$apparentTemperature°C",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherHourItem(
    time: String,
    temperature: Double,
    rain: Double,
    cloudCoverHigh: Int,
    isCurrentHour: Boolean,
) {

    val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME).hour

    val isNightTime = dateTime in 18..23 || dateTime in 0..4

    val painter = getWeatherIconPainter(isNightTime, rain, cloudCoverHigh)

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(end = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .background(if (isCurrentHour) Color(0xFF671EE6) else Color(0xFF331763))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = time.substring(11, 16), color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(3.dp))
            Image(
                modifier = Modifier.size(40.dp),
                painter = painter,
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "$temperature °C", color = Color.White, fontSize = 15.sp)
        }
    }
}

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