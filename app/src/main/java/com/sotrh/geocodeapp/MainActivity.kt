package com.sotrh.geocodeapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.sotrh.geocodeapp.service.GeocodeService
import com.sotrh.geocodeapp.service.LocationService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject

class MainActivity : AppCompatActivity() {

    class PermissionResult(val areAllPermissionsGranted: Boolean, val permissionRequestCode: Int, val permissions: Array<out String>, val grantResults: IntArray)

    private lateinit var permissionSubject: ReplaySubject<PermissionResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionSubject = ReplaySubject.create()
        LocationService.init(this)

        val textView = findViewById(R.id.text) as TextView
        val searchView = findViewById(R.id.search) as SearchView

        searchView.observeOnQueryTextSubmit().subscribe {
            if (!LocationService.isEnabled) {
                Toast.makeText(this@MainActivity, getString(R.string.error_location_not_enabled), Toast.LENGTH_LONG).show()
            } else if (it.isNullOrBlank()) {
                Toast.makeText(this@MainActivity, getString(R.string.error_input_address), Toast.LENGTH_LONG).show()
            } else {
                GeocodeService.getResultsWithAddress(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ addressResult ->
                            LocationService.observeLastKnownLocation().subscribe {
                                val R = 6371e3
                                val addressLocation = addressResult.results.first().geometry.location
                                val lat1Radians = it.latitude.toRadians()
                                val lat2Radians = addressLocation.lat.toRadians()
                                val deltaLat = (addressLocation.lat - it.latitude).toRadians()
                                val deltaLng = (addressLocation.lng - it.longitude).toRadians()

                                val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                                        Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2)

                                val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))

                                val greatCircleDistance = R * c

                                textView.text = "The distance between your current location and ${addressResult.results.first().formatted_address} is ${greatCircleDistance.km()} km\nDistance calculated using the haversine formula."
                            }
                        }, {
                            textView.text = it.message
                        })
            }
        }
    }

    fun observePermissionRequest(permissions: Array<out String>, permissionRequestCode: Int): Observable<PermissionResult> {
        val firstPermissionResults = permissions.map { ContextCompat.checkSelfPermission(this, it) }
        val deniedPermissions = permissions.filterIndexed { index, _ -> firstPermissionResults[index] == PackageManager.PERMISSION_DENIED }
        if (deniedPermissions.isEmpty()) {
            permissionSubject.onNext(PermissionResult(true, permissionRequestCode, permissions, firstPermissionResults.toIntArray()))
        } else {
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode)
        }

        return permissionSubject.hide()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val deniedPermissions = permissions.filterIndexed { index, _ -> grantResults[index] == PackageManager.PERMISSION_DENIED }
        permissionSubject.onNext(PermissionResult(deniedPermissions.isEmpty(), requestCode, permissions, grantResults))
    }

}
