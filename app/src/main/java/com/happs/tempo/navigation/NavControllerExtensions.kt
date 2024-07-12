package com.happs.tempo.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController


val NavController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED