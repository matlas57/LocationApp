package com.example.locationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

//context is the interface with global information about the app environment
class LocationUtils(val context: Context) {

    private val _fusedLocationProviderClient: FusedLocationProviderClient
        = LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission") //FusedLocationProviderClient must have permissions enabled, and creates a compile time error if its unsure. Lint says that we understand and have handled it
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longitude = it.longitude)
                    viewModel.updateLocation(location)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000).build() //request location every second which allows quick updates when location moves (routes)

        //The looper specifies which thread the request executes on
        _fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun hasLocationPermission(context: Context): Boolean {
        //Access the app permissions and check if its granted
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
               &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun reverseGeocodeLocation(location: LocationData) : String {
        val geocoder = Geocoder(context, Locale.getDefault()) //Locale sets the address format, default uses the locale based on default lang and loc
        val coordinates = LatLng(location.latitude, location.longitude) //create a coordinates object to join lat and lng
        val addresses:MutableList<Address>? = //load coordinates into a mutable list of address types that are found using the geocoder based on lat and lng
            geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
        //if there are addresses in the list than get the first address
        return if (addresses?.isNotEmpty() == true){
            addresses[0].getAddressLine(0)
        } else {
            "Address Not Found"
        }
    }
}