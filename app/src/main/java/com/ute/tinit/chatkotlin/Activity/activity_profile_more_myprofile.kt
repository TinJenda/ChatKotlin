package com.ute.tinit.chatkotlin.Activity

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.storage.FirebaseStorage
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile.*
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL

class activity_profile_more_myprofile : AppCompatActivity() {
    private val BLUR_PRECENTAGE = 50
    private val IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta%2Favarta.jpg?alt=media&token=84d76d2f-6d6c-4929-b8db-63e8c00d35f7"

    private var mStorageRef: StorageReference? = null
    var imgUri: Uri? = null

    companion object {
        var FB_STORAGE_PATH: String = "avarta/"
        var FB_DATABASE_PATH: String = "image"
        var REQUEST_CODE: Int = 1234
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
       // val resultBmp = BlurBuilder.blur(this@activity_profile_more_myprofile, BitmapFactory.decodeResource(resources, R.drawable.avarta))
       // val resultBmp = BlurBuilder.blur(this@activity_profile_more_myprofile, BitmapFactory.decodeStream())
      //  LoadImage().execute("https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta%2F1507790338040.jpg?alt=media&token=d4ddd06c-33e0-40f1-b7b5-4e3acd67ddbc")
      //  toolbarImage1.setImageBitmap(resultBmp)
        //Configure target for
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
            showFileChooser()
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
                    var bm:Bitmap=MediaStore.Images.Media.getBitmap(contentResolver,imgUri)
                    myprofile_anh_dai_dien.setImageBitmap(bm)
                }
                catch (e:FileNotFoundException)
                {
                    e.printStackTrace()
                }
                catch (e:IOException)
                {
                    e.printStackTrace()
                }
            }
        }
        else{
            Log.d("AAA","DATA NULL")
        }
    }
    //
    fun  showFileChooser() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, REQUEST_CODE)
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
            ref.putFile(imgUri!!)
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

    //giam dung luong file
    private fun decodeFile(f: File): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            // The new size we want to scale to
            val REQUIRED_SIZE = 70

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2
            }

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

}
