package com.happs.tempo

import android.app.Application
import com.happs.tempo.di.networkModule
import com.happs.tempo.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class TempoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger(level = Level.DEBUG) //ver logs do koin
            androidContext(this@TempoApplication)
            modules(networkModule, viewModelModule)
        }
    }
}