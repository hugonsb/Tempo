package com.happs.tempo.di

import com.happs.tempo.api.OpenMeteoApi
import com.happs.tempo.api.RetrofitInstance
import com.happs.tempo.viewModel.TempoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Network Module
val networkModule = module {
    single<OpenMeteoApi> { RetrofitInstance.openMeteoApi }
}

// ViewModel Module
val viewModelModule = module {
    viewModel { TempoViewModel(get()) }
    //viewModelOf(::TempoViewModel)
}