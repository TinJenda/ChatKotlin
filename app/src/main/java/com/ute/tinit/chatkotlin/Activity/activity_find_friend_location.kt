package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_find_friend_location.*
import kotlinx.android.synthetic.main.toolbar_more.*
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.ute.tinit.chatkotlin.R.string.disconnect
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationListener
import com.ute.tinit.chatkotlin.R.string.disconnect
import android.widget.TextView
import java.text.DateFormat


class activity_find_friend_location : PermissionsActivity(), LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var locationA= Location("")
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mCurrentLocation: Location? = null
    var mLastUpdateTime: String? = null
    private val INTERVAL = (1000 * 10).toLong()
    private val FASTEST_INTERVAL = (1000 * 5).toLong()

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }
    public override fun onStart() {
        super.onStart()
        Log.d("BBB", "onStart fired ..............")
        if(this@activity_find_friend_location.mGoogleApiClient != null){
            mGoogleApiClient!!.connect()
        }

    }

    public override fun onStop() {
        super.onStop()
        Log.d("BBB", "onStop fired ..............")
        mGoogleApiClient!!.disconnect()
        Log.d("BBB", "isConnected ...............: " + mGoogleApiClient!!.isConnected())
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        if (ConnectionResult.SUCCESS == status) {
            return true
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show()
            return false
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d("BBB", "onConnected - isConnected ...............: " + mGoogleApiClient!!.isConnected())
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    protected fun startLocationUpdates() {
        val pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this@activity_find_friend_location)
        Log.d("BBB", "Location update started ..............: ")
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("BBB", "Connection failed: " + connectionResult.toString())
    }

    override fun onLocationChanged(location: Location) {
        Log.d("BBB", "Firing onLocationChanged..............................................")
        mCurrentLocation = location
        mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
        updateUI()
    }

    private fun updateUI() {
        Log.d("BBB", "UI update initiated .............")
        if (null != mCurrentLocation) {
            val lat = (mCurrentLocation!!.getLatitude()).toString()
            val lng = (mCurrentLocation!!.getLongitude()).toString()
            tvToaDoX_VT1.setText(lat)
            tvToaDoY_VT1.setText(lng)
            locationA.latitude=mCurrentLocation!!.getLatitude()
            locationA.longitude=mCurrentLocation!!.getLongitude()
            Log.d("BBB","At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation!!.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation!!.getProvider())
        } else {
            Log.d("BBB", "location is null ...............")
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    protected fun stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this)
        Log.d("BBB", "Location update stopped .......................")
    }

    public override fun onResume() {
        super.onResume()
        if (mGoogleApiClient!!.isConnected()) {
            startLocationUpdates()
            Log.d("BBB", "Location update resumed .....................")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BBB", "onCreate ...............................")
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish()
        }
        createLocationRequest()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        setContentView(R.layout.layout_activity_find_friend_location)
        toolbar_more.title = ""
        setSupportActionBar(toolbar_more)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@activity_find_friend_location);
        btnGetViTri.setOnClickListener {

            requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, object : PermissionCallBack {
                @SuppressLint("MissingPermission")
                override fun permissionGranted() {
                    super.permissionGranted()
                    updateUI()
                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_find_friend_location, "Vui lòng bật chấp nhận quyền để thực hiện tính năng này", Toast.LENGTH_SHORT).show()

                }
            })
        }

        btnTinhKhoangCach.setOnClickListener {
//            locationB!!.latitude= 10.8730123
//            locationB!!.longitude=106.7981348
            var locationC=Location("")
            locationC!!.longitude=106.7959461
            locationC.latitude=10.8730176
            var formatNumber=DecimalFormat()
            formatNumber.minimumFractionDigits=0
            formatNumber.maximumFractionDigits=0
            // txtKetQua.setText(locationA!!.distanceTo(locationB).toString())
            Toast.makeText(this@activity_find_friend_location,"Khoảng cách đường chim bay",Toast.LENGTH_SHORT).show()
           txtKetQua.text=(formatNumber.format(locationA!!.distanceTo(locationC))).toString() +" meter"
            //txtKetQua.text=distance_between(locationC,locationA!!).toString()
        }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        when (id) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}