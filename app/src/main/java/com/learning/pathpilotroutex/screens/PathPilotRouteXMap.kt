package com.learning.pathpilotroutex.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import com.learning.pathpilotroutex.location.RequestLocationPermission
import com.learning.pathpilotroutex.utilities.ExpandableFAB
import com.learning.pathpilotroutex.viewmodels.MapViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathPilotRouteXMap() {

    val cameraPositionState = rememberCameraPositionState()

    // Testing addMarker()
    val viewModel: MapViewModel = hiltViewModel()
    viewModel.addMarker(LatLng(28.61394, 77.20902), "New Delhi", {}, {})
    // getting markers from database
//    val markers by viewModel.markersInFirebase.collectAsState()

    val markersInFirestore = remember { viewModel.markers }

    // Current location
    val userLocation by viewModel.userLocation.collectAsState()
    RequestLocationPermission(viewModel, {
        viewModel.startLocationUpdates()
    })
    val context = LocalContext.current

    // Directions API
    // converting the flow into state object
    // your composable will automatically update whenever new route points
    // are emitted
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }


    // Multiple Marker selection status
    var isAddingMarkersEnabled by remember { mutableStateOf(false) }
    val multipleMarkersList = remember { mutableStateListOf<LatLng>() }
    val routeMultiPoints by viewModel.multiRoutePoints.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()


    Scaffold(
        contentWindowInsets = WindowInsets(0), // remove system padding
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PathPilot RouteX", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF28736C)
                )
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { myMarker ->
                    markerPosition = myMarker
                    viewModel.setSelectedLocation(myMarker)

                    // If Multiple markers enabled
                    if (isAddingMarkersEnabled) {
                        multipleMarkersList.add(myMarker)
                        // store marker to 'waypoints' API URL
                        viewModel.addMarkerToMultiPointsRoute(myMarker)
                    }

                }

            ) {
                // Current location marker it get updated after 10s automatically as set it as it is.
                userLocation?.let { myCurrentLocation ->
                    Marker(
                        state = MarkerState(position = myCurrentLocation),
                        title = "Current Location",
                        snippet = "Current Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                        onClick = {
                            Toast.makeText(
                                context,
                                "Current Location Marker:- ${myCurrentLocation}",
                                Toast.LENGTH_LONG
                            ).show()
                            true
                        })
                }


                // Marker from firebase
                markersInFirestore.forEach { marker ->
                    Marker(
                        state = MarkerState(position = marker.toLatLng()),
                        title = marker.id,
                        snippet = marker.title,
                        onClick = {
                            Toast.makeText(
                                context, "Firebase Locations Marker:- ${marker}", Toast.LENGTH_LONG
                            ).show()
                            viewModel.setSelectedLocation(marker.toLatLng())

                            if (isAddingMarkersEnabled) {
                                multipleMarkersList.add(marker.toLatLng())
                                viewModel.addMarkerToMultiPointsRoute(marker.toLatLng())
                            }



                            false

                        })
                }

                // the marker clicked position on the map
                markerPosition?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Location",
                        snippet = "${it.latitude}, ${it.longitude}",
                        onClick = {
                            viewModel.setSelectedLocation(markerPosition!!)
                            false
                        })
                }

                routePoints?.let { routePoints ->
                    Polyline(
                        points = routePoints, color = Color.Red, width = 8f
                    )

                }


                // Draw multipoints route polyline
                routeMultiPoints.let { routePoints ->
                    if (routePoints != null) {
                        Polyline(
                            points = routePoints, color = Color.Magenta, width = 10f
                        )
                    }
                }


                // Display Multiple markers on map
                multipleMarkersList.forEach { marker ->
                    Marker(
                        state = MarkerState(marker),
                        title = "Selected Location",
                        snippet = "${marker.latitude}, ${marker.longitude}",
                        onClick = {
                            viewModel.setSelectedLocation(marker)
                            false
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                    )
                }


            }

            // Expandable FAB
            ExpandableFAB(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp), {
                if (selectedLocation != null) {
                    viewModel.fetchRouteFromCurrentPositionToMarker(selectedLocation!!)

                }

            }, {
                selectedLocation?.let {
                    viewModel.addMarker(
                        marker = it,
                        name = "Destination : ${it.latitude}, ${it.longitude}",
                        {},
                        {})
                }
            }, {
                // Enable / disable adding markers to the list
                isAddingMarkersEnabled = !isAddingMarkersEnabled
            }, {
                if (isAddingMarkersEnabled) {
                    viewModel.fetchRouteForMultipleMarkers()
                    isAddingMarkersEnabled = false
                }
            }, {
                viewModel.clearRoute()
                multipleMarkersList.clear()
                markerPosition = null
            })

            // Disappearing Scale Bar
            DisappearingScaleBar(
                modifier = Modifier
                    .padding(top = 50.dp, start = 10.dp)
                    .align(Alignment.TopStart),
                cameraPositionState = cameraPositionState
            )


            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 100.dp)
                    .size(30.dp),
                onClick = {
                    userLocation?.let { myCurrentLocation ->
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        myCurrentLocation,
                                        10f,
                                        0f,
                                        0f,
                                    )
                                ), durationMs = 5000
                            )
                        }
                    }

                },
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Home, "Go to Home", modifier = Modifier.size(20.dp))
            }


        }

    }


}