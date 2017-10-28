package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ute.tinit.chatkotlin.R
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_find_friend_location
import com.ute.tinit.chatkotlin.Activity.activity_profile
import com.ute.tinit.chatkotlin.DataClass.UserDC
import kotlinx.android.synthetic.main.layout_fragment_more.*
import kotlinx.android.synthetic.main.layout_fragment_more.view.*
import com.google.firebase.auth.FirebaseAuth



class fragment_more : Fragment() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_more, container, false)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid= mAuth!!.uid!!

        Log.d("AAA", "userid " + userid)

        view.btn_linear_Profile.setOnClickListener {
            Toast.makeText(activity, "Open profile", Toast.LENGTH_SHORT).show()
            var intent = Intent(activity, activity_profile::class.java)
            startActivity(intent)
        }
        btnTimQuanhDay(view)
        loadData(view)
        return view
    }

    fun btnTimQuanhDay(view: View) {
        view.ln_timquanhday.setOnClickListener {
            var intentTQD = Intent(activity, activity_find_friend_location::class.java)
            startActivity(intentTQD)
        }
    }

    fun loadData(view: View) {
        var getuser: UserDC
        mDatabase!!.child("users").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Toast.makeText(context, "AAA", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        getuser = p0!!.getValue(UserDC::class.java)!!
                        //  name= getuser.name!!
                        // avartaURL= getuser.avarta!!
                        tv_user_name_more.text = getuser.name!!
                        Picasso.with(context)
                                .load(getuser.avarta!!)
                                .error(R.drawable.default_avarta)
                                .into(view.image_avarta)
                    }
                })
        //  mDatabase!!.addValueEventListener(addValueEventListener)
    }
}

