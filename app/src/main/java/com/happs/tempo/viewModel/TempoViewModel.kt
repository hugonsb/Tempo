package com.happs.tempo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happs.tempo.api.OpenMeteoApi
import com.happs.tempo.model.WeatherModel
import com.happs.tempo.network.NetworkResponse
import kotlinx.coroutines.launch

class TempoViewModel(private val openMeteoApi: OpenMeteoApi) : ViewModel() {

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun updateLocation(latitude: Double, longitude: Double) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val queryParams = mapOf(
                    "latitude" to latitude.toString(),
                    "longitude" to longitude.toString(),
                    "hourly" to "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,rain,cloud_cover_high",
                    "timezone" to "America/Sao_Paulo"
                )

                val response = openMeteoApi.getWeatherData(queryParams)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Erro ao carregar dados.")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Erro ao carregar dados.")
            }
        }
    }
}
