package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.FindFriendsAdapter
import com.ute.tinit.chatkotlin.DataClass.FindFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_find_friend.*
import java.util.ArrayList
import com.google.firebase.database.DataSnapshot

class activity_find_friend : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: FindFriendsAdapter? = null
    var userid = ""
    var getInfoUser: UserDC? = null
    var data = ArrayList<FindFriendDC>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_find_friend)
        toolbar_addfr.setTitle("")
        setSupportActionBar(toolbar_addfr)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        spinner()
        mRecyclerView = findViewById(R.id.rv_find_friend)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this@activity_find_friend)
        mAdapter = FindFriendsAdapter(this@activity_find_friend, data)
        mRecyclerView!!.adapter = mAdapter
        //  setData()
        btnsearch.isEnabled = false
        loadData()


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

    fun getUser(idUser: String): UserDC {
        var temp = UserDC()
        mDatabase!!.child("users").child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                temp = p0!!.getValue(UserDC::class.java)!!
            }

        })
        return temp!!
    }

    fun getUserByEmail(email: String): UserDC {
        mDatabase!!.child("users").orderByChild("email")
                .equalTo(email)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        getInfoUser = p0!!.getValue(UserDC::class.java)!!
                    }

                })
        return getInfoUser!!
    }

    fun loadData() {

        ed_find_friend.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed_find_friend.text.toString().equals("") || (ed_find_friend.text.toString().equals(null))) {
                    btnsearch.isEnabled = false
                } else {
                    btnsearch.isEnabled = true
                    btnsearch.setOnClickListener {
                        if (id_select_search.getSelectedItem().toString().equals("Email")) {
                            Log.d("BBB", "Da chon email")
                            mDatabase!!.child("users").orderByChild("email").equalTo(ed_find_friend.text.toString())
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                            Toast.makeText(this@activity_find_friend, "AAA", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onDataChange(p0: DataSnapshot?) {
                                            if (p0!!.getValue() != null) {
                                                var keyChild = ""
                                                Log.d("BBB", "Data change email")
                                                for (childSnapshot in p0!!.getChildren()) {
//                                                    Log.d("BBB", " " + getUser.name!!)
//                                                    Log.d("BBB", " " + getUser.avatar!!)
//                                                    Log.d("BBB", " " + getUser.email)
                                                    Log.d("BBB", "key = " + childSnapshot.key)
                                                    keyChild = childSnapshot.key
                                                    // vao friendreuest vao user so sanh tim co tom tai user ko
                                                    mDatabase!!.child("users").child(keyChild).addValueEventListener(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError?) {
                                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                            data.clear()
                                                            //tim và lấy usẻ theo gmail
                                                            var temp = p0!!.getValue(UserDC::class.java)!!
                                                            Log.d("BBB", " " + temp.name!!)
                                                            Log.d("BBB", " " + temp.avatar!!)
                                                            Log.d("BBB", " " + temp.email)
                                                            data.clear()
                                                            var dataTemp = FindFriendDC(temp.userID, temp.name, temp.avatar)
                                                            data.add(dataTemp)
                                                            (mRecyclerView!!.adapter as FindFriendsAdapter).notifyDataSetChanged()
                                                            tv_noti_findfriends.visibility = View.GONE
                                                        }
                                                    })
                                                }
                                            } else {
                                                data.clear()
                                                (mRecyclerView!!.adapter as FindFriendsAdapter).notifyDataSetChanged()
                                                tv_noti_findfriends.visibility = View.VISIBLE
                                            }
                                        }
                                    })

                        } else {
                            //search theo ten
                            data.clear()
                            mDatabase!!.child("users").orderByChild("name")
                                    .startAt(ed_find_friend.text.toString())
                                    .endAt(ed_find_friend.text.toString() + "\uf8ff")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                            Toast.makeText(this@activity_find_friend, "AAA", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onDataChange(p0: DataSnapshot?) {
                                            if (p0!!.getValue() != null) {
                                                var keyChild = ""
                                                Log.d("BBB", "Data change ten")
                                                for (childSnapshot in p0!!.getChildren()) {
//                                                    Log.d("BBB", " " + getUser.name!!)
//                                                    Log.d("BBB", " " + getUser.avatar!!)
//                                                    Log.d("BBB", " " + getUser.email)
                                                    Log.d("BBB", "key = " + childSnapshot.key)
                                                    keyChild = childSnapshot.key
                                                    // vao friendreuest vao user so sanh tim co tom tai user ko
                                                    mDatabase!!.child("users").child(keyChild).addValueEventListener(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError?) {
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                           if(p0!!.getValue()!=null)
                                                           {
                                                               //tim và lấy usẻ theo gmail
                                                               var temp = p0!!.getValue(UserDC::class.java)!!
                                                               Log.d("BBB", " " + temp.name!!)
                                                               Log.d("BBB", " " + temp.avatar!!)
                                                               Log.d("BBB", " " + temp.email)
                                                               var dataTemp = FindFriendDC(temp.userID, temp.name, temp.avatar)

//                                                               if ((mRecyclerView!!.adapter as FindFriendsAdapter).isAdded(dataTemp))
//                                                                   (mRecyclerView!!.adapter as FindFriendsAdapter).notifyChange(dataTemp)
//                                                               else
//                                                                   (mRecyclerView!!.adapter as FindFriendsAdapter).addItem(dataTemp)
                                                               data.add(dataTemp)
                                                               (mRecyclerView!!.adapter as FindFriendsAdapter).notifyDataSetChanged()
                                                               mDatabase!!.child("users").child(keyChild).removeEventListener(this)
                                                               mDatabase!!.child("users").orderByChild("name")
                                                                       .startAt(ed_find_friend.text.toString())
                                                                       .endAt(ed_find_friend.text.toString() + "\uf8ff").removeEventListener(this)
                                                               tv_noti_findfriends.visibility = View.GONE
                                                           }
                                                        }
                                                    })
                                                }
                                            } else {
                                                (mRecyclerView!!.adapter as FindFriendsAdapter).notifyDataSetChanged()
                                                tv_noti_findfriends.visibility = View.VISIBLE
                                            }
                                        }
                                    })
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (ed_find_friend.text.toString().equals("") || (ed_find_friend.text.toString().equals(null))) {
                    btnsearch.isEnabled = false
                } else {
                    btnsearch.isEnabled = true
                }

            }

            //  mDatabase!!.addValueEventListener(addValueEventListener)
        })
    }
}
