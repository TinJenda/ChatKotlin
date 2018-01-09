package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.FragmentTabHost
import com.ute.tinit.chatkotlin.R

class fragment_contacts : Fragment() {

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
//            val arg2 = Bundle()
//            arg2.putInt("Arg for Frag2", 2)
//            mTabHost!!.addTab(mTabHost!!.newTabSpec("Tab2").setIndicator(custom_tab_group),
//                    fragment_contacts_group::class.java, arg2)
//            mTabHost!!.setOnTabChangedListener(OnTabChangeListener {
//                mTabHost!!.getTabWidget().getChildAt( mTabHost!!.getCurrentTab())
//                        .setBackgroundResource(R.drawable.tab_selector) // selected
//        })

        return mTabHost
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
