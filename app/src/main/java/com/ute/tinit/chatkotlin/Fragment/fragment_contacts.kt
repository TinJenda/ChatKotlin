package com.ute.tinit.chatkotlin.Fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.FragmentTabHost
import android.widget.Toast
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.custom_tab_contact.*
import android.widget.TextView
import android.widget.TabHost.TabSpec
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.widget.TabHost.OnTabChangeListener
import android.widget.TabHost
import android.widget.TabWidget






class fragment_contacts : Fragment () {

     var mTabHost: FragmentTabHost? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
         mTabHost = FragmentTabHost(activity)
            mTabHost!!.setup(activity, childFragmentManager, R.layout.layout_fragment_contacts)
            val arg1 = Bundle()
            arg1.putInt("Arg for Frag1", 1)
            val custom_tab_contact = inflater!!.inflate(R.layout.custom_tab_contact, null)
            val custom_tab_group = inflater!!.inflate(R.layout.custom_tab_group, null)
            mTabHost!!.addTab(mTabHost!!.newTabSpec("Tab1").setIndicator(custom_tab_contact),
                    fragment_contacts_childcontact::class.java, arg1)
            val arg2 = Bundle()
            arg2.putInt("Arg for Frag2", 2)
            mTabHost!!.addTab(mTabHost!!.newTabSpec("Tab2").setIndicator(custom_tab_group),
                    fragment_contacts_group::class.java, arg2)

//            mTabHost!!.setOnTabChangedListener(OnTabChangeListener {
//                mTabHost!!.getTabWidget().getChildAt( mTabHost!!.getCurrentTab())
//                        .setBackgroundResource(R.drawable.tab_selector) // selected
//        })

        return mTabHost
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }


    private fun createTabView(context: Context, text: String): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_tab_contact, null)
        val animator = ObjectAnimator.ofInt(txtChildContact, "textColor", 0xFF8363FF.toInt(), 0xFFC953BE.toInt())
        return view
    }

}
