package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.RequestFriendAdapter
import com.ute.tinit.chatkotlin.DataClass.Contact2DC
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_list_friend_request.*

class activity_list_friend_request : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    private var mAdapter: RequestFriendAdapter? = null
    var data = arrayListOf<Contact2DC>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_list_friend_request)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!

        Log.d("TTT", " " + userid)
        rv_listFriendRequest!!.setHasFixedSize(true)
        rv_listFriendRequest!!.layoutManager = LinearLayoutManager(this)
        setData()
        mAdapter = RequestFriendAdapter(this, data)
        rv_listFriendRequest!!.adapter = mAdapter
        loadDATA()
    }

    override fun onBackPressed() {
        finish()
    }

    fun loadDATA()
    {
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if(p0!!.value!=null)
                        {
                            var tempRF: UserDC = p0!!.getValue(UserDC::class.java)!!
                            tv_user_name.text=tempRF.name
                            mDatabase!!.child("users").child(userid).removeEventListener(this)
                        }
                    }

                })
    }
    fun setData() {
        mDatabase!!.child("request_friend").orderByChild("status").equalTo("0")
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        Log.d("TTT", "DATACHANGE " + p1 + " data =" + p0!!.value.toString())
                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        Log.d("TTT", "DATACHANGE " + p1 + " data =" + p0!!.value.toString())
                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0!!.getValue() != null) {
                            var userFR = ""
                            var getRF: RequestFriendDC = p0!!.getValue(RequestFriendDC::class.java)!!
                            if ((userid == getRF.userid1 || userid == getRF.userid2) && userid != getRF.useraction) {
                                if (userid == getRF.userid1) {
                                    userFR = getRF.userid2!!
                                } else {
                                    userFR = getRF.userid1!!
                                }
                                mDatabase!!.child("users").child(userFR).addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot?) {
                                        var getUser: UserDC = p0!!.getValue(UserDC::class.java)!!
                                        var contact: Contact2DC = Contact2DC(userFR, getUser.name!!, getUser.avatar!!)
                                        // kiem tra contact da co trong list
                                        if ((rv_listFriendRequest!!.adapter as RequestFriendAdapter).isContactAdded(contact))
                                            (rv_listFriendRequest!!.adapter as RequestFriendAdapter).notifyItemDataChange(contact)
                                        else
                                            (rv_listFriendRequest!!.adapter as RequestFriendAdapter).addItem(contact)
                                    }

                                })

                            }
                            // kiem tra contact da co trong list
//                                if ((rv_listFriendRequest!!.adapter as RequestFriendAdapter).isContactAdded(contact))
//                                    (rv_listFriendRequest!!.adapter as RequestFriendAdapter).notifyItemDataChange(contact)
//                                else
//                                    (rv_listFriendRequest!!.adapter as RequestFriendAdapter).addItem(contact)

                        } else {
                            Log.d("TTT", "DATA NULL")
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        (rv_listFriendRequest!!.adapter as RequestFriendAdapter).removeItem(p0!!.key)
                        Log.d("TTT", "DATA REMOVE" + p0!!.key)
                        mDatabase!!.child("request_friend").child(p0.key)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError?) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot?) {
                                        if (p0!!.value != null) {
                                            var userFR = ""
                                            var tempRF: RequestFriendDC = p0!!.getValue(RequestFriendDC::class.java)!!
                                            if (userid == tempRF.userid1) {
                                                userFR = tempRF.userid2!!
                                            } else {
                                                userFR = tempRF.userid1!!
                                            }
                                            (rv_listFriendRequest!!.adapter as RequestFriendAdapter).removeItem(userFR)
                                            (rv_listFriendRequest!!.adapter as RequestFriendAdapter).notifyDataSetChanged()
                                            mDatabase!!.child("request_friend").child(p0.key).removeEventListener(this)
                                        }
                                    }

                                })
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }

                })
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
}
