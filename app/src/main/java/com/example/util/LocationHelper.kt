package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {

  data class UserLocationResult(
    val latitude: Double,
    val longitude: Double,
    val cityName: String
  )

  @SuppressLint("MissingPermission")
  suspend fun getCurrentLocation(context: Context): UserLocationResult? = withContext(Dispatchers.IO) {
    try {
      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return@withContext null

      var bestLocation: Location? = null

      val providers = locationManager.getProviders(true)
      for (provider in providers) {
        val location = locationManager.getLastKnownLocation(provider) ?: continue
        if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
          bestLocation = location
        }
      }

      if (bestLocation != null) {
        val cityName = resolveCityName(context, bestLocation.latitude, bestLocation.longitude)
        return@withContext UserLocationResult(
          latitude = bestLocation.latitude,
          longitude = bestLocation.longitude,
          cityName = cityName
        )
      }
    } catch (e: SecurityException) {
      // Permission not granted
    } catch (e: Exception) {
      e.printStackTrace()
    }
    null
  }

  private fun resolveCityName(context: Context, latitude: Double, longitude: Double): String {
    return try {
      val geocoder = Geocoder(context, Locale.getDefault())
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        formatAddress(addresses?.firstOrNull())
      } else {
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        formatAddress(addresses?.firstOrNull())
      }
    } catch (e: Exception) {
      "مقام: %.2f°, %.2f°".format(Locale.US, latitude, longitude)
    }
  }

  private fun formatAddress(address: Address?): String {
    if (address == null) return "موجودہ مقام"
    val locality = address.locality ?: address.subAdminArea ?: address.adminArea
    val country = address.countryName ?: ""
    return if (!locality.isNullOrEmpty() && country.isNotEmpty()) {
      "$locality، $country"
    } else if (!locality.isNullOrEmpty()) {
      locality
    } else {
      country.ifEmpty { "موجودہ مقام" }
    }
  }
}
