package com.servicein.data.service

import com.servicein.data.response.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): DirectionsResponse
}