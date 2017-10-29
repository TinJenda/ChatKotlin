package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ute.tinit.chatkotlin.R
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import com.ute.tinit.chatkotlin.DataClass.UserDC
import kotlinx.android.synthetic.main.layout_activity_profile.*

class activity_profile : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        nhatky_hinhanh()
        floatClick()
        loadData()
    }

    fun nhatky_hinhanh() {

        btnAnh.setOnClickListener {
            //            btnAnh.setBackgroundResource(R.drawable.click_btn_contact)
//            btnNhatKy.setBackgroundResource(R.drawable.tab_bg_unselected)
            txtNhatKy.setTextColor(getResources().getColorStateList(R.color.colorDefaultText));
            txtAnh.setTextColor(getResources().getColorStateList(R.color.mainColor))
            liner_nhatky.visibility = View.GONE
            liner_hinhanh.visibility = View.VISIBLE
        }
        btnNhatKy.setOnClickListener {
            txtNhatKy.setTextColor(getResources().getColorStateList(R.color.mainColor));
            txtAnh.setTextColor(getResources().getColorStateList(R.color.colorDefaultText))
            liner_nhatky.visibility = View.VISIBLE
            liner_hinhanh.visibility = View.GONE
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

    fun floatClick() {
        btnFloatProfile.setBackgroundTintList(getResources().getColorStateList(R.color.mainColor));
        btnFloatProfile.setOnClickListener {
            var intent = Intent(this@activity_profile, activity_profile_more::class.java)
            startActivity(intent)
        }
    }

    fun loadData() {
        var getuser: UserDC
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_profile, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        getuser = p0!!.getValue(UserDC::class.java)!!
                        //  name= getuser.name!!
                        // avartaURL= getuser.avarta!!
                        user_name_profile.text = getuser.name!!
                        Picasso.with(this@activity_profile)
                                .load(getuser.avarta!!)
                                .error(R.drawable.default_avarta)
                                .into(anh_dai_dien)
                        val handler: Handler = Handler()
                        handler.postDelayed(Runnable {
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
                            Picasso.with(this@activity_profile)
                                    .load(getuser.avarta!!)
                                    .error(R.drawable.default_avarta)
                                    .resize(800, 800)
                                    .placeholder(R.drawable.default_avarta)
                                    .into(target)
                        }, 100)
                    }
                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)

    }

}
