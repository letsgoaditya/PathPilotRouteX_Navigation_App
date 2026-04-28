package com.learning.pathpilotroutex.repositories

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.learning.pathpilotroutex.retofit.DirectionsService
import javax.inject.Inject
import javax.inject.Named

class DirectionsRepositoryImplementation @Inject constructor(
    @Named("maps_api_key") private val apiKey: String,
    private val directionsService: DirectionsService
) : DirectionsRepository {

    override suspend fun getRoute(
        origin: LatLng,
        destination: LatLng
    ): List<LatLng>? {

        return try {
            // 1- converting origin & destination into strings
            //    for API call
            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"

            // 2- API Call
            val response = directionsService.getDirections(
                origin = originStr, destination = destinationStr, apiKey = apiKey
            )

            // 3- Response Processing
            response.routes.firstOrNull()         // access the first route in response
                ?.overview_polyline?.points       // containing encoded points
                ?.let {

                    // 4- Polyline decoding
                    PolyUtil.decode(it).map { latLng ->
                        LatLng(
                            latLng.latitude, latLng.longitude
                        )
                    }
                }
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun getRouteForMultipleMarkers(
        origin: LatLng, waypoints: List<LatLng>, destination: LatLng
    ): List<LatLng>? {
        try {
            val origin = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"

            // Transform waypoints to string
            val waypoints = if (waypoints.size > 2) {
                "optimize:true|" + waypoints.subList(1, waypoints.size - 1).joinToString(separator = "|") {
                    "${it.latitude},${it.longitude}"
                }
            } else {
                waypoints.joinToString(separator = "|") {
                    "${it.latitude},${it.longitude}"
                }
            }

            val response = directionsService.getDirections(
                origin = origin,
                destination = destinationStr,
                waypoints = waypoints,
                apiKey = apiKey

            )
            val points = response.routes[0].overview_polyline.points
            return PolyUtil.decode(points).map {
                LatLng(
                    it.latitude, it.longitude
                )
            }


        } catch (e: Exception) {
            return null
        }
    }


}