package com.learning.pathpilotroutex.di

import com.google.firebase.firestore.FirebaseFirestore
import com.learning.pathpilotroutex.repositories.MarkerRepository
import com.learning.pathpilotroutex.repositories.MarkerRepositoryImplementation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideMarkerRepository(firestore: FirebaseFirestore): MarkerRepository {
        return MarkerRepositoryImplementation(firestore)
    }

}



