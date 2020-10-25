package com.falcon.turingx.sample.location

import android.Manifest
import android.os.Bundle
import com.falcon.turingx.core.ui.activity.TXActivity
import com.falcon.turingx.location.TXLocationService
import com.falcon.turingx.sample.R
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : TXActivity() {

    lateinit var locationService: TXLocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationService = TXLocationService(this, lifecycle)

        buttonStart.setOnClickListener { locationService.startService() }
        buttonStop.setOnClickListener { locationService.stopService() }

        locationService.addOnLocationChanged {
            locationText.text = "${it.latitude} x ${it.longitude}"
        }

        checkPermission(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            101
        ) { isPermissionGranted ->
            if (isPermissionGranted) locationService.startService()
        }

    }
}