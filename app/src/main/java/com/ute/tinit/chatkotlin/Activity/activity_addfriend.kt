package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ChatAdapter
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ContactAdapter
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.FindFriendsAdapter
import com.ute.tinit.chatkotlin.DataClass.ContactDC
import com.ute.tinit.chatkotlin.DataClass.FindFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.activity_profile_more_editprofile.*
import kotlinx.android.synthetic.main.layout_activity_addfriend.*
import kotlinx.android.synthetic.main.layout_activity_profile_first_login.*
import kotlinx.android.synthetic.main.layout_activity_setting.*
import kotlinx.android.synthetic.main.layout_fragment_more.*
import kotlinx.android.synthetic.main.layout_fragment_more.view.*
import java.util.ArrayList
import com.google.firebase.database.DataSnapshot


class activity_addfriend : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: FindFriendsAdapter? = null
    var userid = ""
    val data = ArrayList<FindFriendDC>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_addfriend)
        toolbar_addfr.setTitle("")
        setSupportActionBar(toolbar_addfr)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        spinner()
        mRecyclerView = findViewById(R.id.rv_find_friend)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this@activity_addfriend)
        mAdapter = FindFriendsAdapter(this@activity_addfriend, data)
        mRecyclerView!!.adapter = mAdapter
        setData()
        btnsearch.setOnClickListener {
            loadData()
        }

    }

    fun setData() {
        var test = FindFriendDC("1", "Tin", "0", "http://hinhnendepnhat.net/wp-content/uploads/2017/07/nhung-hinh-anh-dep-danh-cho-facebook-ve-chu-meo-dang-yeu.jpg")
        var test1 = FindFriendDC("2", "Tin 1", "1", "http://hinhnendepnhat.net/wp-content/uploads/2017/07/nhung-hinh-anh-dep-danh-cho-facebook-ve-chu-meo-dang-yeu.jpg")
        var test2 = FindFriendDC("3", "Tin 2", "2", "http://hinhnendepnhat.net/wp-content/uploads/2017/07/nhung-hinh-anh-dep-danh-cho-facebook-ve-chu-meo-dang-yeu.jpg")
        var test3 = FindFriendDC("4", "Tin 3", "2", "http://hinhnendepnhat.net/wp-content/uploads/2017/07/nhung-hinh-anh-dep-danh-cho-facebook-ve-chu-meo-dang-yeu.jpg")
        data.add(test)
        data.add(test1)
        data.add(test2)
        data.add(test3)
        mAdapter!!.notifyDataSetChanged()
//        mDatabase!!.child("users").child(userid).child("friend")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onCancelled(p0: DatabaseError?) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onDataChange(p0: DataSnapshot?) {
//
//                        for (postSnapshot in p0!!.getChildren()) {
//                            Log.d("test", "Key: " + postSnapshot.key + " = " + postSnapshot.getValue().toString())
//                            var idFriend = postSnapshot.getValue().toString() //kiem tra
//                            mDatabase!!.child("users").child(idFriend)
//                                    .addValueEventListener(object : ValueEventListener {
//                                        override fun onDataChange(p0: DataSnapshot?) {
//                                            Log.d("AAA","Update tai day")
//                                            data.clear()
//                                            var getFriend: UserDC = p0!!.getValue(UserDC::class.java)!!
//                                            var tempOnline = getFriend.online
//                                            var isonline: Boolean = true
//                                            if (tempOnline == 1) {
//                                                isonline = true
//                                            } else {
//                                                isonline = false
//                                            }
//                                            val contact: ContactDC = ContactDC(getFriend.name!!, getFriend.avatar!!, isonline)
//                                            Log.d("AAA"," "+getFriend.name!!)
//                                            Log.d("AAA"," "+getFriend.avatar!!)
//                                            Log.d("AAA"," "+tempOnline)
//                                            data.add(contact)
//                                            mAdapter!!.notifyDataSetChanged()
//                                        }
//
//                                        override fun onCancelled(p0: DatabaseError?) {
//                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                                        }
//
//                                    })
//                        }
//                    }
//
//
//                })
    }

    fun spinner() {
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.select_search, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        id_select_search.adapter = adapter
        id_select_search.setSelection(0)
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

    fun loadData() {

        var getuser: UserDC
        mDatabase!!.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(this@activity_addfriend, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.d("BBB", "Vao dayddd")
                        if (id_select_search.getSelectedItem().toString().equals("Email")) {
                            Log.d("BBB", "Da chon email")
                            mDatabase!!.child("users").orderByChild("email").equalTo(ed_find_friend.text.toString())
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                            Toast.makeText(this@activity_addfriend, "AAA", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onDataChange(p0: DataSnapshot?) {
                                            if (p0!!.getValue() != null) {
                                                Log.d("BBB", "Data change email")
                                                for (childSnapshot in p0!!.getChildren()) {
                                                    var getUser: UserDC = p0!!.getValue(UserDC::class.java)!!
//                                                    Log.d("BBB", " " + getUser.name!!)
//                                                    Log.d("BBB", " " + getUser.avatar!!)
//                                                    Log.d("BBB", " " + getUser.email)
                                                    Log.d("BBB", "key = "+childSnapshot.key)
                                                }
                                            } else {
                                                Toast.makeText(this@activity_addfriend, "Không tìm thấy Email bạn vừa nhập"
                                                        , Toast.LENGTH_SHORT).show()
                                            }


                                        }


                                    })

                        }
                    }

                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)
    }
}
