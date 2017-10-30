package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurImage
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.MainActivity
import com.ute.tinit.chatkotlin.R
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.activity_profile_more_editprofile.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class activity_profile_more_editprofile : PermissionsActivity() {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var userid = ""
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta.jpg?alt=media&token=d9bfc794-a5bd-47b7-966c-9bee18bfc75c"
    private var DATA_UPDATE: ByteArray? = null
    private var mStorageRef: StorageReference? = null
    var imgUploadLink: String = ""
    var imgUri: Uri? = null


    companion object {
        var FB_STORAGE_PATH: String = "avarta/"
        var REQUEST_CODE: Int = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_more_editprofile)
        toolbarxx.setTitle("")
        setSupportActionBar(toolbarxx)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance()
        userid= mAuth!!.uid!!
        spinner()
        tvdateselect()
        loadData()
        editName()
        updateAvarta()
        saveInfo()
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

    fun updateAvarta() {
        btn_avarta_updatex.setOnClickListener {
            requestPermissions(Manifest.permission.CAMERA, object : PermissionCallBack {
                @SuppressLint("MissingPermission")
                override fun permissionGranted() {
                    super.permissionGranted()
                    showFileChooser()
                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_profile_more_editprofile, "Vui lòng bật/chấp nhận quyền máy ảnh để thực hiện tính năng này", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    fun showFileChooser() {
        val dialog = Dialog(this@activity_profile_more_editprofile)
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
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 2)
                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_profile_more_editprofile, "Vui lòng bật/chấp nhận quyền bộ nhớ", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.dismiss()
        }
        dialog.show()
    }

    fun editName() {
        btn_edit_name_edit.setOnClickListener {
            tv_username_edit.visibility = View.GONE
            edit_username_edit.visibility = View.VISIBLE
            btn_edit_name_edit.visibility = View.GONE
            btn_edit_name_save_edit.visibility = View.VISIBLE
            edit_username_edit.setText(tv_username_edit.text)
        }

        btn_edit_name_save_edit.setOnClickListener {
            tv_username_edit.visibility = View.VISIBLE
            edit_username_edit.visibility = View.GONE
            btn_edit_name_edit.visibility = View.VISIBLE
            btn_edit_name_save_edit.visibility = View.GONE
            tv_username_edit.text = edit_username_edit.text.toString()
        }
    }

    fun saveInfo() {
        btn_save_info_edit.setOnClickListener {
            saveFirebase()
            finish()
        }
    }

    fun saveFirebase() {

        var userID: String = userid
        var email: String =tv_email_edit.text.toString()
        var phone_number = "" + et_sodienthoai.text.toString()
        var sex: String = select_gioitinh.getSelectedItem().toString()
        var tensave = tv_username_edit.text.toString()
        var ns=""+tv_ngaysinh.text.toString()
        var friend= listOf<String>("VqQahB7aoxMLvcXUNY0uke4yloz2") //lay xuong sau
        if (!imgUploadLink.equals("")) {
            IMAGE_URL = imgUploadLink
        }

        CreateUser(userID, tensave, sex, phone_number, email, "0", "0", 1,
                IMAGE_URL,ns,friend)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            Log.d("AAA", "REQUESE_CODE = " + activity_profile_more_myprofile.REQUEST_CODE)
            Log.d("AAA", "requestCode = " + requestCode)
            Log.d("AAA", "RESULT_OK = " + RESULT_OK)
            Log.d("AAA", "resultCode = " + resultCode)
            if (requestCode == 1 && resultCode == RESULT_OK) {

                try {
                    var bitmap: Bitmap = data.getExtras().get("data") as Bitmap;
                    image_editx.setImageBitmap(bitmap);
                    //
                    val baos = ByteArrayOutputStream()
                    //giam dung luong truoc khi day len firebase :(
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    DATA_UPDATE = baos.toByteArray()
                    Toast.makeText(this@activity_profile_more_editprofile, "Updating...", Toast.LENGTH_SHORT).show()
                    Upload()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else {
                if (requestCode == 2 && resultCode == RESULT_OK) {
                    try {
                        val selectedImage = data!!.getData()
                        image_editx.setImageURI(selectedImage)
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        image_editx.setImageBitmap(bitmap);
                        val baos = ByteArrayOutputStream()
                        //giam dung luong truoc khi day len firebase :(
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        DATA_UPDATE = baos.toByteArray()
                        imgUri = selectedImage
                        Toast.makeText(this@activity_profile_more_editprofile, "Updating...", Toast.LENGTH_SHORT).show()
                        Upload()
                    } catch (e: FileNotFoundException) {
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

    fun Upload() {
        if (DATA_UPDATE != null) {
            var dialog = ProgressDialog(this@activity_profile_more_editprofile)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            var ref: StorageReference = mStorageRef!!.child(activity_profile_firstlogin.FB_STORAGE_PATH + System.currentTimeMillis() + "." + "JPEG")

            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            imgUploadLink = p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_profile_more_editprofile,
                                    "Image Uploaded -> " + imgUploadLink, Toast.LENGTH_SHORT).show()
                            //set image test
                            IMAGE_URL = imgUploadLink
                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    image_edit_timeline.setImageBitmap(BlurImage.fastblur(bitmap, 40))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    image_edit_timeline.setImageResource(R.drawable.default_avarta)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            image_edit_timeline.setTag(target)
                            Picasso.with(this@activity_profile_more_editprofile)
                                    .load(IMAGE_URL)
                                    .error(R.drawable.default_avarta)
                                    .placeholder(R.drawable.default_avarta)
                                    .centerCrop()
                                    .resize(800, 800)
                                    .into(target)
                            //set image test
                            Log.d("BBB", imgUploadLink)
                        }
                    })
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@activity_profile_more_editprofile,
                                "Image Error", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long = (100 * taskSnapshot!!.bytesTransferred) / taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading " + progress.toInt() + "%")
                        }

                    })
        } else {
            Toast.makeText(this@activity_profile_more_editprofile,
                    "Select Image Error", Toast.LENGTH_SHORT).show()
        }
    }


    fun CreateUser(userId: String, name: String, sex: String, phone_number: String, email: String, latitude: String
                   , longitude: String, is_online: Int, avarta: String,ns:String,friend:List<String>) {
        var user = UserDC(userId, name, sex, phone_number, email, latitude, longitude, is_online, avarta,ns)
        Log.d("BBB",userId)
        Log.d("BBB",name)
        Log.d("BBB",sex)
        Log.d("BBB",phone_number)
        Log.d("BBB",email)
        Log.d("BBB",latitude)
        Log.d("BBB",longitude)
        Log.d("BBB",""+is_online)
        Log.d("BBB",avarta)
        Log.d("BBB",ns)

        mDatabase!!.child("users").child(userId).setValue(user, DatabaseReference.CompletionListener
        { databaseError, databaseReference ->
            if (databaseError == null) {
                Toast.makeText(this@activity_profile_more_editprofile,"Cập nhập thành công", Toast.LENGTH_SHORT).show()
                Log.d("BBB","Cập nhập thành công")
            }
            else
            {
                Toast.makeText(this@activity_profile_more_editprofile,"Lỗi rồi!!!!", Toast.LENGTH_SHORT).show()
                Log.d("BBB","lỗi rồi!!!!")
            }
        })
    }

    fun loadData() {
        var getuser: UserDC
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_profile_more_editprofile, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        getuser = p0!!.getValue(UserDC::class.java)!!
                        //  name= getuser.name!!
                        // avartaURL= getuser.avarta!!
                        tv_username_edit.text = getuser.name!!
                        IMAGE_URL=getuser.avatar!!
                        //select_gioitinh.text=getuser.sex
                        if(getuser.sex!!.equals("Nam"))
                        {
                            select_gioitinh.setSelection(1)
                        }
                        else
                        {
                            if(getuser.sex!!.equals("Nữ"))
                            {
                                select_gioitinh.setSelection(2)
                            }
                            else
                            {
                                select_gioitinh.setSelection(0)
                            }
                        }
                        tv_ngaysinh.text=getuser.date
                        et_sodienthoai.setText(getuser.phone_number)
                        tv_email_edit.text=getuser.email
                        Picasso.with(this@activity_profile_more_editprofile)
                                .load(getuser.avatar!!)
                                .error(R.drawable.default_avarta)
                                .into(image_editx)
                        val handler: Handler = Handler()
                        handler.postDelayed(Runnable {
                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    image_edit_timeline.setImageBitmap(BlurImage.fastblur(bitmap, 40))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    image_edit_timeline.setImageResource(R.drawable.default_avarta)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            image_edit_timeline.setTag(target)
                            Picasso.with(this@activity_profile_more_editprofile)
                                    .load(getuser.avatar!!)
                                    .error(R.drawable.default_avarta)
                                    .resize(800, 800)
                                    .placeholder(R.drawable.default_avarta)
                                    .into(target)
                        }, 100)
                    }
                })
    }

    fun tvdateselect() {

        tv_ngaysinh.setOnClickListener {
            showDialog(999)
        }
    }

    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
        // arg1 = year
        // arg2 = month
        // arg3 = day
        var temp = arg2 + 1
        tv_ngaysinh.setText("" + arg3 + "/" + temp + "/" + arg1)
    }

    override fun onCreateDialog(id: Int): Dialog? {
        // TODO Auto-generated method stub
        return if (id == 999) {
            DatePickerDialog(this, myDateListener, year, month, day)
        } else null
    }

    fun spinner() {
// Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.sex, android.R.layout.simple_spinner_item)
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
// Apply the adapter to the spinner
        select_gioitinh.adapter = adapter
        select_gioitinh.setSelection(0)
    }
}
