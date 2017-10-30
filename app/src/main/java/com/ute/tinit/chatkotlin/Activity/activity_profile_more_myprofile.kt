package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.storage.FirebaseStorage
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile.*
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.MainActivity
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.layout_activity_profile.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class activity_profile_more_myprofile : PermissionsActivity() {

    private val BLUR_PRECENTAGE = 40
    private var IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta.jpg?alt=media&token=d9bfc794-a5bd-47b7-966c-9bee18bfc75c"
    private var DATA_UPDATE: ByteArray? = null
    private var mStorageRef: StorageReference? = null
    var imgUri: Uri? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    companion object {
        var FB_STORAGE_PATH: String = "avarta/"
        var REQUEST_CODE: Int = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_more_myprofile)
        toolbar1.setTitle("")
        setSupportActionBar(toolbar1)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!

       // blurImage()
        mStorageRef = FirebaseStorage.getInstance().getReference();
        loadData()
        doiThongTin()
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


    fun doiThongTin() {
        btn_doithongtin.setOnClickListener {
            var intent=Intent(this@activity_profile_more_myprofile,activity_profile_more_editprofile::class.java)
            startActivity(intent)
        }
    }

    fun loadData() {
        var getuser: UserDC
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_profile_more_myprofile, "AAA", Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        getuser = p0!!.getValue(UserDC::class.java)!!
                        //  name= getuser.name!!
                        // avartaURL= getuser.avarta!!
                        tv_user_namex.text = getuser.name!!
                        tvGioiTinh.text=getuser.sex
                        tv_date.text=getuser.date
                        tv_sdt.text=getuser.phone_number
                        tv_email.text=getuser.email
                        Picasso.with(this@activity_profile_more_myprofile)
                                .load(getuser.avatar!!)
                                .error(R.drawable.default_avarta)
                                .into(image_myprofilex)
                        val handler: Handler = Handler()
                        handler.postDelayed(Runnable {
                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    image_timelinex.setImageBitmap(BlurImage.fastblur(bitmap, 40))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    image_timelinex.setImageResource(R.drawable.default_avarta)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            image_timelinex.setTag(target)
                            Picasso.with(this@activity_profile_more_myprofile)
                                    .load(getuser.avatar!!)
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
