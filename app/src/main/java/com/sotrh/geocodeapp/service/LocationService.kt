package com.sotrh.geocodeapp.service

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import com.sotrh.geocodeapp.MainActivity
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by benjamin on 10/11/17
 */
object LocationService {
    private lateinit var context: Context
    private lateinit var locationManager: LocationManager

    private val locationSubject = BehaviorSubject.create<Location>()

    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private val PERMISSION_REQUEST_CODE = 0

    var isEnabled = false; private set

    fun init(activity: MainActivity) {
        LocationService.context = activity
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // ask for location permissions
        activity.observePermissionRequest(PERMISSIONS, PERMISSION_REQUEST_CODE)
                .subscribe({
                    isEnabled = it.areAllPermissionsGranted
                }, {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                })
    }

    fun observeLastKnownLocation(): Observable<Location> {
        locationSubject.onNext(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))
        return locationSubject.hide()
    }
}