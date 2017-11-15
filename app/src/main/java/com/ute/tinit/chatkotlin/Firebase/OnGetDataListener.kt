package com.ute.tinit.chatkotlin.Firebase

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot



/**
 * Created by tin3p on 11/14/2017.
 */
interface OnGetDataListener {
        fun onStart()
        fun onSuccess(data: DataSnapshot)
        fun onFailed(databaseError: DatabaseError)
}