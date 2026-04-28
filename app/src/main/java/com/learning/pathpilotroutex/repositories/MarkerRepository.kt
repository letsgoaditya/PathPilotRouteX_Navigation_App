package com.learning.pathpilotroutex.repositories

import com.google.android.gms.maps.model.LatLng
import com.learning.pathpilotroutex.utilities.DestinationMarker
import kotlinx.coroutines.flow.Flow


interface MarkerRepository {

    suspend fun addMarker(
        marker : LatLng,
        name : String
    )

    suspend fun getAllMarkersFromFirestore(): Flow<List<DestinationMarker>>

}