package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ute.tinit.chatkotlin.R
import android.content.Intent
import android.widget.Toast
import com.ute.tinit.chatkotlin.Activity.activity_profile
import kotlinx.android.synthetic.main.layout_fragment_more.*
import kotlinx.android.synthetic.main.layout_fragment_more.view.*


class fragment_more : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_more, container, false)
         view.btn_linear_Profile.setOnClickListener{
             Toast.makeText(activity,"Open profile",Toast.LENGTH_SHORT).show()
                           var intent=Intent(activity, activity_profile::class.java)
                           startActivity(intent)
         }
        return view
    }

}

