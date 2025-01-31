package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    //setter for _location to be called in LocationUtils
    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

}