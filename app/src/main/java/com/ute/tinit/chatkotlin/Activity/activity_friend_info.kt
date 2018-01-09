package com.ute.tinit.chatkotlin.Activity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_friend_info.*

class activity_friend_info : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userFR=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_friend_info)
        toolbar1.setTitle("")
        setSupportActionBar(toolbar1)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        var intent = intent
        userFR = intent.getStringExtra("userfriend")
        Log.d("AAA","userfrid= "+userFR)
        loadData()
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

    override fun onBackPressed() {
        finish()
    }

    fun loadData() {
        var getuser: UserDC
        mDatabase!!.child("users").child(userFR)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_friend_info, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if(p0!!.value!=null)
                        {
                            getuser = p0!!.getValue(UserDC::class.java)!!
                            //  name= getuser.name!!
                            // avartaURL= getuser.avarta!!
                            Log.d("AAA",getuser.name!!)
                            Log.d("AAA",getuser.sex!!)
                            Log.d("AAA",getuser.date!!)
                            Log.d("AAA",getuser.phone_number!!)
                            Log.d("AAA",getuser.email!!)

                            tv_user_namex.text = getuser.name!!
                            tvGioiTinh.text = getuser.sex
                            tv_date.text = getuser.date
                            tv_sdt.text = getuser.phone_number
                            tv_email.text = getuser.email
                            Picasso.with(this@activity_friend_info)
                                    .load(getuser.avatar!!)
                                    .error(R.drawable.default_avarta)
                                    .placeholder(R.drawable.color_timeline)
                                    .into(image_myprofilex)


                            val target = object : Target {

                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    image_timelinex.setImageBitmap(BlurBuilder.blur(this@activity_friend_info,bitmap))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    image_timelinex.setImageResource(R.drawable.color_timeline)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                    image_timelinex.setImageResource(R.drawable.color_timeline)
                                }
                            }
                            image_timelinex.setTag(target)
                            Picasso.with(this@activity_friend_info)
                                    .load(getuser.avatar!!)
                                    .placeholder(R.drawable.color_timeline)
                                    .error(R.drawable.color_timeline)
                                    .centerCrop()
                                    .resize(800, 800)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .into(target)

                            mDatabase!!.child("users").child(userFR).removeEventListener(this)
                        }
                        else

                        {
                            Log.d("AAA","DATA NULL")
                        }
                        }

                })
    }
}
