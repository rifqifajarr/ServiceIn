package com.servicein.data.response

import com.squareup.moshi.Json

data class DirectionsResponse(
    @Json(name = "routes")
    val routes: List<Route>
)

data class Route(
    @Json(name = "overview_polyline")
    val overviewPolyline: OverviewPolyline?,

    @Json(name = "legs")
    val legs: List<Leg>?
)

data class OverviewPolyline(
    @Json(name = "points")
    val points: String
)

data class Leg(
    @Json(name = "distance")
    val distance: TextValue,

    @Json(name = "duration")
    val duration: TextValue,

    @Json(name = "start_address")
    val startAddress: String,

    @Json(name = "end_address")
    val endAddress: String,

    @Json(name = "start_location")
    val startLocation: LatLng,

    @Json(name = "end_location")
    val endLocation: LatLng,

    @Json(name = "steps")
    val steps: List<Step>
)

data class Step(
    @Json(name = "distance")
    val distance: TextValue,

    @Json(name = "duration")
    val duration: TextValue,

    @Json(name = "start_location")
    val startLocation: LatLng,

    @Json(name = "end_location")
    val endLocation: LatLng,

    @Json(name = "polyline")
    val polyline: OverviewPolyline,

    @Json(name = "html_instructions")
    val htmlInstructions: String,

    @Json(name = "travel_mode")
    val travelMode: String,

    @Json(name = "maneuver")
    val maneuver: String? = null
)

data class TextValue(
    val text: String,
    val value: Int
)

data class LatLng(
    val lat: Double,
    val lng: Double
)