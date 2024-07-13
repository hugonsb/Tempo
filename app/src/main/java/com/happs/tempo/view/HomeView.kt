package com.happs.tempo.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.happs.tempo.util.getWeatherIconPainter
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeView(data: WeatherModel, navHostController: NavHostController) {
    val currentHour = LocalTime.now().hour

    // Encontrar o índice do item da hora atual na lista de dados
    var currentIndex by remember {
        mutableIntStateOf(data.hourly.time.indexOfFirst {
            it.substring(11, 13).toInt() == currentHour
        })
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        lazyListState.animateScrollToItem(currentIndex)
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
            Crossfade(
                targetState = currentIndex, animationSpec = tween(durationMillis = 800),
                label = ""
            ) { selectedIndex ->
                data.hourly.run {
                    CurrentWeatherHourItem(
                        time = time[selectedIndex],
                        temperature = temperature_2m[selectedIndex],
                        humidity = relative_humidity_2m[selectedIndex],
                        apparentTemperature = apparent_temperature[selectedIndex],
                        precipitationProbability = precipitation_probability[selectedIndex],
                        rain = rain[selectedIndex],
                        cloudCoverHigh = cloud_cover_high[selectedIndex]
                    )
                }
            }
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
                modifier = Modifier.clickable {
                    navHostController.navigate("previsaoView") {
                        launchSingleTop = true
                    }
                },
                text = "Próximos 6 dias >", fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(state = lazyListState) {
            items(data.hourly.time.take(24).size) { index ->
                val isSelected = (currentIndex == index)
                WeatherHourItem(
                    time = data.hourly.time[index],
                    temperature = data.hourly.temperature_2m[index],
                    rain = data.hourly.rain[index],
                    cloudCoverHigh = data.hourly.cloud_cover_high[index],
                    isSelectedHour = isSelected,
                    index = index,
                    onClick = { currentIndex = it }
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = formattedDateTime, fontSize = 20.sp, color = Color.White)

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Dados da sua localização atual\nopen-meteo.com",
            fontSize = 10.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp
        )


        Spacer(modifier = Modifier.height(40.dp))

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
    isSelectedHour: Boolean,
    index: Int,
    onClick: (Int) -> Unit
) {

    val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_LOCAL_DATE_TIME).hour

    val isNightTime = dateTime in 18..23 || dateTime in 0..4

    val painter = getWeatherIconPainter(isNightTime, rain, cloudCoverHigh)

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.padding(end = 10.dp),
        onClick = { onClick(index) }
    ) {
        Column(
            modifier = Modifier
                .background(if (isSelectedHour) Color(0xFF671EE6) else Color(0xFF331763))
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