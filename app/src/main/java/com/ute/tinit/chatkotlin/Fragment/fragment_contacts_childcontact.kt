package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ContactAdapter
import com.ute.tinit.chatkotlin.DataClass.ContactDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_fragment_more.*
import kotlinx.android.synthetic.main.layout_fragment_more.view.*
import java.util.ArrayList
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.GenericTypeIndicator
import android.support.v7.widget.DividerItemDecoration
import com.ute.tinit.chatkotlin.R.id.recyclerView


class fragment_contacts_childcontact : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ContactAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    var data = arrayListOf<ContactAdapter.AdapterContact>()

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_contact_contactchild, null, false)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        Log.d("userid", " " + userid)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)

        setData()
//
        mAdapter = ContactAdapter(context, data)
        mRecyclerView!!.adapter = mAdapter

        // loadData(view)
        return view
    }

    fun setData() {

        mDatabase!!.child("friends").child(userid)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                        if (p0!!.getValue() != null) {
                            var friendId = p0!!.value.toString()
                            Log.d("friendxxx", friendId)
                            //get dua len list
                            mDatabase!!.child("users").child(friendId)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                        }

                                        override fun onDataChange(p0: DataSnapshot?) {
                                            Log.d("AAA", "Update tai day")
                                            var getFriend: UserDC = p0!!.getValue(UserDC::class.java)!!
                                            var tempOnline = getFriend.online
                                            var isonline: Boolean = true
                                            if (tempOnline == 1) {
                                                isonline = true
                                            } else {
                                                isonline = false
                                            }
                                            val contact = ContactAdapter.AdapterContact(getFriend.userID!!, getFriend.name!!, getFriend.avatar!!, isonline)
                                            Log.d("AAA", " " + getFriend.name!!)
                                            Log.d("AAA", " " + getFriend.avatar!!)
                                            Log.d("AAA", " " + tempOnline)

                                            // kiem tra contact da co trong list
                                            if ((mRecyclerView!!.adapter as ContactAdapter).isContactAdded(contact))
                                                (mRecyclerView!!.adapter as ContactAdapter).notifyItemDataChange(contact)
                                            else
                                                (mRecyclerView!!.adapter as ContactAdapter).addItem(contact)
                                        }


                                    })
                        } else {
                            Log.d("friendx", "DATA NULL")
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                        var removedFriendID = p0!!.value.toString()
                        (mRecyclerView!!.adapter as ContactAdapter).removeItem(removedFriendID)
                    }

                })
    }


    private fun toggleSelection(position: Int) {
        mAdapter!!.toggleSelection(position)
    }

}