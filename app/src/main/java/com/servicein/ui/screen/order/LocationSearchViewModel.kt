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
import com.google.android.libraries.places.api.net.PlacesClient

class LocationSearchViewModel() : ViewModel() {
    private val _searchResults = mutableStateListOf<AutocompletePrediction>()
    val searchResults: List<AutocompletePrediction> = _searchResults

    private var sessionToken: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private var placesClient: PlacesClient? = null

    private fun getPlacesClient(context: Context): PlacesClient {
        if (placesClient == null) {
            placesClient = Places.createClient(context)
        }
        return placesClient!!
    }

    fun searchPlaces(context: Context, query: String) {
        val client = getPlacesClient(context)

        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .setCountries("id")
            .build()

        client.findAutocompletePredictions(request)
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
        val client = getPlacesClient(context)
        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LOCATION)).build()

        client.fetchPlace(request)
            .addOnSuccessListener { response ->
                response.place.location?.let { onLatLngRetrieved(it) }
                // Regenerate session token setelah fetch place untuk menghindari reuse
                sessionToken = AutocompleteSessionToken.newInstance()
            }
            .addOnFailureListener { exception ->
                Log.e("LocationSearchViewModel", "Error fetching place details", exception)
                // Regenerate session token meskipun gagal
                sessionToken = AutocompleteSessionToken.newInstance()
            }
    }

    fun clearSearchResults() {
        _searchResults.clear()
    }

    override fun onCleared() {
        super.onCleared()
        // Shutdown PlacesClient saat ViewModel di-destroy
        placesClient = null
    }
}