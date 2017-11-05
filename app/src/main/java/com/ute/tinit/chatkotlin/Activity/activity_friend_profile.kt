package com.ute.tinit.chatkotlin.Activity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_friend_profile.*

class activity_friend_profile : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    var userFR = ""
    var status_rf = ""
    var keytemp = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_friend_profile)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        var intent = intent
        userFR = intent.getStringExtra("userfriend")
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        loadData()
        ketBan()
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

    fun ketBan() {
        var count_userid: Int = -1
        var count_userFR: Int = -1
        mDatabase!!.child("users").child(userid).child("friend")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            count_userid = p0.childrenCount.toInt()

                        }
                        else
                        {
                            count_userid =0
                        }
                    }
                })
        //tinh so friendhien tai de them <list> loi chua xu ly dc
        mDatabase!!.child("users").child(userFR).child("friend")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            count_userFR = p0.childrenCount.toInt()
                        }
                        else
                        {
                            count_userFR =0
                        }
                    }

                })
        btnKetBan.setOnClickListener {
            var setRF = RequestFriendDC(userid, userFR, userid, "0")
            mDatabase!!.child("request_friend").push().setValue(setRF)
            btnKetBan.visibility = View.GONE
            btnHuyKetBan.visibility = View.VISIBLE
            ln_dongy_tuchoi.visibility = View.GONE
        }
        btnDongYYC.setOnClickListener {
            var setRFDY1 = RequestFriendDC(userid, userFR, userid, "1") //dong y
            mDatabase!!.child("request_friend").child(keytemp).setValue(setRFDY1)
            //tinh so friendhien tai de them <list> loi chua xu ly dc
            // them 2 friend vao ds cua nhau
            mDatabase!!.child("users").child(userid).child("friend").child("" + count_userid).setValue(userFR)
            mDatabase!!.child("users").child(userFR).child("friend").child("" + count_userFR).setValue(userid)
            Toast.makeText(this@activity_friend_profile,"Kết bạn thành công",Toast.LENGTH_SHORT).show()

        }
        btnHuyKetBan.setOnClickListener {
            Log.d("statusx", "status btn huy kb" + status_rf)
            mDatabase!!.child("request_friend").child(keytemp).removeValue()
            btnHuyKetBan.visibility = View.GONE
            btnKetBan.visibility = View.VISIBLE
            ln_dongy_tuchoi.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        finish()
    }

    fun loadData() {
        var getuser: UserDC
        mDatabase!!.child("users").child(userFR)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_friend_profile, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            getuser = p0!!.getValue(UserDC::class.java)!!
                            Picasso.with(this@activity_friend_profile)
                                    .load(getuser.avatar!!)
                                    .error(R.drawable.default_avarta)
                                    .into(anh_dai_dien)
                            user_name_profile.text = getuser.name

                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    Image_profile_activity.setImageBitmap(BlurImage.fastblur(bitmap, 40))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    Image_profile_activity.setImageResource(R.drawable.default_avarta)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            Image_profile_activity.setTag(target)
                            Picasso.with(this@activity_friend_profile)
                                    .load(getuser.avatar)
                                    .error(R.drawable.default_avarta)
                                    .placeholder(R.drawable.default_avarta)
                                    .centerCrop()
                                    .resize(500, 500)
                                    .into(target)
                        } else {
                            ln_dongy_tuchoi.visibility = View.GONE
                            btnKetBan.visibility = View.VISIBLE
                            btnHuyKetBan.visibility = View.GONE
                        }
                    }
                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)
        //check user gui yeu cau ko phai la minh
        mDatabase!!.child("request_friend").orderByChild("useraction").equalTo(userFR)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            for (postSnapshot in p0!!.getChildren()) {
                                Log.d("keyrf", postSnapshot.key)
                                mDatabase!!.child("request_friend").child(postSnapshot.key)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.getValue() != null) {
                                                    var getRF: RequestFriendDC
                                                    getRF = p0!!.getValue(RequestFriendDC::class.java)!!
                                                    if ((getRF.userid1 == userid && getRF.userid2 == userFR) || (getRF.userid1 == userFR && getRF.userid2 == userid)) {
                                                        status_rf = getRF.status!!
                                                        if (status_rf.equals("0")) {
                                                            ln_dongy_tuchoi.visibility = View.VISIBLE
                                                            btnKetBan.visibility = View.GONE
                                                            btnHuyKetBan.visibility = View.GONE
                                                        }
                                                        if (status_rf.equals("1")) {
                                                            ln_dongy_tuchoi.visibility = View.GONE
                                                            btnKetBan.visibility = View.GONE
                                                            btnHuyKetBan.visibility = View.GONE
                                                        }
                                                        Log.d("statusx", "status trong rf " + status_rf)
                                                        keytemp = p0.key
                                                    }
                                                } else {
                                                    ln_dongy_tuchoi.visibility = View.GONE
                                                    btnKetBan.visibility = View.VISIBLE
                                                    btnHuyKetBan.visibility = View.GONE
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("statusx", "Du lieu null")
                        }
                    }
                })
        //check user gui yeu cau la minh
        mDatabase!!.child("request_friend").orderByChild("useraction").equalTo(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            for (postSnapshot in p0!!.getChildren()) {
                                Log.d("keyrf", postSnapshot.key)
                                mDatabase!!.child("request_friend").child(postSnapshot.key)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.getValue() != null) {
                                                    var getRF: RequestFriendDC
                                                    getRF = p0!!.getValue(RequestFriendDC::class.java)!!
                                                    if ((getRF.userid1 == userid && getRF.userid2 == userFR) || (getRF.userid1 == userFR && getRF.userid2 == userid)) {
                                                        status_rf = getRF.status!!
                                                        if (status_rf.equals("0")) {
                                                            btnKetBan.visibility = View.GONE
                                                            btnHuyKetBan.visibility = View.VISIBLE
                                                        }
                                                        if (status_rf.equals("1")) {
                                                            ln_dongy_tuchoi.visibility = View.GONE
                                                            btnKetBan.visibility = View.GONE
                                                            btnHuyKetBan.visibility = View.GONE
                                                        }
                                                        Log.d("statusx", "status trong rf " + status_rf)
                                                        keytemp = p0.key
                                                    }
                                                } else {
                                                    ln_dongy_tuchoi.visibility = View.GONE
                                                    btnKetBan.visibility = View.VISIBLE
                                                    btnHuyKetBan.visibility = View.GONE
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("statusx", "Du lieu null")
                        }
                    }
                })
    }
}
