package com.ute.tinit.chatkotlin.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import com.ute.tinit.chatkotlin.Activity.activity_chat
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_fragment_chat.view.*

class fragment_chat : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_chat, container, false)

        return view
    }

}
