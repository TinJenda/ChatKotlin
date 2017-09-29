package com.ute.tinit.chatkotlin

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.ute.tinit.chatkotlin.Fragment.fragment_chat
import com.ute.tinit.chatkotlin.Fragment.fragment_contacts
import com.ute.tinit.chatkotlin.Fragment.fragment_more
import com.ute.tinit.chatkotlin.Fragment.fragment_time
import com.ute.tinit.chatkotlin.Adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity () {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabViewPage()
    }

    fun tabViewPage()
    {
        var viewPageAdapter= ViewPagerAdapter(supportFragmentManager)
        viewPageAdapter.addFragment(fragment_chat(),"Chat")
        viewPageAdapter.addFragment(fragment_contacts(),"Contacts")
        viewPageAdapter.addFragment(fragment_time(),"TimeLine")
        viewPageAdapter.addFragment(fragment_more(),"More")
        viewpager_main.adapter=viewPageAdapter

        viewpager_main.setCurrentItem(0)
        btnChat.isPressed()
        clickChat()

        /*Event click change icon tabview*/
        btnChat.setOnClickListener {
            viewpager_main.setCurrentItem(0,true);
            clickChat()
        }
        btnContacts.setOnClickListener{
            clickContacts()
            viewpager_main.setCurrentItem(1,true);
        }
        btnTimeLine.setOnClickListener {
            clickTimeline()
            viewpager_main.setCurrentItem(2,true);
        }
        btnMore.setOnClickListener {
            clickMore()
            viewpager_main.setCurrentItem(3,true);

        }

        viewpager_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        btnChat.isPressed()
                        clickChat()
                    }
                    1 -> {
                        btnContacts.isPressed()
                        clickContacts()
                    }
                    2->{
                        btnTimeLine.isPressed()
                        clickTimeline()
                    }
                    3->{
                        btnMore.isPressed()
                        clickMore()
                    }
                }
            }



        })
   }

    fun clickChat()
    {
        btnChat.setImageResource(R.drawable.ic_chat_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
    }
    fun clickContacts()
    {
        btnContacts.setImageResource(R.drawable.ic_contacts_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnChat.setImageResource(R.drawable.ic_chat_black)
    }
    fun clickTimeline()
    {
        btnTimeLine.setImageResource(R.drawable.ic_timeline_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_chat_black)
    }
    fun clickMore()
    {
        btnMore.setImageResource(R.drawable.ic_more_active)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_chat_black)
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
//        var intentTab1= Intent(this@MainActivity,fragment_chat::class.java)
//        var intentTab2= Intent(this@MainActivity,fragment_contacts::class.java)
//        var intentTab3= Intent(this@MainActivity,fragment_time::class.java)
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
//   }
}

