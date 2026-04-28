package com.learning.pathpilotroutex.repositories

import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val userLocation: StateFlow<LatLng?>
    val hasLocationPermission: MutableState<Boolean>

    fun startLocationUpdates()
    fun stopLocationUpdates()

    fun debugSetLocation()
}