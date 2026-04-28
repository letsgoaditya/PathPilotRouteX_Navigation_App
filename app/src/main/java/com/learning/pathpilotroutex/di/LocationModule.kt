package com.learning.pathpilotroutex.di

import android.content.Context
import com.learning.pathpilotroutex.repositories.LocationRepository
import com.learning.pathpilotroutex.repositories.LocationRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ): LocationRepository = LocationRepositoryImplementation(context)


}