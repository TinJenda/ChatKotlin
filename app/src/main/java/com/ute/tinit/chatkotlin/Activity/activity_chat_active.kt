package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.EditText
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
import java.util.ArrayList

class activity_chat_active : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatDataAdapter? = null
    private var text: EditText? = null
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    var userid = ""
    var userFR = ""
    var nameUser: String = ""
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
        userFR = intent.getStringExtra("userfriend")
        loadDATA()
        textEmply()
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

    fun textEmply() {
        btn_send.isEnabled = false
        btn_send.setImageResource(R.drawable.ic_send_disable)
        et_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("AAA", "afterTextChagne " + et_message.text)
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    btn_send.isEnabled = false
                    btn_send.setImageResource(R.drawable.ic_send_disable)
                } else {
                    btn_send.isEnabled = true
                    btn_send.setImageResource(R.drawable.ic_send)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("AAA", "beforeTextChanged " + et_message.text)
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    btn_send.isEnabled = false
                    btn_send.setImageResource(R.drawable.ic_send_disable)
                } else {
                    btn_send.isEnabled = true
                    //btn_send.visibility=View.VISIBLE
                    btn_send.setImageResource(R.drawable.ic_send)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun loadDATA() {
        Log.d("userFR", "user " + userFR)
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = ChatDataAdapter(this@activity_chat_active, setData() as MutableList<ChatDataDC>)
        mRecyclerView!!.adapter = mAdapter
        mRecyclerView!!.postDelayed({ mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1) }, 500)

        text = findViewById(R.id.et_message)
        text!!.setOnClickListener { mRecyclerView!!.postDelayed({ mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1) }, 400) }

        mDatabase!!.child("users").child(userFR)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            var getFriend: UserDC = p0.getValue(UserDC::class.java)!!
                            tv_nameuser.setText("" + getFriend.name!!)
                        }
                    }

                })

        btn_send.setOnClickListener {
            if (!text!!.text.equals("") || !text!!.text.equals(null)) {
                mDatabase!!.child("user_listconver").child(userid)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                            }
                            override fun onDataChange(p0: DataSnapshot?) {
                                if (p0!!.getValue() != null) {
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
                                                                checkExitsConver = true
                                                                //them chat
                                                                Log.d("TTT","THEM CHAT")
                                                            }
                                                        }
                                                    }

                                                })
                                        if (checkExitsConver == true) {
                                            break
                                        }
                                    }

                                    if(checkExitsConver==false)
                                    {
                                        //tao cuoc tro chuyen moi tai day
                                        var tempListUser= arrayListOf<String>(userid,userFR)
                                        var conver=ConversationDC("",tempListUser,false)
                                        var myRef = mDatabase!!.child("conversation").push()
                                        myRef.setValue(conver).addOnCompleteListener {
                                            mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                            mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                                            mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                            myRef.removeEventListener(this)
                                        }
                                                //them chat
                                        Log.d("TTT","THEM CHAT checkExitsConver==false")
                                    }
                                } else {
                                    Log.d("TTT", "DATA NULL")
                                    //ko ton tai cai nao cung tao moi tai day
                                    //tao cuoc tro chuyen moi tai day
                                    var tempListUser= arrayListOf<String>(userid,userFR)
                                    var conver=ConversationDC("",tempListUser,false)
                                    var myRef = mDatabase!!.child("conversation").push()
                                    myRef.setValue(conver).addOnCompleteListener {
                                        mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                        mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                                        mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                    }
                                    //them chat
                                    Log.d("TTT","THEM CHAT DATA BAN DAU NULL")
                                }
                            }

                        })
//                val data = ArrayList<ChatDataDC>()
//                val item = ChatDataDC("2", text!!.text.toString(), "6:00")
//                data.add(item)
//                mAdapter!!.addItem(data)
//                mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1)
//                text!!.setText("")
            }
        }
    }


    fun setData(): List<ChatDataDC> {
        val data = ArrayList<ChatDataDC>()
        val text = arrayOf("15 September", "Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?", "Yes! Coffe maybe?",
                "Great idea! You can come 9:00 pm? :)))", "Ok!", "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, " +
                ":`(", "16 September", "Yes! Coffe maybe?")
        val time = arrayOf("", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm", "", "5:50am")
        val type = arrayOf("0", "2", "1", "1", "2", "1", "2", "2", "2", "1", "0", "1")

        for (i in text.indices) {
            val item = ChatDataDC(type[i], text[i], time[i])
            data.add(item)
        }
        return data
    }

    override fun onBackPressed() {
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
