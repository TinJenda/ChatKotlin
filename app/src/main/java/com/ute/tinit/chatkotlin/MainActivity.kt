package com.ute.tinit.chatkotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.ute.tinit.chatkotlin.Fragment.fragment_chat
import com.ute.tinit.chatkotlin.Fragment.fragment_contacts
import com.ute.tinit.chatkotlin.Fragment.fragment_more
import com.ute.tinit.chatkotlin.Fragment.fragment_time
import com.ute.tinit.chatkotlin.Adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.ute.tinit.chatkotlin.Activity.activity_setting
import android.widget.AdapterView.AdapterContextMenuInfo


class MainActivity : AppCompatActivity() {

    var doubleClickExit: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabViewPage()
        btnSetting()
        btnInsertMore()
    }

    fun tabViewPage() {
        var viewPageAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPageAdapter.addFragment(fragment_chat(), "Chat")
        viewPageAdapter.addFragment(fragment_contacts(), "Contacts")
        viewPageAdapter.addFragment(fragment_time(), "TimeLine")
        viewPageAdapter.addFragment(fragment_more(), "More")
        viewpager_main.adapter = viewPageAdapter

        viewpager_main.setCurrentItem(0)
        btnChat.isPressed()
        clickChat()

        /*Event click change icon tabview*/
        btnChat.setOnClickListener {
            viewpager_main.setCurrentItem(0, true);
            clickChat()
        }
        btnContacts.setOnClickListener {
            clickContacts()
            viewpager_main.setCurrentItem(1, true);
        }
        btnTimeLine.setOnClickListener {
            clickTimeline()
            viewpager_main.setCurrentItem(2, true);
        }
        btnMore.setOnClickListener {
            clickMore()
            viewpager_main.setCurrentItem(3, true);
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
                    2 -> {
                        btnTimeLine.isPressed()
                        clickTimeline()
                    }
                    3 -> {
                        btnMore.isPressed()
                        clickMore()
                    }
                }
            }
        })
    }

    fun clickChat() {
        btnChat.setImageResource(R.drawable.ic_message_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnInsertMore.visibility = View.VISIBLE
        btnInsertFriend.visibility = View.GONE
        btnInsertPicture.visibility = View.GONE
        btnSetting.visibility = View.GONE
    }

    fun clickContacts() {
        btnContacts.setImageResource(R.drawable.ic_contact_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
        btnInsertMore.visibility = View.GONE
        btnInsertFriend.visibility = View.VISIBLE
        btnInsertPicture.visibility = View.GONE
        btnSetting.visibility = View.GONE
    }

    fun clickTimeline() {
        btnInsertMore.visibility = View.GONE
        btnInsertFriend.visibility = View.GONE
        btnInsertPicture.visibility = View.VISIBLE
        btnSetting.visibility = View.GONE
        btnTimeLine.setImageResource(R.drawable.ic_timeline_active)
        btnMore.setImageResource(R.drawable.ic_more_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
    }

    fun clickMore() {
        btnInsertMore.visibility = View.GONE
        btnInsertFriend.visibility = View.GONE
        btnInsertPicture.visibility = View.GONE
        btnSetting.visibility = View.VISIBLE
        btnMore.setImageResource(R.drawable.ic_more_active)
        btnTimeLine.setImageResource(R.drawable.ic_timeline_black)
        btnContacts.setImageResource(R.drawable.ic_contacts_black)
        btnChat.setImageResource(R.drawable.ic_message_black)
    }

    //back to return current item 1 => exit
    override fun onBackPressed() {
        if (doubleClickExit) {
            super.onBackPressed()
            return
        }

        if (viewpager_main.currentItem == 3 || viewpager_main.currentItem == 2 || viewpager_main.currentItem == 1) {
            viewpager_main.currentItem = 0
            return
        }
        this@MainActivity.doubleClickExit = true
        Handler().postDelayed(Runnable { doubleClickExit = false }, 1000)
    }


    fun toastShow(text: String, context: Context) { /*show toast*/
        var m_currentToast: Toast? = null
        if (m_currentToast != null) {
            m_currentToast!!.cancel()
        }
        m_currentToast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        m_currentToast!!.show()

    }

    fun btnSetting() {
        btnSetting.setOnClickListener {
            var intent = Intent(this@MainActivity, activity_setting::class.java)
            startActivity(intent)
        }
    }

    fun btnInsertMore() {

        btnInsertMore.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                //Creating the instance of PopupMenu
                val popup = PopupMenu(this@MainActivity, btnInsertMore)
                //Inflating the Popup using xml file

                popup.menuInflater.inflate(R.menu.poupup_menu, popup.menu)
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        // Toast.makeText(this@MainActivity,"CLick "+item.title,Toast.LENGTH_SHORT).show()
                        if (item.title.toString().equals("Thêm bạn")) {
                            Log.d("AAA", "Bạn click thêm bạn")
                            toastShow("Bạn click thêm bạn", this@MainActivity)
                        } else {
                            Log.d("AAA", "Bạn click tạo nhóm")
                            toastShow("Bạn click tạo nhóm", this@MainActivity)
                        }
                        return true
                    }
                })
                //... initialization of your PopupMenu
                val menuHelper: Any
                val argTypes: Array<Class<*>>
                try {
                    val fMenuHelper = PopupMenu::class.java.getDeclaredField("mPopup")
                    fMenuHelper.isAccessible = true
                    menuHelper = fMenuHelper.get(popup)
                    argTypes = arrayOf<Class<*>>(Boolean::class.java)
                    menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes).invoke(menuHelper, true)
                } catch (e: Exception) {
                    // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                    //
                    // In either case, an exception indicates something is wrong with the reflection code, or the
                    // structure of the PopupMenu class or its dependencies has changed.
                    //
                    // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                    // but in the case that they do, we simply can't force icons to display, so log the error and
                    // show the menu normally.
                    //  Log.d("AAA",""+e.message)
                }

                popup.show()//showing popup menu
            }
        })
    }


}

