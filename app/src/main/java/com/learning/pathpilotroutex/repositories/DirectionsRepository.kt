package com.learning.pathpilotroutex.repositories

import com.google.android.gms.maps.model.LatLng

interface DirectionsRepository {

    // Fetch the route between the current position and a marker
    suspend fun getRoute(
        origin: LatLng,
        destination: LatLng): List<LatLng>?

    // Fetch the route between multiple markers
    suspend fun getRouteForMultipleMarkers(
        origin: LatLng,
        waypoints: List<LatLng>,
        destination: LatLng
    ):List<LatLng>?


}