package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_setting.*

class activity_setting : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_setting)
        toolbarSetting.setTitle("")
        setSupportActionBar(toolbarSetting)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid= mAuth!!.uid!!
        btnYCKB()//btn yeu cau ket ban
        loadData()
        logout()
    }

    fun btnYCKB()
    {
        btnYeuCauKetBan.setOnClickListener {
            var intent = Intent(this@activity_setting, activity_list_friend_request::class.java)
            startActivity(intent)
        }
    }
    fun loadData() {
        mDatabase!!.child("request_friend").orderByChild("status").equalTo("0")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        var count=0
                        if (p0!!.getValue() != null) {
                            for(snap in p0!!.children)
                            {
                                var userFR = ""
                                var getRF: RequestFriendDC = snap!!.getValue(RequestFriendDC::class.java)!!

                                Log.d("CCC","1"+getRF.userid2)
                                Log.d("CCC","2"+getRF.userid1)
                                Log.d("CCC","3"+getRF.useraction)
                                Log.d("CCC","4"+p0!!.value.toString())
                                Log.d("CCC","5"+p0.key)
                                if ((userid == getRF.userid1 || userid == getRF.userid2) && userid != getRF.useraction) {
                                    if (userid == getRF.userid1) {
                                        count++
                                    } else {
                                        count++
                                    }
                                    Log.d("CCC","count "+count)
                                }
                            }
                            if(count==0)
                            {
                                numRequest.visibility= View.GONE
                            }
                            else {
                                numRequest.visibility= View.VISIBLE
                                numRequest.text = "(" + count + " yêu cầu)"
                                Log.d("CCC", "(" + count + " yêu cầu kết bạn)")
                            }
                        }
                        else
                        {
                            Log.d("CCC", "DATA NULL")
                            numRequest.visibility= View.GONE
                        }
                    }

                })

    }
    fun logout() {
        btn_linear_dangxuat.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this@activity_setting)

            // khởi tạo dialog
            alertDialogBuilder.setMessage("Bạn có muốn đăng xuất không?")
            // thiết lập nội dung cho dialog
            alertDialogBuilder.setPositiveButton("Có") { arg0, arg1 ->
                mDatabase!!.child("users").child(userid).child("online").setValue(0)
                AuthUI.getInstance().signOut(this@activity_setting).addOnCompleteListener {
                    // do something here
                    var intent = Intent(this@activity_setting, activity_login::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("logout", "logout")
                    startActivity(intent)
                }
                // button "Có" thoát khỏi ứng dụng
            }
            alertDialogBuilder.setNegativeButton("Không") { dialog, which ->
                dialog.dismiss()
                // button "no" ẩn dialog đi
            }
            val alertDialog = alertDialogBuilder.create()
            // tạo dialog
            alertDialog.show()
            // hiển thị dialog
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
    override fun onDestroy() {
        super.onDestroy()
        mDatabase!!.child("users").child(userid).child("online").setValue(0)
    }
}
