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


class fragment_contacts_childcontact : Fragment(), ContactAdapter.ViewHolder.ClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ContactAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""

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
        mAdapter = ContactAdapter(context, setData(), this)
        mRecyclerView!!.adapter = mAdapter
        // loadData(view)
        return view
    }


    fun setData(): List<ContactDC> {
        val data = ArrayList<ContactDC>()
        mDatabase!!.child("users").child(userid).child("friend")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(context, "AAA", Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        for (postSnapshot in p0!!.getChildren()) {
                            Log.d("test", "Key: " + postSnapshot.key + " = " + postSnapshot.getValue().toString())
                            var idFriend = postSnapshot.getValue().toString() //kiem tra
                            mDatabase!!.child("users").child(idFriend)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot?) {
                                            var getFriend: UserDC = p0!!.getValue(UserDC::class.java)!!
                                            var tempOnline = getFriend.online
                                            var isonline: Boolean = true
                                            if (tempOnline == 1) {
                                                isonline = true
                                            } else {
                                                isonline = false
                                            }
                                            val contact: ContactDC = ContactDC(getFriend.name!!, getFriend.avatar!!, isonline)
                                            Log.d("AAA"," "+getFriend.name!!)
                                            Log.d("AAA"," "+getFriend.avatar!!)
                                            Log.d("AAA"," "+tempOnline)
                                            data.add(contact)
                                            mAdapter!!.notifyDataSetChanged();
                                        }

                                        override fun onCancelled(p0: DatabaseError?) {
                                        }
                                    })
                        }
                    }
                })
        return data
    }

    override fun onItemClicked(position: Int) {

    }

    override fun onItemLongClicked(position: Int): Boolean {
        toggleSelection(position)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun toggleSelection(position: Int) {
        mAdapter!!.toggleSelection(position)
    }

}
