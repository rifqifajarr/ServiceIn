package com.servicein.data.service

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.servicein.core.util.MapUtil.decodePolyline

class RouteService(
    private val api: ApiService = ApiConfig.apiConfig
) {
    suspend fun getRoutePolyline(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): List<LatLng> {
        val response = api.getDirections(
            origin = "${origin.latitude},${origin.longitude}",
            destination = "${destination.latitude},${destination.longitude}",
            apiKey = apiKey
        )
        Log.d("RouteService", "Response: $response")
        Log.d("RouteService", "Polyline: ${response.routes.first().overviewPolyline?.points}")

        val steps = response.routes.firstOrNull()?.legs?.flatMap { it.steps } ?: return emptyList()
        val allPoints = steps.flatMap { step ->
            step.polyline.points.let { decodePolyline(it) }
        }

        return allPoints
    }
}