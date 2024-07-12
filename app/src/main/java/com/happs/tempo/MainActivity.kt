package com.happs.tempo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.happs.tempo.constant.Const.Companion.permissions
import com.happs.tempo.model.MyLatLong
import com.happs.tempo.network.NetworkResponse
import com.happs.tempo.ui.theme.TempoTheme
import com.happs.tempo.view.HomeToday
import com.happs.tempo.viewModel.TempoViewModel
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false
    private var isUpdated = false

    override fun onResume() {
        super.onResume()
        if (locationRequired) startLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 100
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(100)
            .build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationClient()
        setContent {

            var currentLocation by remember { mutableStateOf(MyLatLong(0.0, 0.0)) }
            val tempoViewModel = ViewModelProvider(this)[TempoViewModel::class.java]

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        currentLocation = MyLatLong(
                            location.latitude,
                            location.longitude
                        )
                        if (!isUpdated) {
                            isUpdated = true
                            tempoViewModel.updateLocation(location.latitude, location.longitude)
                        }
                    }
                }
            }

            val ctx = LocalContext.current
            TempoTheme {
                MyScreen(ctx, currentLocation, tempoViewModel)
            }

        }
    }

    private fun initLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @Composable
    private fun MyScreen(ctx: Context, currentLocation: MyLatLong, tempoViewModel: TempoViewModel) {

        val weatherResult by tempoViewModel.weatherResult.observeAsState()

        val launcherMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionMap ->
            val areGranted = permissionMap.values.reduce { accepted, next -> accepted && next }
            if (areGranted) {
                locationRequired = true
                startLocationUpdate()
                Toast.makeText(ctx, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(ctx, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(key1 = currentLocation, block = {
            coroutineScope {
                if (permissions.all {
                        ContextCompat.checkSelfPermission(
                            ctx,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }) {
                    startLocationUpdate()
                } else {
                    launcherMultiplePermissions.launch(permissions)
                }
            }
        })

        when (val result = weatherResult) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }

            is NetworkResponse.Loading -> {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }

            is NetworkResponse.Success -> {
                HomeToday(result.data)
            }

            null -> {}
        }
    }
}