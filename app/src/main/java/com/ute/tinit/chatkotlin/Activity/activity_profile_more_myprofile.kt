package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.provider.MediaStore
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class activity_profile_more_myprofile : PermissionsActivity() {

    private val BLUR_PRECENTAGE = 40
    private var IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta%2F1508246074211.JPEG?alt=media&token=24d2a77e-471f-482a-b375-c621eafd5809"
    private var DATA_UPDATE: ByteArray? = null
    private var mStorageRef: StorageReference? = null
    var imgUri: Uri? = null

    companion object {
        var FB_STORAGE_PATH: String = "avarta/"
        var REQUEST_CODE: Int = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_more_myprofile)
        toolbar1.setTitle("")
        setSupportActionBar(toolbar1)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        blurImage()
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnDoiAnhDaiDien()
        logoAnimation()
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

    fun blurImage() {

        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                toolbarImage1.setImageBitmap(BlurImage.fastblur(bitmap, BLUR_PRECENTAGE))
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                toolbarImage1.setImageResource(R.drawable.null_image)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
            }
        }
        toolbarImage1.setTag(target)
        Picasso.with(this)
                .load(IMAGE_URL)
                .error(R.drawable.null_image)
                .resize(800, 800)
                .placeholder(R.drawable.null_image)
                .into(target)
    }


    fun doiThongTin() {
        btn_doithongtin.setOnClickListener {
            btnUploadCamera()
        }
    }

    //test upload image to storage firebase and get link that image
    fun btnDoiAnhDaiDien() {

        cap_nhap_anh_dd.setOnClickListener {
            requestPermissions(Manifest.permission.CAMERA, object : PermissionCallBack {
                @SuppressLint("MissingPermission")
                override fun permissionGranted() {
                    super.permissionGranted()
                    showFileChooser()
                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_profile_more_myprofile, "Vui lòng bật/chấp nhận quyền máy ảnh để thực hiện tính năng này", Toast.LENGTH_SHORT).show()

                }
            })

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            Log.d("AAA", "REQUESE_CODE = " + REQUEST_CODE)
            Log.d("AAA", "requestCode = " + requestCode)
            Log.d("AAA", "RESULT_OK = " + RESULT_OK)
            Log.d("AAA", "resultCode = " + resultCode)
            if (requestCode == 1 && resultCode == RESULT_OK) {

                try {
                    var bitmap: Bitmap = data.getExtras().get("data") as Bitmap;
                    myprofile_anh_dai_dien.setImageBitmap(bitmap);
                    //
                    val baos = ByteArrayOutputStream()
                    //giam dung luong truoc khi day len firebase :(
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    DATA_UPDATE=baos.toByteArray()
                    Toast.makeText(this@activity_profile_more_myprofile, "Vao update", Toast.LENGTH_SHORT).show()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                if (requestCode == 2 && resultCode == RESULT_OK) {
                    try {
                        val selectedImage = data!!.getData()
                        myprofile_anh_dai_dien.setImageURI(selectedImage)
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        myprofile_anh_dai_dien.setImageBitmap(bitmap);
                        val baos = ByteArrayOutputStream()
                        //giam dung luong truoc khi day len firebase :(
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        DATA_UPDATE = baos.toByteArray()
                        imgUri=selectedImage
                        Toast.makeText(this@activity_profile_more_myprofile, "Vao update", Toast.LENGTH_SHORT).show()
                    }
                    catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        } else {
            Log.d("AAA", "DATA NULL")

        }
    }
    //

    fun showFileChooser() {
        val dialog = Dialog(this@activity_profile_more_myprofile)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_list_select_image)
        // Set dialog title
        dialog.setTitle("")

        // set values for custom dialog components - text, image and button
        val btnCamera = dialog.findViewById<Button>(R.id.btnCamera)
        val btnThuVien = dialog.findViewById<Button>(R.id.btnThuVien)

        btnCamera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 1)
            dialog.dismiss()
        }

        btnThuVien.setOnClickListener {
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, object : PermissionCallBack {
                @SuppressLint("MissingPermission")
                override fun permissionGranted() {
                    super.permissionGranted()

                    val pickPhoto = Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 2)

                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_profile_more_myprofile, "Vui lòng bật/chấp nhận quyền bộ nhớ để thực hiện tính năng này", Toast.LENGTH_SHORT).show()

                }
            })
            dialog.dismiss()
        }
        dialog.show()
    }

    fun btnUploadGallery() {

        if (DATA_UPDATE != null) {
            var dialog = ProgressDialog(this@activity_profile_more_myprofile)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            //get storage ref
            var ref: StorageReference = mStorageRef!!.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." +"png")
            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            var imgUploadLink: String = p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_profile_more_myprofile,
                                    "Image Uploaded -> " + imgUploadLink, Toast.LENGTH_SHORT).show()
                            Log.d("BBB", imgUploadLink)
                        }
                    })
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@activity_profile_more_myprofile,
                                "Image Error", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long = (100 * taskSnapshot!!.bytesTransferred) / taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading " + progress.toInt() + "%")
                        }

                    })
        } else {
            Toast.makeText(this@activity_profile_more_myprofile,
                    "Select Image Error", Toast.LENGTH_SHORT).show()
        }
    }
    fun btnUploadCamera() {

        if (DATA_UPDATE != null) {
            var dialog = ProgressDialog(this@activity_profile_more_myprofile)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            var ref: StorageReference = mStorageRef!!.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + "JPEG")

            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            var imgUploadLink: String = p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_profile_more_myprofile,
                                    "Image Uploaded -> " + imgUploadLink, Toast.LENGTH_SHORT).show()
                            //set image test
                            IMAGE_URL=imgUploadLink
                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    toolbarImage1.setImageBitmap(BlurImage.fastblur(bitmap, BLUR_PRECENTAGE))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    toolbarImage1.setImageResource(R.drawable.null_image)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            toolbarImage1.setTag(target)
                            Picasso.with(this@activity_profile_more_myprofile)
                                    .load(IMAGE_URL)
                                    .error(R.drawable.null_image)
                                    .placeholder(R.drawable.null_image)
                                    .centerCrop()
                                    .resize(800, 800)
                                    .into(target)
                            //set image test
                            Log.d("BBB", imgUploadLink)
                        }
                    })
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@activity_profile_more_myprofile,
                                "Image Error", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long = (100 * taskSnapshot!!.bytesTransferred) / taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading " + progress.toInt() + "%")
                        }

                    })
        } else {
            Toast.makeText(this@activity_profile_more_myprofile,
                    "Select Image Error", Toast.LENGTH_SHORT).show()
        }
    }
    fun logoAnimation() {
        var animation: Animation = AnimationUtils.loadAnimation(this@activity_profile_more_myprofile, R.anim.ani_camera)
        cap_nhap_anh_dd.startAnimation(animation)
    }
}
