package com.ute.tinit.chatkotlin.Activity

import android.os.Bundle
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_find_friend_location.*
import kotlinx.android.synthetic.main.toolbar_more.*
import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.android.gms.location.LocationServices
import io.vrinda.kotlinpermissions.PermissionsActivity
import java.util.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Adapter.FindFriendLocationAdapter
import com.ute.tinit.chatkotlin.DataClass.FindFriendLocationDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class activity_find_friend_location : PermissionsActivity(), LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var locationA = Location("")
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mCurrentLocation: Location? = null
    var mLastUpdateTime: String? = null
    private val INTERVAL = (1000 * 10).toLong()
    private val FASTEST_INTERVAL = (1000 * 5).toLong()
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    private var mAdapter: FindFriendLocationAdapter? = null
    var data = arrayListOf<FindFriendLocationDC>()

    protected fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    public override fun onStart() {
        super.onStart()
        Log.d("BBB", "onStart fired ..............")
        if (this@activity_find_friend_location.mGoogleApiClient != null) {
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

    fun updateLocation(longitude: String, latitude: String) {
        mDatabase!!.child("users").child(userid).child("latitude").setValue(latitude)
        mDatabase!!.child("users").child(userid).child("longitude").setValue(longitude)
    }

    private fun updateUI() {
        Log.d("BBB", "UI update initiated .............")
        loading()
        if (null != mCurrentLocation) {
            val lat = (mCurrentLocation!!.getLatitude()).toString()
            val lng = (mCurrentLocation!!.getLongitude()).toString()
            locationA.latitude = mCurrentLocation!!.getLatitude()
            locationA.longitude = mCurrentLocation!!.getLongitude()
            updateLocation(mCurrentLocation!!.getLongitude().toString(), mCurrentLocation!!.getLatitude().toString())
            Log.d("BBB", "At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation!!.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation!!.getProvider())
        } else {
            Log.d("BBB", "location is null ...............")
        }

    }

    fun loading() {
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            var tempUser: UserDC = p0!!.getValue(UserDC::class.java)!!
                            if (tempUser.longitude == "0" && tempUser.latitude == "0") {
                                ln_timquanhday.visibility = View.GONE
                                ln_load.visibility = View.VISIBLE
                            } else {
                                ln_timquanhday.visibility = View.VISIBLE
                                ln_load.visibility = View.GONE
                            }
                        }
                    }

                })
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
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this@activity_find_friend_location)

        Log.d("TTT", " " + userid)
        rv_listFindFriend!!.setHasFixedSize(true)
        rv_listFindFriend!!.layoutManager = LinearLayoutManager(this)
        setData()
        mAdapter = FindFriendLocationAdapter(this, data)
        rv_listFindFriend!!.adapter = mAdapter
        loading()
        loadDATA()

//        btnGetViTri.setOnClickListener {
//            requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, object : PermissionCallBack {
//                @SuppressLint("MissingPermission")
//                override fun permissionGranted() {
//                    super.permissionGranted()
//                    updateUI()
//                }
//
//                override fun permissionDenied() {
//                    super.permissionDenied()
//                    Log.v("Call permissions", "Denied")
//                    Toast.makeText(this@activity_find_friend_location, "Vui lòng bật chấp nhận quyền để thực hiện tính năng này", Toast.LENGTH_SHORT).show()
//
//                }
//            })
//        }
//
//        btnTinhKhoangCach.setOnClickListener {
////            locationB!!.latitude= 10.8730123
////            locationB!!.longitude=106.7981348
//            var locationC=Location("")
//            locationC!!.longitude=106.7959461
//            locationC.latitude=10.8730176
//            var formatNumber=DecimalFormat()
//            formatNumber.minimumFractionDigits=0
//            formatNumber.maximumFractionDigits=0
//            // txtKetQua.setText(locationA!!.distanceTo(locationB).toString())
//            Toast.makeText(this@activity_find_friend_location,"Khoảng cách đường chim bay",Toast.LENGTH_SHORT).show()
//           txtKetQua.text=(formatNumber.format(locationA!!.distanceTo(locationC))).toString() +" meter"
//            //txtKetQua.text=distance_between(locationC,locationA!!).toString()
//        }

    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this@activity_find_friend_location)

        // khởi tạo dialog
        alertDialogBuilder.setMessage("Bạn có muốn xóa vị trí và tránh làm phiền...")
        // thiết lập nội dung cho dialog
        alertDialogBuilder.setPositiveButton("Đồng ý") { arg0, arg1 ->
            mDatabase!!.child("users").child(userid).child("latitude").setValue("0")
            mDatabase!!.child("users").child(userid).child("longitude").setValue("0")
            finish()
            // button "Có" thoát khỏi ứng dụng
        }
        alertDialogBuilder.setNegativeButton("Không") { dialog, which ->
            dialog.dismiss()
            finish()
            // button "no" ẩn dialog đi
        }
        val alertDialog = alertDialogBuilder.create()
        // tạo dialog
        alertDialog.show()
        // hiển thị dialog
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

    fun loadDATA() {
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            var tempRF: UserDC = p0!!.getValue(UserDC::class.java)!!
                            tv_user_name.text = tempRF.name
                            mDatabase!!.child("users").child(userid).removeEventListener(this)
                        }
                    }

                })
    }

    fun getAge(dob: Calendar): Int {
        val today = Calendar.getInstance()
        val curYear = today.get(Calendar.YEAR)
        val dobYear = dob.get(Calendar.YEAR)
        var age = curYear - dobYear
        // if dob is month or day is behind today's month or day
        // reduce age by 1
        val curMonth = today.get(Calendar.MONTH)
        val dobMonth = dob.get(Calendar.MONTH)
        if (dobMonth > curMonth) { // this year can't be counted!
            age--
        } else if (dobMonth == curMonth) { // same month? check for day
            val curDay = today.get(Calendar.DAY_OF_MONTH)
            val dobDay = dob.get(Calendar.DAY_OF_MONTH)
            if (dobDay > curDay) { // this year can't be counted!
                age--
            }
        }
        return age
    }

    fun setData() {
        mDatabase!!.child("users")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0!!.value != null) {
                            var tempuser: UserDC = p0.getValue(UserDC::class.java)!!
                            if ((userid != tempuser.userID) && (!tempuser.latitude.equals("0")) && (!tempuser.longitude.equals("0"))) {
                                var age = ""
                                if (tempuser.date != "") {
                                    val sdf = SimpleDateFormat("dd/MM/yyyy")
                                    var dob = Calendar.getInstance()
                                    dob.time = sdf.parse(tempuser.date)
                                    age = "" + getAge(dob)
                                } else {
                                    age = "0"
                                }
                                var locationFriend = Location("")
                                locationFriend!!.longitude = tempuser.longitude!!.toDouble()
                                locationFriend.latitude = tempuser.latitude!!.toDouble()
                                var formatNumber = DecimalFormat()
                                formatNumber.minimumFractionDigits = 0
                                formatNumber.maximumFractionDigits = 0
                                var distance = (formatNumber.format(locationA!!.distanceTo(locationFriend))) + "m"
                                Log.d("AAA", " =" + (formatNumber.format(locationA!!.distanceTo(locationFriend))))
                                var items: FindFriendLocationDC = FindFriendLocationDC(tempuser.userID, tempuser.name, tempuser.avatar, age, tempuser.sex, distance)

                                if ((rv_listFindFriend!!.adapter as FindFriendLocationAdapter).isContactAdded(items))
                                    (rv_listFindFriend!!.adapter as FindFriendLocationAdapter).notifyItemDataChange(items)
                                else
                                    (rv_listFindFriend!!.adapter as FindFriendLocationAdapter).addItem(items)
                            }

                        } else {
                            Log.d("AAA", "DATA NULL")
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        (rv_listFindFriend!!.adapter as FindFriendLocationAdapter).removeItem(p0!!.key)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }


                })
    }
}