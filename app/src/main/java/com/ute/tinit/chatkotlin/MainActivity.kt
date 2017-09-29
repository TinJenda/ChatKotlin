package com.ute.tinit.chatkotlin

import android.app.Fragment
import android.app.TabActivity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.TabHost
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabViewPage()
        //tabHost()
    }

    fun tabViewPage()
    {
        var viewPageAdapter=ViewPagerAdapter(supportFragmentManager)
        viewPageAdapter.addFragment(activity_chat(),"Chat")
        viewPageAdapter.addFragment(activity_contacts(),"Contacts")
        viewPageAdapter.addFragment(activity_time(),"TimeLine")
        viewPageAdapter.addFragment(activity_more(),"More")
        viewpager_main.adapter=viewPageAdapter
    }
//    fun tabHost()
//    {
//        //Creating tab menu.
//        var tab1: TabHost.TabSpec = tabhost.newTabSpec("1")
//        var tab2: TabHost.TabSpec = tabhost.newTabSpec("2")
//        var tab3: TabHost.TabSpec = tabhost.newTabSpec("3")
//        var tab4: TabHost.TabSpec = tabhost.newTabSpec("4")
//
//        var inflater:LayoutInflater = getLayoutInflater()
//        var custom_home_tab:View? = inflater.inflate(R.layout.custom_search_tab, null)
//        var custom_home_tab2:View? = inflater.inflate(R.layout.custom_search_tab, null)
//        var custom_home_tab3:View? = inflater.inflate(R.layout.custom_search_tab, null)
//        var custom_home_tab4:View? = inflater.inflate(R.layout.custom_search_tab, null)
//
//
//        //Set tab 1 activity to tab 1 menu.
//        var intentTab1= Intent(this@MainActivity,activity_chat::class.java)
//        var intentTab2= Intent(this@MainActivity,activity_contacts::class.java)
//        var intentTab3= Intent(this@MainActivity,activity_time::class.java)
//        var intentTab4= Intent(this@MainActivity,layout_activity_more::class.java)
//        tab1.setContent(intentTab1)
//        tab2.setContent(intentTab2)
//        tab3.setContent(intentTab3)
//        tab4.setContent(intentTab4)
//
//        //
//        //setting tabHome
//        tab1.setIndicator(custom_home_tab)
//        tab2.setIndicator(custom_home_tab2)
//        tab3.setIndicator(custom_home_tab3)
//        tab4.setIndicator(custom_home_tab4)
////        tab2.setIndicator(custom_collection);
////        //Set tab 3 activity to tab 1 menu.
////        tab2.setContent(new Intent(this, CollectionActivity.class));
////
////
////        //Set tab 1 activity to tab 1 menu.
////        tab3.setIndicator(custom_search);
////        tab3.setContent(new Intent(this, SearchActivity.class));
//////
////        //Set tab 1 activity to tab 1 menu.
////        tab4.setContent(new Intent(this, AccountActivity.class));
////        tab4.setIndicator(custom_account);
//        //Set tab 1 activity to tab 1 menu.
//
////        //Set tab 1 activity to tab 1 menu.
//
//        tabhost.addTab(tab1)
//        tabhost.addTab(tab2)
//        tabhost.addTab(tab3)
//        tabhost.addTab(tab4)
//
//
//        tabhost.getTabWidget().setDividerDrawable(null)
//
//    }
}

