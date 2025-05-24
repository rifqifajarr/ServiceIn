package com.servicein.ui.screen.order

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

class LocationSearchViewModel() : ViewModel() {
    private val _searchResults = mutableStateListOf<AutocompletePrediction>()
    val searchResults: List<AutocompletePrediction> = _searchResults

    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    fun searchPlaces(context: Context, query: String) {
        val placesClient = Places.createClient(context)

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .setCountries("id")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _searchResults.clear()
                _searchResults.addAll(response.autocompletePredictions)
            }
            .addOnFailureListener { exception ->
                Log.e("LocationSearchViewModel", "Error searching for places", exception)
                _searchResults.clear()
            }
    }

    fun getPlaceLatLng(
        context: Context,
        placeId: String,
        onLatLngRetrieved: (LatLng) -> Unit
    ) {
        val placesClient = Places.createClient(context)
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LOCATION)).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                response.place.location?.let { onLatLngRetrieved(it) }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationSearchViewModel", "Error fetching place details", exception)
            }
    }
}