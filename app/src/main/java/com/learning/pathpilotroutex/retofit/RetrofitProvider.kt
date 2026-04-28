package com.learning.pathpilotroutex.retofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    fun createDirectionsService(
        baseUrl: String
    ): DirectionsService {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(DirectionsService::class.java)
    }

}