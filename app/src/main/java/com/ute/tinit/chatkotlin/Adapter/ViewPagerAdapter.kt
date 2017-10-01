package com.ute.tinit.chatkotlin.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import java.util.ArrayList

//ChildrenTabAdapter view page
class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    internal var fragments: MutableList<Fragment> = ArrayList()
    internal var titleFragemnts: MutableList<String> = ArrayList()

    fun addFragment(fm: Fragment, nameFR: String) {
        fragments.add(fm)
        titleFragemnts.add(nameFR)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titleFragemnts[position]
    }
}
