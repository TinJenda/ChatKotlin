package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import com.ute.tinit.chatkotlin.R
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.layout_activity_profile_first_login.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class activity_profile_firstlogin : PermissionsActivity() {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    private var mDatabase: DatabaseReference? = null
    private var IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/avarta.jpg?alt=media&token=d9bfc794-a5bd-47b7-966c-9bee18bfc75c"
    private var DATA_UPDATE: ByteArray? = null
    private var mStorageRef: StorageReference? = null
    var imgUploadLink: String = ""
    var imgUri: Uri? = null
    private var useID=""
    companion object {
        var FB_STORAGE_PATH: String = "avarta/"
        var REQUEST_CODE: Int = 234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_first_login)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mStorageRef = FirebaseStorage.getInstance().getReference();
        spinner()
        tvdateselect()
        loadData()
        editName()
        updateAvarta()
        saveInfo()
    }

    fun updateAvarta() {
        btn_avarta_update.setOnClickListener {
            requestPermissions(Manifest.permission.CAMERA, object : PermissionCallBack {
                @SuppressLint("MissingPermission")
                override fun permissionGranted() {
                    super.permissionGranted()
                    showFileChooser()
                }

                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Call permissions", "Denied")
                    Toast.makeText(this@activity_profile_firstlogin, "Vui lòng bật/chấp nhận quyền máy ảnh để thực hiện tính năng này", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    fun showFileChooser() {
        val dialog = Dialog(this@activity_profile_firstlogin)
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
                    Toast.makeText(this@activity_profile_firstlogin, "Vui lòng bật/chấp nhận quyền bộ nhớ", Toast.LENGTH_SHORT).show()
                }
            })
            dialog.dismiss()
        }
        dialog.show()
    }

    fun editName() {
        btn_edit_name.setOnClickListener {
            user_name_firstlogin.visibility = View.GONE
            edit_text_name.visibility = View.VISIBLE
            btn_edit_name.visibility = View.GONE
            btn_edit_name_save.visibility = View.VISIBLE
        }

        btn_edit_name_save.setOnClickListener {
            user_name_firstlogin.visibility = View.VISIBLE
            edit_text_name.visibility = View.GONE
            btn_edit_name.visibility = View.VISIBLE
            btn_edit_name_save.visibility = View.GONE
            user_name_firstlogin.text = edit_text_name.text.toString()
        }
    }

    fun saveInfo() {
        btn_luuthongtin.setOnClickListener {
            saveFirebase()
        }
    }

    fun saveFirebase() {
        var intent = intent
        var userID: String = intent.getStringExtra("userid")
        var email: String = intent.getStringExtra("email")
        var phone_number = "" + et_phone.text.toString()
        var sex: String = sex_spinner.getSelectedItem().toString()
        var tensave = edit_text_name.text.toString()
        var ns=""+tv_date_select.text.toString()
        var friend= listOf<String>("E0A8P9YEmUXS9NCUK6EqaZR1kk32")
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
                    image_avarta_firstlogin.setImageBitmap(bitmap);
                    //
                    val baos = ByteArrayOutputStream()
                    //giam dung luong truoc khi day len firebase :(
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    DATA_UPDATE = baos.toByteArray()
                    Toast.makeText(this@activity_profile_firstlogin, "Updating...", Toast.LENGTH_SHORT).show()
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
                        image_avarta_firstlogin.setImageURI(selectedImage)
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        image_avarta_firstlogin.setImageBitmap(bitmap);
                        val baos = ByteArrayOutputStream()
                        //giam dung luong truoc khi day len firebase :(
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        DATA_UPDATE = baos.toByteArray()
                        imgUri = selectedImage
                        Toast.makeText(this@activity_profile_firstlogin, "Updating...", Toast.LENGTH_SHORT).show()
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
            var dialog = ProgressDialog(this@activity_profile_firstlogin)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            var ref: StorageReference = mStorageRef!!.child(activity_profile_firstlogin.FB_STORAGE_PATH + System.currentTimeMillis() + "." + "JPEG")

            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            imgUploadLink = p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_profile_firstlogin,
                                    "Image Uploaded -> " + imgUploadLink, Toast.LENGTH_SHORT).show()
                            //set image test
                            IMAGE_URL = imgUploadLink
                            val target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                                    image_timeline.setImageBitmap(BlurImage.fastblur(bitmap, 40))
                                }

                                override fun onBitmapFailed(errorDrawable: Drawable) {
                                    image_timeline.setImageResource(R.drawable.default_avarta)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                                }
                            }
                            image_timeline.setTag(target)
                            Picasso.with(this@activity_profile_firstlogin)
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
                        Toast.makeText(this@activity_profile_firstlogin,
                                "Image Error", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long = (100 * taskSnapshot!!.bytesTransferred) / taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading " + progress.toInt() + "%")
                        }

                    })
        } else {
            Toast.makeText(this@activity_profile_firstlogin,
                    "Select Image Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
    }

    fun CreateUser(userId: String, name: String, sex: String, phone_number: String, email: String, latitude: String
                   , longitude: String, is_online: Int, avatar: String,ns:String,friend:List<String>) {
        var user = UserDC(userId, name, sex, phone_number, email, latitude, longitude, is_online, avatar,ns,friend)
        mDatabase!!.child("users").child(userId).setValue(user, DatabaseReference.CompletionListener
        { databaseError, databaseReference ->
            if (databaseError == null) {
                Toast.makeText(this@activity_profile_firstlogin,"Cập nhập thành công",Toast.LENGTH_SHORT).show()
                var intent=Intent(this@activity_profile_firstlogin,MainActivity::class.java)
                intent.putExtra("userid",useID)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this@activity_profile_firstlogin,"Lỗi rồi!!!!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun loadData() {
        var intent = intent
        var ten: String = intent.getStringExtra("username")
        var email: String = intent.getStringExtra("email")
        useID=intent.getStringExtra("userid")
        user_name_firstlogin.text = ten
        edit_text_name.setText(ten)
        tv_email.text = email
        blurImage()
    }

    fun blurImage() {
        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                image_timeline.setImageBitmap(BlurImage.fastblur(bitmap, 40))
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                image_timeline.setImageResource(R.drawable.default_avarta)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
            }
        }
        image_timeline.setTag(target)
        Picasso.with(this)
                .load(IMAGE_URL)
                .error(R.drawable.default_avarta)
                .resize(800, 800)
                .placeholder(R.drawable.default_avarta)
                .into(target)
    }

    fun tvdateselect() {

        tv_date_select.setOnClickListener {
            showDialog(999)
        }
    }

    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
        // arg1 = year
        // arg2 = month
        // arg3 = day
        var temp = arg2 + 1
        tv_date_select.setText("" + arg3 + "/" + temp + "/" + arg1)
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
        sex_spinner.adapter = adapter
        sex_spinner.setSelection(0)
    }
}
