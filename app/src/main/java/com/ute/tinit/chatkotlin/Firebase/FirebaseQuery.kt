package com.ute.tinit.chatkotlin.Firebase

import com.google.firebase.auth.FirebaseAuth
import android.R.attr.y
import android.R.attr.x
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase




/**
 * Created by tin3p on 11/2/2017.
 */

class FirebaseQuery {
    fun mReadDataOnce(child: String, listener: OnGetDataListener) {
        listener.onStart()
        FirebaseDatabase.getInstance().reference.child(child).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener.onSuccess(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.onFailed(databaseError)
            }
        })
    }
}