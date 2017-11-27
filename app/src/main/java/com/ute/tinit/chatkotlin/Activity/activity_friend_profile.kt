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
import android.view.Gravity
import android.widget.LinearLayout
import android.app.Dialog
import android.os.Handler
import kotlinx.android.synthetic.main.bottom_sheet.view.*


class activity_friend_profile : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    var userFR = ""
    var status_rf_block = ""
    var status_rf = ""
    var keytemp = ""
    var key_conver_block = ""
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
        btnMore()
    }

    private fun eventClick() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val mBottomSheetDialog = Dialog(this@activity_friend_profile, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()
        view.btnUnBlock.visibility = View.GONE
        view.btnUnfriend.visibility = View.GONE
        //check first
        //check is friend
        mDatabase!!.child("friends").child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.value != null) {
                    for (snap in p0.children) {
                        if (snap.value.toString() != userFR) {
                            view.btnUnfriend.visibility = View.GONE
                        } else {
                            view.btnUnfriend.visibility = View.VISIBLE
                            break
                        }
                    }
                }
            }
        })
        //check blocked
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
                                                        status_rf_block = getRF.status!!
                                                        if (status_rf_block.equals("3")) {
                                                            view.btnBlock.visibility = View.GONE
                                                            view.btnUnBlock.visibility = View.VISIBLE
                                                            view.btnUnfriend.visibility = View.GONE
                                                            view.btnChatt.visibility = View.GONE
                                                            view.btnInfomation.visibility = View.GONE
                                                        }
                                                        if (status_rf_block.equals("1")) {
                                                            view.btnUnfriend.visibility = View.VISIBLE
                                                            view.btnBlock.visibility = View.VISIBLE
                                                            view.btnUnBlock.visibility = View.GONE
                                                            view.btnChatt.visibility = View.VISIBLE
                                                            view.btnInfomation.visibility = View.VISIBLE
                                                        }
                                                        if (status_rf_block.equals("0")) {
                                                            view.btnUnfriend.visibility = View.GONE
                                                            view.btnBlock.visibility = View.VISIBLE
                                                            view.btnUnBlock.visibility = View.GONE
                                                            view.btnChatt.visibility = View.VISIBLE
                                                            view.btnInfomation.visibility = View.VISIBLE
                                                        }
                                                        Log.d("statusx", "status trong rf " + status_rf_block)
                                                        key_conver_block = p0.key
                                                    }
                                                } else {
                                                    view.btnBlock.visibility = View.VISIBLE
                                                    view.btnUnBlock.visibility = View.GONE
                                                    view.btnUnfriend.visibility=View.GONE
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("statusx", "Du lieu null")
                        }
                    }
                })
        view.btnChatt.setOnClickListener {
            Toast.makeText(this@activity_friend_profile, "Clicked btnChatt", Toast.LENGTH_SHORT).show()
            mBottomSheetDialog.dismiss()
        }
        view.btnUnfriend.setOnClickListener {
            Toast.makeText(this@activity_friend_profile, "Clicked btnUnfriend", Toast.LENGTH_SHORT).show()
            //xoa ban trong userid
            mDatabase!!.child("friends").child(userid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                for (snap in p0.children) {
                                    if (snap.getValue() == userFR) {
                                        mDatabase!!.child("friends").child(userid).child(snap.key).removeValue()
                                    }
                                }
                            } else {
                                Log.d("AAA", "Friend null")
                            }
                            mDatabase!!.child("friends").child(userid).removeEventListener(this)
                        }

                    })
            //xoa ban trong userFR
            mDatabase!!.child("friends").child(userFR)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                for (snap in p0.children) {
                                    if (snap.getValue() == userid) {
                                        mDatabase!!.child("friends").child(userFR).child(snap.key).removeValue()
                                    }
                                }
                            } else {
                                Log.d("AAA", "Friend null")
                            }
                            mDatabase!!.child("friends").child(userFR).removeEventListener(this)
                        }

                    })
            //xoa request la ban
            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid)
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
                                                        if (getRF.userid2 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusx", "Du lieu user1 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid).removeEventListener(this)
                        }
                    })
            //xoa request neu la user 2
            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid)
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
                                                        if (getRF.userid1 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusxxx", "Du lieu user2 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid).removeEventListener(this)
                        }
                    })
            mBottomSheetDialog.dismiss()

        }
        view.btnBlock.setOnClickListener {
            view.btnBlock.visibility = View.GONE
            view.btnUnBlock.visibility = View.VISIBLE
            Toast.makeText(this@activity_friend_profile, "Clicked btnBlock", Toast.LENGTH_SHORT).show()
            //xoa ban trong userid
            mDatabase!!.child("friends").child(userid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                for (snap in p0.children) {
                                    if (snap.getValue() == userFR) {
                                        mDatabase!!.child("friends").child(userid).child(snap.key).removeValue()
                                    }
                                }
                            } else {
                                Log.d("AAA", "Friend null")
                            }
                            mDatabase!!.child("friends").child(userid).removeEventListener(this)
                        }

                    })
            //xoa ban trong userFR
            mDatabase!!.child("friends").child(userFR)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                for (snap in p0.children) {
                                    if (snap.getValue() == userid) {
                                        mDatabase!!.child("friends").child(userFR).child(snap.key).removeValue()
                                    }
                                }
                            } else {
                                Log.d("AAA", "Friend null")
                            }
                            mDatabase!!.child("friends").child(userFR).removeEventListener(this)
                        }

                    })
            //xoa request la ban
            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid)
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
                                                        if (getRF.userid2 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusx", "Du lieu user1 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid).removeEventListener(this)
                        }
                    })
            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid)
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
                                                        if (getRF.userid1 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusxxx", "Du lieu user2 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid).removeEventListener(this)
                        }
                    })

            val handler: Handler = Handler()
            handler.postDelayed(Runnable {
                var setRF = RequestFriendDC(userid, userFR, userid, "3")
                mDatabase!!.child("request_friend").push().setValue(setRF)
            }, 1000)
            mBottomSheetDialog.dismiss()
        }

        view.btnUnBlock.setOnClickListener {
            mDatabase!!.child("request_friend").child(key_conver_block).removeValue()
            view.btnUnBlock.visibility = View.GONE
            mBottomSheetDialog.dismiss()
        }
        view.btnInfomation.setOnClickListener {
            Toast.makeText(this@activity_friend_profile, "Clicked btnInfomation", Toast.LENGTH_SHORT).show()
            mBottomSheetDialog.dismiss()
        }
    }

    fun btnMore() {
        btnFloatProfile.setOnClickListener {
            eventClick()
        }
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

        btnKetBan.setOnClickListener {
            var setRF = RequestFriendDC(userid, userFR, userid, "0")
            mDatabase!!.child("request_friend").push().setValue(setRF)
            btnKetBan.visibility = View.GONE
            btnHuyYCKetBan.visibility = View.VISIBLE
            ln_dongy_tuchoi.visibility = View.GONE
        }
        btnDongYYC.setOnClickListener {
            var setRFDY1 = RequestFriendDC(userid, userFR, userid, "1") //dong y
            request_friends.visibility = View.GONE
            mDatabase!!.child("request_friend").child(keytemp).setValue(setRFDY1)
            //tinh so friendhien tai de them <list> loi chua xu ly dc
            // them 2 friend vao ds cua nhau
            mDatabase!!.child("friends").child(userid).push().setValue(userFR)
            mDatabase!!.child("friends").child(userFR).push().setValue(userid)
            Toast.makeText(this@activity_friend_profile, "Kết bạn thành công", Toast.LENGTH_SHORT).show()

        }
        btnTuChoiYC.setOnClickListener {
            mDatabase!!.child("request_friend").child(keytemp).removeValue()
            btnHuyYCKetBan.visibility = View.GONE
            btnKetBan.visibility = View.VISIBLE
            ln_dongy_tuchoi.visibility = View.GONE
        }
        btnHuyYCKetBan.setOnClickListener {
            Log.d("statusx", "status btn huy kb" + status_rf)
            mDatabase!!.child("request_friend").child(keytemp).removeValue()
            btnHuyYCKetBan.visibility = View.GONE
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
                            btnHuyYCKetBan.visibility = View.GONE
                        }
                    }
                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)

        // check user gui yeu cau ko phai la minh
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
                                                            btnHuyYCKetBan.visibility = View.GONE
                                                            request_friends.visibility = View.VISIBLE
                                                            wall_friends.visibility = View.VISIBLE
                                                            btnFloatProfile.visibility = View.VISIBLE
                                                        }
                                                        else
                                                        if (status_rf.equals("1")) {
                                                            ln_dongy_tuchoi.visibility = View.GONE
                                                            btnKetBan.visibility = View.GONE
                                                            btnHuyYCKetBan.visibility = View.GONE
                                                            request_friends.visibility = View.GONE
                                                            wall_friends.visibility = View.VISIBLE
                                                            btnFloatProfile.visibility = View.VISIBLE
                                                        }
                                                        else
                                                        if (status_rf.equals("3")) {
                                                            wall_friends.visibility = View.GONE
                                                            btnFloatProfile.visibility = View.GONE
                                                        }
                                                        else
                                                        {
                                                            btnFloatProfile.visibility = View.VISIBLE
                                                            wall_friends.visibility = View.VISIBLE
                                                            ln_dongy_tuchoi.visibility = View.GONE
                                                            btnKetBan.visibility = View.VISIBLE
                                                            btnHuyYCKetBan.visibility = View.GONE
                                                            request_friends.visibility=View.VISIBLE
                                                        }
                                                        Log.d("statusx", "status trong rf " + status_rf)
                                                        keytemp = p0.key
                                                    }
                                                } else {
                                                    request_friends.visibility = View.VISIBLE
                                                    ln_dongy_tuchoi.visibility = View.GONE
                                                    btnKetBan.visibility = View.VISIBLE
                                                    btnHuyYCKetBan.visibility = View.GONE
                                                    wall_friends.visibility = View.VISIBLE
                                                    btnFloatProfile.visibility = View.VISIBLE
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("statusx", "Du lieu null")
                            btnFloatProfile.visibility = View.VISIBLE
                            wall_friends.visibility = View.VISIBLE
                            ln_dongy_tuchoi.visibility = View.GONE
                            btnKetBan.visibility = View.VISIBLE
                            btnHuyYCKetBan.visibility = View.GONE
                            request_friends.visibility=View.VISIBLE
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
                                                            btnHuyYCKetBan.visibility = View.VISIBLE
                                                            request_friends.visibility = View.VISIBLE
                                                            wall_friends.visibility = View.VISIBLE
                                                        } else
                                                            if (status_rf.equals("1")) {
                                                                ln_dongy_tuchoi.visibility = View.GONE
                                                                btnKetBan.visibility = View.GONE
                                                                request_friends.visibility = View.GONE
                                                                btnHuyYCKetBan.visibility = View.GONE
                                                                wall_friends.visibility = View.VISIBLE
                                                            } else
                                                                if (status_rf.equals("3")) {
                                                                    Log.d("VVV","Vao dayy")
                                                                    request_friends.visibility = View.GONE
                                                                    ln_dongy_tuchoi.visibility = View.GONE
                                                                    btnKetBan.visibility = View.GONE
                                                                    btnHuyYCKetBan.visibility = View.GONE
                                                                    wall_friends.visibility = View.GONE
                                                                } else {
                                                                    Log.d("VVV","Vao day")
                                                                    wall_friends.visibility = View.VISIBLE
                                                                    ln_dongy_tuchoi.visibility = View.GONE
                                                                    btnKetBan.visibility = View.VISIBLE
                                                                    btnHuyYCKetBan.visibility = View.GONE
                                                                    request_friends.visibility = View.VISIBLE
                                                                }
                                                        Log.d("statusx", "status trong rf " + status_rf)
                                                        keytemp = p0.key
                                                    }
                                                }
                                            }
                                        })
                            }
                        } else {
                            wall_friends.visibility = View.VISIBLE
                            ln_dongy_tuchoi.visibility = View.GONE
                            btnKetBan.visibility = View.VISIBLE
                            btnHuyYCKetBan.visibility = View.GONE
                            request_friends.visibility = View.VISIBLE
                            Log.d("statusx", "Du lieu null")
                        }
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        // mDatabase!!.child("users").child(userid).child("online").setValue(0)
    }
}
