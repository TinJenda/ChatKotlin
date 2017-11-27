package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.ChatAction.ChatDataAdapter
import com.ute.tinit.chatkotlin.DataClass.ChatDataDC
import com.ute.tinit.chatkotlin.DataClass.ConversationDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.content_chat_activity.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.ute.tinit.chatkotlin.DataClass.MessageDC
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_activity_chat_active.*
import kotlinx.android.synthetic.main.layout_flash_screen.*
import kotlin.collections.ArrayList


class activity_chat_active : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatDataAdapter? = null
    private var text: EditText? = null
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    var userid = ""
    var userFR = ""
    var group_check = false
    var converID: String = ""
    var currentPage: Double = 0.0
    var nameconver = ""
    val data = arrayListOf<ChatDataDC>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_chat_active)
        toolbar_chat.title = ""
        setSupportActionBar(toolbar_chat)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        var intent = intent
        group_check = intent.extras.getBoolean("group_check")
        if (group_check.equals(false)) {
            userFR = intent.getStringExtra("userfriend")
            setData()
        } else {
            converID = intent.getStringExtra("conversation")
           // nameconver = intent.getStringExtra("nameconver")
            setDataChatGroup()
        }
        btnSend()
        loadDATA()
        textEmply()
        profileFriend()
    }

    fun loadChatGroup() {

    }

    fun profileFriend() {
//        tv_nameuser.setOnClickListener {
//            var intent = Intent(this@activity_chat_active, activity_friend_profile::class.java)
//            intent.putExtra("userfriend",userFR)
//            startActivity(intent)
//        }

        btnMoreChat.setOnClickListener {
            Toast.makeText(this@activity_chat_active, "Ahih", Toast.LENGTH_SHORT).show()
        }
    }

    fun getNameById(idUser: String): String {
        var temp = ""
        mDatabase!!.child("users").child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.getValue() != null) {
                    var getFriend: UserDC = p0.getValue(UserDC::class.java)!!
                    temp = getFriend.name!!
                    Log.d("userFR", temp)
                    Log.d("userFR", getFriend.userID)
                }
            }
        })
        return temp
    }

    fun aniBtnSendMore_activesendmore() {
        var animationLogoName: Animation = AnimationUtils.loadAnimation(this@activity_chat_active, R.anim.zoomandrotate)
        btnSendMore.startAnimation(animationLogoName)
    }

    fun animation_BtnSend_actives() {
        var animationLogoName: Animation = AnimationUtils.loadAnimation(this@activity_chat_active, R.anim.zoomandrotate)
        btnSend.startAnimation(animationLogoName)
    }

    fun removeAnimotionbtnSendmore() {
        btnSendMore.animate().cancel()
        btnSendMore.clearAnimation()
    }

    fun removeAnimotionbtnSend() {
        btnSend.animate().cancel()
        btnSend.clearAnimation()
    }

    fun textEmply() {
        btnSend.visibility = View.GONE
        btnSendMore.visibility = View.VISIBLE
        et_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    animation_BtnSend_actives()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("AAA", "onTextChanged " + et_message.text)
                var checkAni: Boolean = true
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    Log.d("AAA", "NULL TEXT")
                    btnSend.visibility = View.GONE
                    btnSendMore.visibility = View.VISIBLE
                    aniBtnSendMore_activesendmore()
                    removeAnimotionbtnSend()
                    var checkAni = false
                } else {
                    removeAnimotionbtnSendmore()
                    Log.d("AAA", "NOT NULL TEXT")
                    btnSend.visibility = View.VISIBLE
                    btnSendMore.visibility = View.GONE
                }
            }
        })
    }

    fun btnSend() {
        btnSend.setOnClickListener {
            if (!text!!.text.equals("") || !text!!.text.equals(null)) {
                if (group_check == false) {
                    mDatabase!!.child("user_listconver").child(userid)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    var checkExitsConver: Boolean = false
                                    if (p0!!.getValue() != null) {
                                        for (snap in p0!!.children) {
                                            //vao id cua conversation ma user do chat
                                            mDatabase!!.child("conversation").child("" + snap!!.value)
                                                    .addValueEventListener(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError?) {
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                            if (p0!!.value != null) {
                                                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                                if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                                        (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {
                                                                    //xu ly them chat trong nay
                                                                    checkExitsConver = true
                                                                    //them chat
                                                                    var myRefMess = mDatabase!!.child("conversation").child("" + snap!!.value).child("messages").push()
                                                                    Log.d("TTT", "THEM CHAT")
                                                                    val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                                    val currentTime = df.format(Calendar.getInstance().time)
                                                                    var userSeen = arrayListOf<String>(userid)
                                                                    var mess = MessageDC(myRefMess.key, "" + snap!!.value, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                                                    myRefMess.setValue(mess)
                                                                    text!!.setText("")
                                                                    mDatabase!!.child("conversation").child("" + snap!!.value).removeEventListener(this)

                                                                }
                                                            }
                                                        }

                                                    })
                                            if (checkExitsConver == true) {
                                                break
                                            }
                                        }

                                        val handler: Handler = Handler()
                                        handler.postDelayed(Runnable {
                                            if (checkExitsConver == false) {
                                                //tao cuoc tro chuyen moi tai day
                                                var tempListUser = arrayListOf<String>(userid, userFR)
                                                var listMessage = arrayListOf<String>()
                                                var myRef = mDatabase!!.child("conversation").push()
                                                var conver = ConversationDC(myRef.key, "", tempListUser, false, listMessage)
                                                myRef.setValue(conver).addOnCompleteListener {
                                                    mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                                    mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                                                    mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                                    myRef.removeEventListener(this)
                                                }
                                                //them chat
                                                //type=2 is me
                                                Log.d("TTT", "THEM CHAT checkExitsConver==false")
                                                val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                val currentTime = df.format(Calendar.getInstance().time)
                                                var userSeen = arrayListOf<String>(userid)
                                                var myRefMess = mDatabase!!.child("conversation").child(myRef.key).child("messages").push()
                                                var mess = MessageDC(myRefMess.key, "" + myRef.key, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                                myRefMess.setValue(mess)
                                                text!!.setText("")
                                            }
                                        }, 1000)
                                        mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                    } else {
                                        Log.d("TTT", "DATA NULL")
                                        //ko ton tai cai nao cung tao moi tai day
                                        //tao cuoc tro chuyen moi tai day
                                        var tempListUser = arrayListOf<String>(userid, userFR)
                                        var listMessage = arrayListOf<String>()
                                        var myRef = mDatabase!!.child("conversation").push()
                                        var conver = ConversationDC(myRef.key, "", tempListUser, false, listMessage)
                                        myRef.setValue(conver).addOnCompleteListener {
                                            mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                            mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                                            mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                            myRef.removeEventListener(this)
                                        }
                                        //them chat
                                        //type=2 is me
                                        Log.d("TTT", "THEM CHAT DATANULL")
                                        val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                        var userSeen = arrayListOf<String>(userid)
                                        val currentTime = df.format(Calendar.getInstance().time)
                                        var myRefMess = mDatabase!!.child("conversation").child(myRef.key).child("messages").push()
                                        var mess = MessageDC(myRefMess.key, "" + myRef.key, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                        myRefMess.setValue(mess)
                                        text!!.setText("")
                                        mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                    }
                                }

                            })
                } else {
                    mDatabase!!.child("conversation").child(converID)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.value != null) {
                                        var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!

                                        //xu ly them chat trong nay
                                        //them chat
                                        var myRefMess = mDatabase!!.child("conversation").child(converID).child("messages").push()
                                        Log.d("TTT", "THEM CHAT")
                                        val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                        val currentTime = df.format(Calendar.getInstance().time)
                                        var userSeen = arrayListOf<String>(userid)
                                        var mess = MessageDC(myRefMess.key, converID, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                        myRefMess.setValue(mess)
                                        text!!.setText("")
                                        mDatabase!!.child("conversation").child(converID).removeEventListener(this)

                                    }
                                }
                            })

                }
            }

        }
    }


    fun loadDATA() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = ChatDataAdapter(this@activity_chat_active, data as MutableList<ChatDataDC>)
        mRecyclerView!!.adapter = mAdapter


        text = findViewById(R.id.et_message)
        text!!.setOnClickListener { mRecyclerView!!.postDelayed({ mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1) }, 400) }
        if (group_check == false) {
            mDatabase!!.child("users").child(userFR)
                    .addValueEventListener(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.getValue() != null) {
                                        var getFriend: UserDC = p0.getValue(UserDC::class.java)!!
                                        tv_nameuser.setText("" + getFriend.name!!)
                                    }
                                }

                            })
        } else {
            tv_nameuser.setText("Nhóm")
        }
    }

//  private fun mCheckInforInServer(child: String) {
//        FirebaseQuery().mReadDataOnce(child, object : OnGetDataListener {
//            override fun onStart() {
//                //DO SOME THING WHEN START GET DATA HERE
//            }
//
//            override fun onSuccess(data: DataSnapshot) {
//                //DO SOME THING WHEN GET DATA SUCCESS HERE
//            }
//
//            override fun onFailed(databaseError: DatabaseError) {
//                //DO SOME THING WHEN GET DATA FAILED HERE
//            }
//        })
//
//    }

    //    override fun onPause() {
//        Log.d("chatactive","OnPause")
//        if (seenListener != null && mDatabase!=null) {
//            Log.d("chatactive","OnPause seenListener")
//            mDatabase!!.child("user_listconver").child(userid).removeEventListener(seenListener);
//        }
//        super.onPause()
//    }
//
//    override fun onStop() {
//        Log.d("chatactive","onStop")
//        if (seenListener != null && mDatabase!=null) {
//            Log.d("chatactive","onStop seenListener")
//            mDatabase!!.child("user_listconver").child(userid).removeEventListener(seenListener);
//        }
//        super.onStop()
//
//    }
    fun setData() {
        //vào ds cuộc trò chuyện người mình
        mDatabase!!.child("user_listconver").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            for (snap in p0!!.children) {
                                //vao id cua conversation ma user do chat
                                //vào cuộc trò chuyện
                                mDatabase!!.child("conversation").child("" + snap!!.value)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.value != null) {
                                                    var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                    Log.d("TTT", temp.idConver)
                                                    //kiểm tra đâu là cuộc trò chuyện mình
                                                    if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                            (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {

                                                        // load danh sach nguoi = mang
                                                        var userList = ArrayList<String>(temp!!.listUsers)
                                                        var avatar = ""
                                                        mDatabase!!.child("users").child(userFR).child("avatar")
                                                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                                                    }

                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                        if (p0!!.value != null) {
                                                                            avatar = p0!!.value.toString()
                                                                        } else {
                                                                            avatar = "http://1.gravatar.com/avatar/1771f433d2eed201bd40e6de0c3a74a7?s=1024&d=mm&r=g"
                                                                        }
                                                                    }

                                                                })

                                                        // load tin nhan

                                                        mDatabase!!.child("conversation").child(temp.idConver).child("messages")
                                                                .addChildEventListener(object : ChildEventListener {
                                                                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                                                    }

                                                                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                                                    }

                                                                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                                                                        if (p0!!.value != null) {

                                                                            var getMessage: MessageDC = p0!!.getValue(MessageDC::class.java)!!
                                                                            var checkType = getMessage.type

                                                                            if (getMessage.type == "0") {
                                                                                checkType = "0"
                                                                            } else {
                                                                                if (getMessage.idSender == userid) {
                                                                                    checkType = "2"
                                                                                } else {
                                                                                    checkType = "1"
                                                                                }
                                                                            }
                                                                            Log.d("ABCC", "VAO ELSE")

                                                                            //lấy định dạng ngày mặt định
                                                                            val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                                            //parse ngày ở firebase sang dụng hh:mm
                                                                            val t = df.parse(getMessage.date)
                                                                            df.applyPattern("hh:mm")
//
                                                                            var itemx = ChatDataDC(getMessage.id, avatar, checkType, getMessage.content, df.format(t))
                                                                            if ((mRecyclerView!!.adapter as ChatDataAdapter).isAdded(itemx))
                                                                                (mRecyclerView!!.adapter as ChatDataAdapter).notifyDataSetChanged()
                                                                            else
                                                                                (mRecyclerView!!.adapter as ChatDataAdapter).addItem(itemx)
                                                                            mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1)
                                                                        } else {
                                                                            currentPage--
                                                                            Log.d("TTT", "MESSAGE NULL")
                                                                        }
                                                                    }

                                                                    override fun onChildRemoved(p0: DataSnapshot?) {
                                                                    }

                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                    }
                                                                })
                                                    }
                                                } else {
                                                    Log.d("TTT", "ConverNull")
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("TTT", "user_listconver null")
                        }
                    }
                })
    }

    fun setDataChatGroup() {

        //vao id cua conversation ma user do chat
        //vào cuộc trò chuyện
        mDatabase!!.child("conversation").child(converID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                            Log.d("TTT", temp.idConver)
                            //kiểm tra đâu là cuộc trò chuyện mình
                            // load danh sach nguoi = mang
                            var userList = ArrayList<String>(temp!!.listUsers)
                            var avatar = ""
                            avatar = "http://1.gravatar.com/avatar/1771f433d2eed201bd40e6de0c3a74a7?s=1024&d=mm&r=g"
                            // load tin nhan
                            mDatabase!!.child("conversation").child(converID).child("messages")
                                    .addChildEventListener(object : ChildEventListener {
                                        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                        }

                                        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                        }

                                        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                                            if (p0!!.value != null) {

                                                var getMessage: MessageDC = p0!!.getValue(MessageDC::class.java)!!
                                                mDatabase!!.child("users").child(getMessage.idSender).child("avatar")
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError?) {
                                                            }

                                                            override fun onDataChange(p0: DataSnapshot?) {
                                                                var checkType = getMessage.type
                                                                if (getMessage.type == "0") {
                                                                    checkType = "0"
                                                                } else {
                                                                    if (getMessage.idSender == userid) {
                                                                        checkType = "2"
                                                                    } else {
                                                                        checkType = "1"
                                                                    }
                                                                }
                                                                Log.d("ABCC", "VAO ELSE")

                                                                //lấy định dạng ngày mặt định
                                                                val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                                //parse ngày ở firebase sang dụng hh:mm
                                                                val t = df.parse(getMessage.date)
                                                                df.applyPattern("hh:mm")
                                                                var itemx = ChatDataDC(getMessage.id, p0!!.getValue().toString(), checkType, getMessage.content, df.format(t))
                                                                if ((mRecyclerView!!.adapter as ChatDataAdapter).isAdded(itemx))
                                                                    (mRecyclerView!!.adapter as ChatDataAdapter).notifyDataSetChanged()
                                                                else
                                                                    (mRecyclerView!!.adapter as ChatDataAdapter).addItem(itemx)
                                                                mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1)
                                                            }

                                                        })

                                            } else {
                                                currentPage--
                                                Log.d("TTT", "MESSAGE NULL")
                                            }
                                        }

                                        override fun onChildRemoved(p0: DataSnapshot?) {
                                        }

                                        override fun onCancelled(p0: DatabaseError?) {
                                        }
                                    })
                        } else {
                            Log.d("TTT", "ConverNull")
                        }
                    }
                })
    }

    override fun onBackPressed() {
        //seenlistener
        if (group_check == false) {
            mDatabase!!.child("user_listconver").child(userid)
                    .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.getValue() != null) {
                                        Log.d("ABC", "SU KIEN")
                                        var checkExitsConver: Boolean = false
                                        for (snap in p0!!.children) {
                                            //vao id cua conversation ma user do chat
                                            mDatabase!!.child("conversation").child("" + snap!!.value)
                                                    .addValueEventListener(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError?) {
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                            if (p0!!.value != null) {
                                                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                                if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                                        (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {
                                                                    //xu ly them chat trong nay

                                                                    //addseen
                                                                    mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1)
                                                                            .addListenerForSingleValueEvent(
                                                                                    object : ValueEventListener {
                                                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                                                            if (p0!!.value != null) {
                                                                                                for (snapMess in p0!!.children) {
                                                                                                    var kiemTraTonTai = false
                                                                                                    var tempContent = ""
                                                                                                    var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                                                    for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                                                        Log.d("ABC", "User " + tempMess.userSeen!![i])
                                                                                                        if (tempMess.userSeen!![i].equals(userid)) {
                                                                                                            kiemTraTonTai = true
                                                                                                            Log.d("ABC", "REMOVE USER RIGHT")
                                                                                                            break
                                                                                                        }
                                                                                                    }
                                                                                                    if (kiemTraTonTai == false) {
                                                                                                        tempMess.userSeen!!.add(userid)
                                                                                                        Log.d("ABC", "REmove listener")
                                                                                                        mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").child(tempMess.id).setValue(tempMess)
                                                                                                    }

                                                                                                }
                                                                                                mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1).removeEventListener(this)
                                                                                            } else {
                                                                                                mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1).removeEventListener(this)
                                                                                                Log.d("RRR", "NULL MESSAGE LAST")
                                                                                            }
                                                                                        }

                                                                                        override fun onCancelled(p0: DatabaseError?) {
                                                                                        }


                                                                                    })
                                                                }
                                                                mDatabase!!.child("conversation").child("" + snap!!.value).removeEventListener(this)
                                                            } else {
                                                                mDatabase!!.child("conversation").child("" + snap!!.value).removeEventListener(this)

                                                                Log.d("AAA", "DATA Conver NUll")
                                                            }
                                                        }

                                                    })
                                        }
                                        mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                    }
                                }

                            }
                    )
        } else {
            //vao id cua conversation ma user do chat
            mDatabase!!.child("conversation").child(converID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                //xu ly them chat trong nay
                                //addseen
                                mDatabase!!.child("conversation").child(converID).child("messages").limitToLast(1)
                                        .addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                        if (p0!!.value != null) {
                                                            for (snapMess in p0!!.children) {
                                                                var kiemTraTonTai = false
                                                                var tempContent = ""
                                                                var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                    Log.d("ABC", "User " + tempMess.userSeen!![i])
                                                                    if (tempMess.userSeen!![i].equals(userid)) {
                                                                        kiemTraTonTai = true
                                                                        Log.d("ABC", "REMOVE USER RIGHT")
                                                                        break
                                                                    }
                                                                }
                                                                if (kiemTraTonTai == false) {
                                                                    tempMess.userSeen!!.add(userid)
                                                                    Log.d("ABC", "REmove listener")
                                                                    mDatabase!!.child("conversation").child(converID).child("messages").child(tempMess.id).setValue(tempMess)
                                                                }

                                                            }
                                                        }
                                                        mDatabase!!.child("conversation").child(converID).child("messages").limitToLast(1).removeEventListener(this)
                                                    }

                                                    override fun onCancelled(p0: DatabaseError?) {
                                                    }
                                                })
                                mDatabase!!.child("conversation").child(converID).removeEventListener(this)

                            }
                        }
                    })
        }
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        when (id) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}


