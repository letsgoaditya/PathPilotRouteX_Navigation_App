package com.learning.pathpilotroutex.repositories

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.learning.pathpilotroutex.utilities.DestinationMarker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MarkerRepositoryImplementation @Inject constructor(
    private val firebase: FirebaseFirestore
) : MarkerRepository {
    override suspend fun addMarker(marker: LatLng, name: String) {
        try {
            val data = hashMapOf(
                "latitude" to marker.latitude,
                "longitude" to marker.longitude,
            )
            firebase.collection("markers").document(name).set(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Flow: is a cold asynchronous stream that emits
    // values over time. It is a Kotlin coroutine-based
    // Asynchronous, Reactive Streams, Lifecycle-aware
    override suspend fun getAllMarkersFromFirestore(): Flow<List<DestinationMarker>> =
        callbackFlow {

            // This code does not work for real time changes but if static it works fine
//        val result = firebase.collection("markers").get().await()
//        return result.map { document ->
//            DestinationMarker(
//                id = document.id,
//                document.getDouble("latitude") ?: 0.0,
//                document.getDouble("longitude") ?: 0.0,
//                document.getString("title") ?: ""
//            )
//        }

            // for real time updates we use flow in coroutines
            val listener = firebase.collection("markers").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val markers = snapshot.map { myMarker ->
                        DestinationMarker(
                            id = myMarker.id,
                            myMarker.getDouble("latitude") ?: 0.0,
                            myMarker.getDouble("longitude") ?: 0.0,
                            myMarker.getString("title") ?: ""
                        )


                    }
                    // non-blocking emission and send the list of markers to the flow
                    // used in callbackFlow to safely emit values without suspending
                    // the coroutine

                    trySend(markers)
                } else {
                    trySend(emptyList())
                }

            }
// suspends the coroutine until the flow is cancelled, then runs cleanup code
            awaitClose { listener.remove() }

        }

}