package com.learning.pathpilotroutex.retofit

import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsService {

//    Base URl: https://maps.googleapis.com/maps/api/
//    End Point:   directions/json
//    ?destination=Montreal
//    &origin=Toronto
//    &key=ApiKeyOfProject

    // All methods in this interface will be implemented by Retrofit
    // and converted to HTTP Calls
    @GET("directions/json")
    suspend fun getDirections(
        @Query("destination") destination: String,
        @Query("origin") origin: String,
        @Query("waypoints") waypoints: String? = null,
        @Query("key") apiKey: String
    ): DirectionsResponse


}