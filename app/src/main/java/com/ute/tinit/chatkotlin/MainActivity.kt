package com.ute.tinit.chatkotlin

import android.app.Dialog
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
import android.widget.*
import com.ute.tinit.chatkotlin.Fragment.fragment_conversation
import com.ute.tinit.chatkotlin.Fragment.fragment_contacts
import com.ute.tinit.chatkotlin.Fragment.fragment_more
import com.ute.tinit.chatkotlin.Fragment.fragment_time
import com.ute.tinit.chatkotlin.Adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.chatkotlin.tintt.listview.AdapterListSelectFriends
import com.ute.tinit.chatkotlin.Activity.activity_setting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Activity.activity_chat_active
import com.ute.tinit.chatkotlin.Activity.activity_find_friend
import com.ute.tinit.chatkotlin.DataClass.ConversationDC
import com.ute.tinit.chatkotlin.DataClass.MessageDC
import com.ute.tinit.chatkotlin.DataClass.SelectFriendsDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import kotlinx.android.synthetic.main.content_chat_activity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    var doubleClickExit: Boolean = false
    var userid = ""
    var userName = ""
    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
//        mAuth = FirebaseAuth.getInstance()
//        userid= mAuth!!.uid!!
////        var intent=intent
////        var userid=intent.getStringExtra("userid")
//        Log.d("BBB",userid)

//        val bundle = Bundle()
//        bundle.putString("edttext", "From Activity")
//        val fragobj = fragment_more()
//        fragobj.setArguments(bundle)
        mDatabase!!.child("users").child(userid).child("online").setValue(1)
        mDatabase!!.child("users").child(userid).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.value != null) {
                    userName = p0!!.value.toString()
                }
            }

        })
        tabViewPage()
        btnSetting()
        btnInsertMore()
        btnInsertFriend()
    }

    fun btnInsertFriend() {
        btnInsertFriend.setOnClickListener {
            var intent = Intent(this@MainActivity, activity_find_friend::class.java)
            startActivity(intent)
        }
    }

    fun tabViewPage() {
        var viewPageAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPageAdapter.addFragment(fragment_conversation(), "Chat")
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
                            var intent = Intent(this@MainActivity, activity_find_friend::class.java)
                            startActivity(intent)
                        } else {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_list_create_group)
                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val lv = dialog.findViewById<ListView>(R.id.lv_select_friend)
                            val taonhom = dialog.findViewById<Button>(R.id.taonhom)
                            var arrtemp = ArrayList<SelectFriendsDC>()

                            mDatabase!!.child("friends").child(userid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError?) {
                                        }
                                        override fun onDataChange(p0: DataSnapshot?) {
                                            if (p0!!.value != null) {
                                                arrtemp.clear()
                                                for (snap in p0!!.children) {
                                                    mDatabase!!.child("users").child(snap.value.toString())
                                                            .addValueEventListener(object : ValueEventListener {
                                                                override fun onCancelled(p0: DatabaseError?) {
                                                                }

                                                                override fun onDataChange(p0: DataSnapshot?) {
                                                                    if (p0!!.value != null) {
                                                                        var tempUser: UserDC = p0!!.getValue(UserDC::class.java)!!
                                                                        var save: SelectFriendsDC = SelectFriendsDC(tempUser.userID, tempUser.name, tempUser.avatar)
                                                                        if((lv.adapter as AdapterListSelectFriends).isContactAdded(save))
                                                                        {
                                                                            (lv.adapter as AdapterListSelectFriends).notifyItemDataChange(save)
                                                                        }
                                                                        else
                                                                        {
                                                                            (lv.adapter as AdapterListSelectFriends).addItem(save)
                                                                        }
                                                                    } else {
                                                                        Log.d("mainx", "info null")
                                                                    }
                                                                    mDatabase!!.child("users").child(snap.value.toString()).removeEventListener(this)

                                                                }

                                                            })
                                                }
                                            } else {
                                                Log.d("mainx", "Friends null")
                                            }
                                            mDatabase!!.child("friends").child(userid).removeEventListener(this)
                                        }

                                    })

                            lv.adapter = AdapterListSelectFriends(this@MainActivity, arrtemp)
                            lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//                            lv.setOnItemClickListener(object :AdapterView.OnItemClickListener{
//                                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                                    Toast.makeText(this@MainActivity,"item clcik",Toast.LENGTH_SHORT).show()
//                                }
//
//                            })
                            taonhom.setOnClickListener {
                                var tempListUser = ArrayList<String>((lv.adapter as AdapterListSelectFriends).getListFriendChecked())
                                if (tempListUser.size < 1) {
                                    Toast.makeText(this@MainActivity, "Nhóm phải từ 2 người trở lên...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(v.context, "Tao nhom", Toast.LENGTH_SHORT).show()
                                    //them nhom chat tai day
                                    Log.d("TTT", "DATA NULL")
                                    //ko ton tai cai nao cung tao moi tai day
                                    //tao cuoc tro chuyen moi tai day

                                    var listMessage = arrayListOf<String>()
                                    var myRef = mDatabase!!.child("conversation").push()
                                    var conver = ConversationDC(myRef.key, "", tempListUser, true, listMessage)
                                    myRef.setValue(conver).addOnCompleteListener {
                                        for (user in tempListUser) {
                                            mDatabase!!.child("user_listconver").child(user).push().setValue(myRef.key)
                                        }
                                        mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                    }

                                    Log.d("TTT", "THEM CHAT DATANULL")
                                    val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                    var userSeen = arrayListOf<String>(userid)
                                    val currentTime = df.format(Calendar.getInstance().time)
                                    var myRefMess = mDatabase!!.child("conversation").child(myRef.key).child("messages").push()
                                    var mess = MessageDC(myRefMess.key, "" + myRef.key, userid, userName + " đã tạo cuộc trò chuyện nhóm", "0", currentTime, userSeen)
                                    myRefMess.setValue(mess)
                                    //them chat
                                    dialog.dismiss()
                                    var intent = Intent(v.context, activity_chat_active::class.java)
                                    intent.putExtra("conversation", myRef.key)
                                    intent.putExtra("group_check", true)
                                    startActivity(intent)
                                }
                            }
                            dialog.show()
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
                }

                popup.show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mDatabase!!.child("users").child(userid).child("online").setValue(0)
    }
}

