package com.happs.tempo.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.happs.tempo.navigation.canGoBack
import com.happs.tempo.util.getWeatherIconPainter
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun PrevisaoView(data: WeatherModel, navHostController: NavHostController) {

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .padding(10.dp)
            .verticalScroll(scrollState)
    ) {
        TopAppBar(navHostController)

        Spacer(modifier = Modifier.height(10.dp))

        // Dividindo a lista em blocos de 24 horas
        val timeChunk = data.hourly.time.chunked(24)
        val temperatureChunks = data.hourly.temperature_2m.chunked(24)
        val rainChunks = data.hourly.rain.chunked(24)
        val cloudCoverChunks = data.hourly.cloud_cover_high.chunked(24)

        val humidityChunks = data.hourly.relative_humidity_2m.chunked(24)
        val apparentTemperatureChunks = data.hourly.apparent_temperature.chunked(24)
        val precipitationProbabilityChunks = data.hourly.precipitation_probability.chunked(24)

        for (index in 1 until minOf(7, timeChunk.size)) {
            CardPrevisao(
                timeChunk[index],
                temperatureChunks[index],
                rainChunks[index],
                cloudCoverChunks[index],
                humidityChunks[index],
                apparentTemperatureChunks[index],
                precipitationProbabilityChunks[index]
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TopAppBar(navHostController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    if (navHostController.canGoBack) {
                        navHostController.popBackStack("homeView", false)
                    }
                },
            tint = Color.White,
            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Próximos 6 dias", fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFFFFF)
        )
    }
}

@Composable
fun CardPrevisao(
    timeChunk: List<String>,
    temperatureChunk: List<Double>,
    rainChunk: List<Double>,
    cloudCoverChunk: List<Int>,
    humidityChunks: List<Int>,
    apparentTemperatureChunks: List<Double>,
    precipitationProbabilityChunks: List<Int>
) {

    var expanded by remember { mutableStateOf(false) }

    val currentHour = LocalTime.now().hour

    val isNightTime = currentHour in 18..23 || currentHour in 0..4

    var currentIndex by remember {
        mutableIntStateOf(timeChunk.indexOfFirst {
            it.substring(11, 13).toInt() == currentHour
        })
    }

    val dateTime =
        LocalDateTime.parse(timeChunk[currentIndex], DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MM"))
    val formattedDateHour = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    val painter =
        getWeatherIconPainter(isNightTime, rainChunk[currentIndex], cloudCoverChunk[currentIndex])

    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(currentIndex)
    }

    Card(modifier = Modifier.clickable { expanded = !expanded }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF432667))
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedContent(
                targetState = currentIndex, label = "",
            ) { targetIndex ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$formattedDateTime\n$formattedDateHour",
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.fillMaxWidth(0.1f))


                        Image(
                            modifier = Modifier
                                .size(40.dp),
                            painter = painter,
                            contentDescription = null
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                text = "${temperatureChunk[currentIndex]} °C",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    }

                    if (expanded) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp, start = 10.dp, end = 10.dp, bottom = 30.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
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
                                    text = "${precipitationProbabilityChunks[targetIndex]}%",
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
                                    text = "${humidityChunks[targetIndex]}%",
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
                                    text = "${apparentTemperatureChunks[targetIndex]}%",
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

            if (expanded) {
                LazyRow(
                    modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                    state = lazyListState,
                ) {
                    items(timeChunk.size) { index ->
                        WeatherHourItemPrevisao(
                            time = timeChunk[index],
                            temperature = temperatureChunk[index],
                            rain = rainChunk[index],
                            cloudCoverHigh = cloudCoverChunk[index],
                            isSelectedHour = currentIndex == index,
                            index = index,
                            onClick = { currentIndex = it }
                        )
                    }
                }
                Icon(
                    tint = Color.White,
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_up_24),
                    contentDescription = null
                )
            } else {
                Icon(
                    tint = Color.White,
                    painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun WeatherHourItemPrevisao(
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