package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.storage.FirebaseStorage
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile.*
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mvc.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.theartofdev.edmodo.cropper.CropImage
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.layout_activity_find_friend_location.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class activity_profile_more_myprofile : PermissionsActivity() {

    private val BLUR_PRECENTAGE = 50
    private val IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta%2Favarta.jpg?alt=media&token=84d76d2f-6d6c-4929-b8db-63e8c00d35f7"
    private var DATA_UPDATE:ByteArray?=null
    private var mCropImageUri:Uri?=null
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
                toolbarImage1.setImageResource(R.mipmap.ic_launcher)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
            }
        }
        toolbarImage1.setTag(target)
        Picasso.with(this)
                .load(IMAGE_URL)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(target)
    }


    fun doiThongTin() {
        btn_doithongtin.setOnClickListener {
            btnUpload()
        }
    }

    //test upload image to storage firebase and get link that image
    fun btnDoiAnhDaiDien() {

        myprofile_anh_dai_dien.setOnClickListener {
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
            Log.d("AAA","REQUESE_CODE = "+ REQUEST_CODE)
            Log.d("AAA","requestCode = "+ requestCode)
            Log.d("AAA","RESULT_OK = "+ RESULT_OK)
            Log.d("AAA","resultCode = "+resultCode)
            if(requestCode== REQUEST_CODE&&resultCode== RESULT_OK&&data.data!=null)
            {
                imgUri=data.data
                try {
//                    var bm:Bitmap=MediaStore.Images.Media.getBitmap(contentResolver,imgUri)
//                    myprofile_anh_dai_dien.setImageBitmap(bm)

                     var bitmap:Bitmap  = ImagePicker.getImageFromResult(this@activity_profile_more_myprofile,
                             requestCode, resultCode, data)!!
                    val baos = ByteArrayOutputStream()
                    //giam dung luong truoc khi day len firebase :(
                    bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos)
                    DATA_UPDATE = baos.toByteArray()
                    Toast.makeText(this@activity_profile_more_myprofile,"Vao update", Toast.LENGTH_SHORT).show()
                    myprofile_anh_dai_dien.setImageBitmap(bitmap)
                }
                catch (e:FileNotFoundException)
                {
                    e.printStackTrace()
                }
                catch (e:IOException)
                {
                    e.printStackTrace()
                }
                  // handle result of pick image chooser

//                  if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//                    var imageUri:Uri  = CropImage.getPickImageResultUri(this@activity_profile_more_myprofile, data);
//
//                    // For API >= 23 we need to check specifically that we have permissions to read external storage.
//                    if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
//                      // request permissions and handle the result in onRequestPermissionsResult()
//                      mCropImageUri = imageUri
//                        var listPermission= arrayOf("Manifest.permission.READ_EXTERNAL_STORAGE")
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            requestPermissions(listPermission,   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
//                        };
//                    } else {
//                      // no permissions required or already grunted, can start crop image activity
//                      startCropImageActivity(imageUri);
//                    }
//                  }
            }
        }
        else{
            Log.d("AAA","DATA NULL")
        }
    }
    //

    fun  showFileChooser() {
        onPickImage()
}
    fun getImageExt(uri:Uri): String? {
        var contentResolver:ContentResolver=contentResolver
        var mimeTypeMap:MimeTypeMap= MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
    fun btnUpload()
    {

        if(imgUri!=null)
        {
            var dialog = ProgressDialog(this@activity_profile_more_myprofile)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            //get storage ref
            var ref:StorageReference= mStorageRef!!.child(FB_STORAGE_PATH+System.currentTimeMillis()+"."+getImageExt(imgUri!!))

            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object :OnSuccessListener<UploadTask.TaskSnapshot>{
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            var imgUploadLink:String= p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_profile_more_myprofile,
                                    "Image Uploaded -> "+imgUploadLink ,Toast.LENGTH_SHORT).show()
                            Log.d("BBB",imgUploadLink)
                        }
                    })
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@activity_profile_more_myprofile,
                                "Image Error",Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long =(100* taskSnapshot!!.bytesTransferred)/taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading "+progress.toInt()+"%")
                        }

                    })
        }
        else
        {
            Toast.makeText(this@activity_profile_more_myprofile,
                    "Select Image Error",Toast.LENGTH_SHORT).show()
        }
    }

      fun onPickImage(  ) {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    }

    private fun startCropImageActivity() {
        CropImage.activity()
    .start(this@activity_profile_more_myprofile);
    }

    fun onSelectImageClick() {
        CropImage.startPickImageActivity(this@activity_profile_more_myprofile)
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .start(this@activity_profile_more_myprofile)
    }
}
