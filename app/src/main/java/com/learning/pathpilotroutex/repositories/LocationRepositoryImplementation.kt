package com.learning.pathpilotroutex.repositories

import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class LocationRepositoryImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationRepository {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    override val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    override val hasLocationPermission = mutableStateOf(false)

    // FusedLocationProviderClient: is a Google Play Services API that
    // provides access to the device's location services
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Handling incoming location updates from Android's fused
    // location provider
    // callback: receives periodic location updates & updates an
    // observable state with the latest coordinates
    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { currentLocation ->
                _userLocation.value = LatLng(
                    currentLocation.latitude, currentLocation.longitude
                )
            }
        }
    }

    override fun startLocationUpdates() {
        // Location request Construction
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,5000
        ).apply{
            setMinUpdateIntervalMillis(5000)
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallBack,
                Looper.getMainLooper()
            )


        }catch (e: SecurityException){
            Log.v("TAGY","Permission Exception ${e.message}")

        }

    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack)
    }




    // Mocking location
    // In your repository
    override fun debugSetLocation() {
        _userLocation.value = LatLng(37.7749,-122.4194)
    }

}