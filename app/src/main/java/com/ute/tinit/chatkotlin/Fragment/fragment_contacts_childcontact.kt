package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ContactAdapter
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class fragment_contacts_childcontact : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ContactAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null

    var childRef: DatabaseReference? = null;
    var friendListEventListener :ChildEventListener?=null
    var userid = ""
    var data = arrayListOf<ContactAdapter.AdapterContact>()
    var dataTemp = arrayListOf<ContactAdapter.AdapterContact>()

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

        childRef = mDatabase!!.child("friends").child(userid);

        friendListEventListener = object : ChildEventListener {

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                var friendId = p0!!.value.toString()
                mDatabase!!.child("users").child(friendId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {}

                            override fun onDataChange(p0: DataSnapshot?) {
                                var getFriend: UserDC = p0!!.getValue(UserDC::class.java)!!
                                var tempOnline = getFriend.online
                                var isonline: Boolean = true
                                if (tempOnline == 1) {
                                    isonline = true
                                } else {
                                    isonline = false
                                }
                                val contact = ContactAdapter.AdapterContact(getFriend.userID!!, getFriend.name!!, getFriend.avatar!!, isonline)
                                (mRecyclerView!!.adapter as ContactAdapter).notifyItemDataChange(contact)
                            }

                        })
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                var friendId = p0!!.value.toString()
                mDatabase!!.child("users").child(friendId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {}

                            override fun onDataChange(p0: DataSnapshot?) {
                                var getFriend: UserDC = p0!!.getValue(UserDC::class.java)!!
                                var tempOnline = getFriend.online
                                var isonline: Boolean = true
                                if (tempOnline == 1) {
                                    isonline = true
                                } else {
                                    isonline = false
                                }
                                val contact = ContactAdapter.AdapterContact(getFriend.userID!!, getFriend.name!!, getFriend.avatar!!, isonline)

                                if ((mRecyclerView!!.adapter as ContactAdapter).isContactAdded(contact)==false)
                                    (mRecyclerView!!.adapter as ContactAdapter).addItem(contact)
                            }

                        })
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                // removed friend ID
                var removedFriendID=p0!!.value.toString()
                (mRecyclerView!!.adapter as ContactAdapter).removeItem(removedFriendID)

            }

            override fun onCancelled(p0: DatabaseError?) {
            }


        }

        //setData()
//
        mAdapter = ContactAdapter(context, data)
        mRecyclerView!!.adapter = mAdapter

        // loadData(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        setData()
    }

    override fun onStop() {
        super.onStop()
        (mRecyclerView!!.adapter as ContactAdapter).clear()
        // xoas du lieu adapter

    }

    fun setData() {
        Log.w("func","SetData() called");
        childRef!!.addChildEventListener(friendListEventListener)
    }
}
