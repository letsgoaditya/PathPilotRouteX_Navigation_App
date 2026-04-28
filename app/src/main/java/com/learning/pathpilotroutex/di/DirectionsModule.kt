package com.learning.pathpilotroutex.di

import com.learning.pathpilotroutex.repositories.DirectionsRepository
import com.learning.pathpilotroutex.repositories.DirectionsRepositoryImplementation
import com.learning.pathpilotroutex.retofit.DirectionsService
import com.learning.pathpilotroutex.retofit.RetrofitProvider
import com.learning.pathpilotroutex.BuildConfig;
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DirectionsModule {

    @Provides
    @Singleton
    fun provideDirectionsService(): DirectionsService {
        return RetrofitProvider.createDirectionsService(
            baseUrl = "https://maps.googleapis.com/maps/api/"
        )
    }

    @Provides
    @Singleton
    fun provideDirectionsRepository(
        directionsService: DirectionsService
    ): DirectionsRepository {

        return DirectionsRepositoryImplementation(
            apiKey = provideMapsApiKey(), directionsService = directionsService
        )

    }

    @Provides
    @Named("maps_api_key")
    fun provideMapsApiKey(): String = BuildConfig.MAPS_API_KEY;


}