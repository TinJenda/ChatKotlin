package com.ute.tinit.chatkotlin

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
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
        btnChat.setImageResource(R.drawable.ic_message_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnInsertMore.visibility= View.VISIBLE
        btnInsertFriend.visibility=View.GONE
        btnInsertPicture.visibility=View.GONE
        btnSetting.visibility=View.GONE
    }
    fun clickContacts()
    {
        btnContacts.setImageResource(R.drawable.ic_contact_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
        btnInsertMore.visibility= View.GONE
        btnInsertFriend.visibility=View.VISIBLE
        btnInsertPicture.visibility=View.GONE
        btnSetting.visibility=View.GONE
    }
    fun clickTimeline()
    {
        btnInsertMore.visibility= View.GONE
        btnInsertFriend.visibility=View.GONE
        btnInsertPicture.visibility=View.VISIBLE
        btnSetting.visibility=View.GONE
        btnTimeLine.setImageResource(R.drawable.ic_timeline_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
    }
    fun clickMore()
    {
        btnInsertMore.visibility= View.GONE
        btnInsertFriend.visibility=View.GONE
        btnInsertPicture.visibility=View.GONE
        btnSetting.visibility=View.VISIBLE
        btnMore.setImageResource(R.drawable.ic_more_active)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
    }

}

