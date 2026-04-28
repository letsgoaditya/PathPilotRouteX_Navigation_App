package com.learning.pathpilotroutex.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.learning.pathpilotroutex.repositories.DirectionsRepository
import com.learning.pathpilotroutex.repositories.LocationRepository
import com.learning.pathpilotroutex.repositories.MarkerRepository
import com.learning.pathpilotroutex.utilities.DestinationMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val markerRepository: MarkerRepository,
    private val locationRepository: LocationRepository,
    private val directionsRepository: DirectionsRepository
) : ViewModel() {
    fun addMarker(
        marker: LatLng, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                markerRepository.addMarker(marker, name)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // list of markers in firebase
    // for compose and non - compose UI
//    private val _markers = MutableStateFlow<List<DestinationMarker>>(emptyList())
//    val markersInFirebase: StateFlow<List<DestinationMarker>> = _markers.asStateFlow()

    // For Compose-Only UI
    // creates an observable, mutable list of DestinationMarker items that Jetpack
    // Compose can observe and update its UI (recomposition)
    // any composable that reads 'markers' will recompose when you: add, remove, update items
    val markers: SnapshotStateList<DestinationMarker> = mutableStateListOf()
    private var listenerActive = false

    init {
        loadMarkersFromFirestore()
    }

    fun loadMarkersFromFirestore() {
        if (listenerActive) return

        viewModelScope.launch {
            markerRepository.getAllMarkersFromFirestore().collectLatest { newMarkers ->
                markers.clear()
                markers.addAll(newMarkers)

            }
        }
        listenerActive = true
    }

    // Get Current location
    val userLocation = locationRepository.userLocation
    val hasLocationPermission = locationRepository.hasLocationPermission

    fun startLocationUpdates() {
        if (hasLocationPermission.value) {
            locationRepository.startLocationUpdates()
            Log.v("TAGY", "start location updates ()")
        }
    }

    override fun onCleared() {
        locationRepository.stopLocationUpdates()
    }

    fun onPermissionResult(granted: Boolean) {
        hasLocationPermission.value = granted
        if (granted) startLocationUpdates()
    }

    // Directions API
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation.asStateFlow()
    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }


    // observable data stream for tracking route points
    // Consumers can observe changes but can't modify directly
    private val _routePoints = MutableStateFlow<List<LatLng>?>(null)
    val routePoints: StateFlow<List<LatLng>?> = _routePoints.asStateFlow()

    fun fetchRouteFromCurrentPositionToMarker(marker: LatLng) {

        viewModelScope.launch {

            locationRepository.userLocation.value?.let { currentLocation ->
                val route = directionsRepository.getRoute(
                    origin = currentLocation, destination = marker
                )

                if (route != null) {
                    _routePoints.value = route
                } else {
                    Log.v("TAGY", "route is null")
                }
            } ?: run {
                Log.v("TAGY", "user location is null")
            }
        }
    }

    // Multiple Markers Route
    private val _multiRoutePoints = MutableStateFlow<List<LatLng>?>(null)
    val multiRoutePoints: StateFlow<List<LatLng>?> = _multiRoutePoints.asStateFlow()

    // adding new markers to multiplePoints Route
    fun addMarkerToMultiPointsRoute(newPoint: LatLng) {
        val currentList = _multiRoutePoints.value.orEmpty().toMutableList()
        currentList.add(newPoint)
        _multiRoutePoints.value = currentList
    }

    fun fetchRouteForMultipleMarkers() {
        viewModelScope.launch {
            val route = directionsRepository.getRouteForMultipleMarkers(
                origin = _multiRoutePoints.value?.first()!!,
                waypoints = _multiRoutePoints.value?.drop(1)    // remove origin
                    ?.dropLast(1)  // remove destination
                    ?: emptyList(),
                destination = _multiRoutePoints.value?.lastOrNull()!!
            )

            if (route != null) {
                _multiRoutePoints.value = route
            } else {
                Log.v("TAGY", "route is null")

            }


        }

    }


    // Clear Route
    fun clearRoute() {
        _routePoints.value = emptyList()
        _multiRoutePoints.value = emptyList()
        _selectedLocation.value = null
    }

}