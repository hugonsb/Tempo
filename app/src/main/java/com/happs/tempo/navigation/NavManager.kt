package com.happs.tempo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.happs.tempo.model.WeatherModel
import com.happs.tempo.view.HomeView
import com.happs.tempo.view.PrevisaoView

@Composable
fun NavManager(data: WeatherModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "homeView") {
        composable("homeView") {
            HomeView(data, navController)
        }

        composable("previsaoView") {
            PrevisaoView(data, navController)
        }
    }
}