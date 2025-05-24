package com.servicein.core.util

import android.content.Context
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale

object MapUtil {
    fun getAddressFromLocation(
        context: Context,
        latLng: LatLng,
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var address = ""

        try {
            val geocodeListener = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Geocoder.GeocodeListener { addresses ->
                    address = processAddresses(addresses)
                }
            } else {
                null
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && geocodeListener != null) {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1, geocodeListener)
            } else {
                @Suppress("DEPRECATION")
                val addresses: MutableList<Address>? =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                address = processAddresses(addresses ?: emptyList())
            }
        } catch (e: IOException) {
            Log.e("FormViewModel", "Layanan geocoder tidak tersedia", e)
            address = ("Gagal mendapatkan alamat: Layanan tidak tersedia")
        } catch (e: IllegalArgumentException) {
            Log.e("FormViewModel", "Koordinat tidak valid", e)
            address = ("Gagal mendapatkan alamat: Koordinat tidak valid")
        }

        return address
    }

    fun processAddresses(addresses: List<Address>): String {
        if (addresses.isNotEmpty()) {
            val address: Address = addresses[0]
            val addressParts = mutableListOf<String>()

            address.thoroughfare?.let {
                addressParts.add(it)
            }

            if (address.thoroughfare != null) {
                address.subThoroughfare?.let {
                    addressParts.add(it)
                }
            }

            if (address.thoroughfare == null && address.featureName != null && !isPlusCode(address.featureName)) {
                addressParts.add(address.featureName)
            }

            address.subLocality?.let {
                if (!addressParts.contains(it) && !isPlusCode(it)) {
                    addressParts.add(it)
                }
            }

            address.locality?.let {
                if (!addressParts.contains(it) && !isPlusCode(it)) {
                    addressParts.add(it)
                }
            }

            address.subAdminArea?.let {
                if (!addressParts.contains(it) && !isPlusCode(it)) {
                    addressParts.add(it)
                }
            }

            address.adminArea?.let {
                if (!addressParts.contains(it) && !isPlusCode(it)) {
                    addressParts.add(it)
                }
            }

            if (addressParts.isNotEmpty()) {
                address.postalCode?.let {
                    if (!isPlusCode(it)) {
                        addressParts.add(it)
                    }
                }
            }

            if (addressParts.isEmpty() || addressParts.size < 2) {
                address.countryName?.let {
                    if (!addressParts.contains(it) && !isPlusCode(it)) {
                        addressParts.add(it)
                    }
                }
            }


            return if (addressParts.isNotEmpty()) {
                (addressParts.joinToString(separator = ", "))
            } else {
                val firstLine = address.getAddressLine(0)
                if (firstLine != null && !isPlusCode(firstLine)) {
                    (firstLine)
                } else {
                    ("Detail alamat tidak tersedia")
                }
            }
        } else {
            return ("Alamat tidak ditemukan")
        }
    }

    private fun isPlusCode(text: String?): Boolean {
        return text?.matches(Regex("^[2-9CFGHJMPQRVWX]{4}\\+[2-9CFGHJMPQRVWX]{2,}([2-9CFGHJMPQRVWX]{1})?\$")) ?: false || // Global Plus Code
                text?.matches(Regex("^[2-9CFGHJMPQRVWX]{2,}\\+[2-9CFGHJMPQRVWX]{2,}$")) ?: false // Local Plus Code
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val p = LatLng(lat / 1E5, lng / 1E5)
            poly.add(p)
        }

        return poly
    }

    @Composable
    fun rememberCustomMarkerIcon(context: Context, @DrawableRes resId: Int): BitmapDescriptor {
        val bitmap = remember(resId) {
            val drawable = ContextCompat.getDrawable(context, resId)
            val width = 82
            val height = 100
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, width, height)
            drawable?.draw(canvas)
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
        return bitmap
    }

}