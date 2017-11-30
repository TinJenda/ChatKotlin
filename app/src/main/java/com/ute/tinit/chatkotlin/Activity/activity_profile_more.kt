package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile_more.*

class activity_profile_more : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_more)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        loadData()
        btnThongTin()
        listBlock()
        listAddFriendRequest()
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
                                numRequestMore.visibility= View.GONE
                            }
                            else {
                                numRequestMore.visibility= View.VISIBLE
                                numRequestMore.text = "(" + count + " yêu cầu)"
                                Log.d("CCC", "(" + count + " yêu cầu kết bạn)")
                            }
                        }
                        else
                        {
                            Log.d("CCC", "DATA NULL")
                            numRequestMore.visibility= View.GONE
                        }
                    }

                })

        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if(p0!!.value!=null)
                        {
                            var tempRF: UserDC = p0!!.getValue(UserDC::class.java)!!
                            userName.text=tempRF.name
                            mDatabase!!.child("users").child(userid).removeEventListener(this)
                        }
                    }

                })

    }

    fun listBlock()
    {
        btn_linear_danhsachden.setOnClickListener {
            var intent = Intent(this@activity_profile_more, activity_list_block::class.java)
            startActivity(intent)
        }
    }
    fun listAddFriendRequest() {
        btn_linear_yeucauketban.setOnClickListener {
            var intent = Intent(this@activity_profile_more, activity_list_friend_request::class.java)
            startActivity(intent)
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

    override fun onBackPressed() {
        finish()
    }

    fun btnThongTin() {
        btn_liner_thongtin.setOnClickListener {
            var intent = Intent(this@activity_profile_more, activity_profile_more_myprofile::class.java)
            startActivity(intent)
        }

    }

}
