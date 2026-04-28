package com.learning.pathpilotroutex.location

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.learning.pathpilotroutex.viewmodels.MapViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    viewModel: MapViewModel,
    onPermissionGranted: () -> Unit = {}
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            viewModel.hasLocationPermission.value = true
            onPermissionGranted()
        } else {
            // Request permissions if not granted
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
}