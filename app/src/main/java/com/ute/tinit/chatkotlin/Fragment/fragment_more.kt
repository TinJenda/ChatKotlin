package com.ute.tinit.chatkotlin.Fragment

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ute.tinit.chatkotlin.R
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_find_friend_location
import com.ute.tinit.chatkotlin.Activity.activity_profile
import com.ute.tinit.chatkotlin.DataClass.UserDC
import kotlinx.android.synthetic.main.layout_fragment_more.view.*
import com.google.firebase.auth.FirebaseAuth
import com.ute.tinit.chatkotlin.Activity.activity_list_friend_request
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC

class fragment_more : Fragment() {
    private val PERMISSIONS_LOCATION=arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_more, container, false)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid= mAuth!!.uid!!

        Log.d("AAA", "userid " + userid)
        loadData(view)
        view.btn_linear_Profile.setOnClickListener {
            Toast.makeText(activity, "Open profile", Toast.LENGTH_SHORT).show()
            var intent = Intent(activity, activity_profile::class.java)
            startActivity(intent)
        }
        btnTimQuanhDay(view)
        btnYCKB(view)
        return view
    }
    fun btnYCKB(view:View)
    {
        view.btnYeuCauKetBan.setOnClickListener {
            var intent = Intent(context, activity_list_friend_request::class.java)
            startActivity(intent)
        }
    }
    fun btnTimQuanhDay(view: View) {
      view.ln_timquanhday.setOnClickListener {
          if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                  && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
          {
              var intentTQD = Intent(activity, activity_find_friend_location::class.java)
              startActivity(intentTQD)
          }
          else
          {
              Toast.makeText(activity,"Vui lòng bật quyền vị trí!!!",Toast.LENGTH_SHORT).show()
              ActivityCompat.requestPermissions( activity, PERMISSIONS_LOCATION,1)
          }
      }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "Bạn đã không chấp nhận quyền vị trí :( ", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    fun loadData(view: View) {
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        if(p0!!.getValue()!=null)
                        {
                            var getuser:UserDC = p0!!.getValue(UserDC::class.java)!!
                            //  name= getuser.name!!
                            // avartaURL= getuser.avarta!!
                            view.tv_username.text=""+getuser.name!!
                            Picasso.with(context)
                                    .load(getuser.avatar!!)
                                    .error(R.drawable.default_avarta)
                                    .into(view.image_avarta)
                        }
                    }
                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)

        mDatabase!!.child("request_friend").orderByChild("status").equalTo("0")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        var count=0
                        if (p0!!.getValue() != null) {
                            for(snap in p0!!.children)
                            {
                                var userFR = ""
                                var getRF: RequestFriendDC = snap!!.getValue(RequestFriendDC::class.java)!!

                                Log.d("CCC","1"+getRF.userid2)
                                Log.d("CCC","2"+getRF.userid1)
                                Log.d("CCC","3"+getRF.useraction)
                                Log.d("CCC","4"+p0!!.value.toString())
                                Log.d("CCC","5"+p0.key)
                                if ((userid == getRF.userid1 || userid == getRF.userid2) && userid != getRF.useraction) {
                                    if (userid == getRF.userid1) {
                                        count++
                                    } else {
                                        count++
                                    }
                                    Log.d("CCC","count "+count)
                                }
                            }
                            if(count==0)
                            {
                                view.numRequest.visibility= View.GONE
                            }
                            else {
                                view.numRequest.visibility= View.VISIBLE
                                view.numRequest.text = "(" + count + " yêu cầu)"
                                Log.d("CCC", "(" + count + " yêu cầu kết bạn)")
                            }
                        }
                        else
                        {
                            Log.d("CCC", "DATA NULL")
                            view.numRequest.visibility= View.GONE
                        }
                    }

                })
    }
    override fun onDestroy() {
        super.onDestroy()
       // mDatabase!!.child("users").child(userid).child("online").setValue(0)
    }
}

