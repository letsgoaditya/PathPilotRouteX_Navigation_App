package com.learning.pathpilotroutex.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.exp

@Composable
fun ExpandableFAB(
    modifier: Modifier = Modifier,
    onDrawRoute: () -> Unit,
    onRequestRide: () -> Unit,
    onPlaceMultipleMarkers: () -> Unit,
    onMultipleMarkersRoute: () -> Unit,
    onClearMap: () -> Unit

) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.wrapContentSize()
    ) {
        // Expanded Button when FAB is clicked
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 70.dp)
            ) {

                ExtendedFloatingActionButton(
                    onClick = {
                    expanded = false
                    onRequestRide()
                },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Request Ride") })







                ExtendedFloatingActionButton(
                    onClick = { /* Draw a route */
                    expanded = false
                    onDrawRoute()
                },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = { Text("Draw route") })

                ExtendedFloatingActionButton(
                    onClick = { /* Adding markers to multi points list */
                    expanded = false
                    onPlaceMultipleMarkers()
                },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    text = { Text("Add Multi Markers") })

                ExtendedFloatingActionButton(
                    onClick = { /* Draw route between multiple markers */
                    expanded = false
                    onMultipleMarkersRoute()
                },
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    text = { Text("Draw Multi Route") })





                ExtendedFloatingActionButton(
                    onClick = { /* clears marker */
                    expanded = false
                    onClearMap()
                },
                    icon = { Icon(Icons.Default.Clear, contentDescription = null) },
                    text = { Text("Clear Map") })
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = null
            )
        }

    }

}