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

class activity_find_friend_location : PermissionsActivity() {
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var locationA:Location?=null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
               //     Toast.makeText(this@activity_find_friend_location, "Call thanh cong", Toast.LENGTH_SHORT).show()
                    mFusedLocationClient!!.lastLocation
                            .addOnSuccessListener(this@activity_find_friend_location) { location ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Toast.makeText(this@activity_find_friend_location,"Vao Funsed...",Toast.LENGTH_SHORT).show()
                                    tvToaDoX_VT1.setText((location.latitude).toString())
                                    tvToaDoY_VT1.setText((location.longitude).toString())
                                    locationA=location
                                }
                            }
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