package com.learning.pathpilotroutex.retofit


data class DirectionsResponse(
    val routes: List<Route>
)

// Single route between origin and destination
data class Route(
    // Overview_polyline: contains the encoded polyline
    val overview_polyline: PolyLine
)

// Contains the compressed representation of the route's path
data class PolyLine(
    val points: String
)
