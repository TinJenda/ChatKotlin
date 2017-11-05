package com.ute.tinit.chatkotlin.Firebase

import com.google.firebase.auth.FirebaseAuth
import android.R.attr.y
import android.R.attr.x
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.DataClass.UserDC


/**
 * Created by tin3p on 11/2/2017.
 */

class FirebaseQuery {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    lateinit var getInfoUser: UserDC

    fun start()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
    }

    fun getUser(idUser: String): UserDC {
        mDatabase!!.child("users").child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                getInfoUser = p0!!.getValue(UserDC::class.java)!!
            }

        })
        return getInfoUser
    }
}