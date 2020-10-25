@file:Suppress("MemberVisibilityCanBePrivate")

package com.falcon.turingx.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.falcon.turingx.core.utils.isPermissionsGranted
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Service to request device location using the view lifecycle.
 *
 * Start service when view is started, stop when view is stopped and destroy service when view is
 * destroyed.
 *
 * Initialize the location service when view is being created.
 *
 * */
class TXLocationService(
    private val context: Context,
    private val lifecycle: Lifecycle
) : LocationCallback(), LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    /** The main entry point for interacting with the fused location provider. */
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    /** Control var used to start service. */
    private var isServiceStarted = AtomicBoolean(false)

    /** Callbacks to locationChanged. */
    private var _onLocationChanged: MutableList<(location: Location) -> Unit> = mutableListOf()

    /**
     * Set the priority of the request. See [LocationRequest].
     *
     * The priority of the request is a strong hint to the LocationClient for which location sources
     * to use. For example, [LocationRequest.PRIORITY_HIGH_ACCURACY] is more likely to use GPS, and
     * [LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY] is more likely to use WIFI & Cell tower
     * positioning, but it also depends on many other factors (such as which sources are available)
     * and is implementation dependent.
     *
     * This parameter needs to be configured before the service starts.
     * */
    var priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY

    /**
     * Set the desired interval for active location updates, in milliseconds.
     *
     * The location client will actively try to obtain location updates for your application at
     * this interval, so it has a direct influence on the amount of power used by your application.
     * Choose your interval wisely.
     *
     * This parameter needs to be configured before the service starts.
     * */
    var interval: Long = 1000L

    /**
     * Explicitly set the fastest interval for location updates, in milliseconds.
     *
     * This controls the fastest rate at which your application will receive location updates, which
     * might be faster than [interval] in some situations (for example, if other applications
     * are triggering location updates).
     *
     * This allows your application to passively acquire locations at a rate faster than it actively
     * acquires locations, saving power.
     *
     * This parameter needs to be configured before the service starts.
     * */
    var fastestInterval: Long = 1000L

    /** The last known device location. */
    var lastLocation: Location? = null

    /** Return if permissions to access location is granted. */
    private fun isPermissionToAccessLocationGranted() = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).isPermissionsGranted(context)

    /** Add a callback where [event] is called when device location information is available. */
    fun addOnLocationChanged(event: (location: Location) -> Unit) = _onLocationChanged.add(event)

    /** Start location service when [lifecycle] is at least started. */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @SuppressLint("MissingPermission")
    fun startService() {
        // check if have permissions to start location service.
        if (!isPermissionToAccessLocationGranted()) return
        // check if view lifecycle is started to avoid memory leak.
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        //check if service is already started.
        if (isServiceStarted.get()) return
        // configure location request params
        val locationRequest = LocationRequest().apply {
            priority = this@TXLocationService.priority
            interval = this@TXLocationService.interval
            fastestInterval = this@TXLocationService.fastestInterval
        }
        // start location service.
        fusedLocationProviderClient ?: run {
            fusedLocationProviderClient = getFusedLocationProviderClient(context)
            fusedLocationProviderClient!!
        }.apply {
            requestLocationUpdates(locationRequest, this@TXLocationService, Looper.myLooper())
                .addOnFailureListener { Timber.e(it) }
        }.also { it.flushLocations() }
        // set service started
        isServiceStarted.set(true)
    }

    /** Stop location service when [lifecycle] is at least stopped. */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopService() {
        // clear and stop location listeners
        isServiceStarted.set(false)
        _onLocationChanged.clear()
        fusedLocationProviderClient?.removeLocationUpdates(this)
    }

    /**  Destroy location service when [lifecycle] is destroyed. */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroyService() {
        // clear, stop and destroy location service
        _onLocationChanged.clear()
        fusedLocationProviderClient?.removeLocationUpdates(this)
        fusedLocationProviderClient = null
    }

    /** Called when device location information is available. */
    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        // check if have last location
        if (locationResult != null && locationResult.lastLocation != null) {
            locationResult.lastLocation.also { location ->
                // with last locations dispatch to location changed listeners
                this@TXLocationService.lastLocation = location
                _onLocationChanged.forEach { it(location) }
            }
        }
    }

}