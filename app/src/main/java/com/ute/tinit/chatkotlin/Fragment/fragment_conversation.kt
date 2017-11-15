package com.ute.tinit.chatkotlin.Fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.Activity.activity_chat_active
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ConversationAdapter
import com.ute.tinit.chatkotlin.DataClass.ChatDC
import com.ute.tinit.chatkotlin.DataClass.ConversationDC
import com.ute.tinit.chatkotlin.DataClass.MessageDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.R
import java.util.ArrayList

class fragment_conversation : Fragment(), ConversationAdapter.ViewHolder.ClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ConversationAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    var userFR=""
    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_conversation, null, false)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mAdapter = ConversationAdapter(context, setData(), this@fragment_conversation)
        mRecyclerView!!.adapter = mAdapter

        return view
    }

    fun setData(): ArrayList<ChatDC> {
        val data = ArrayList<ChatDC>()

        mDatabase!!.child("user_listconver").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            for (snap in p0.children) {
                                Log.d("RRR", snap.value!!.toString())

                                mDatabase!!.child("conversation").child(snap.value!!.toString())
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.value != null) {

                                                    var tempConver: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                    if (tempConver!!.isGroup == false && (tempConver!!.listUsers!!.get(0) == userid || tempConver!!.listUsers!!.get(1) == userid)) {
                                                        //truong hop userid o vi tri 1 ta lay then o vi tri 2
                                                        if (tempConver!!.listUsers!!.get(0) == userid) {
                                                            userFR = tempConver!!.listUsers!!.get(1)
                                                        } else {
                                                            userFR = tempConver!!.listUsers!!.get(0)
                                                        }
                                                        mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1)
                                                                .addValueEventListener(object : ValueEventListener {
                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                    }

                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                        if (p0!!.value != null) {
                                                                            for (snapMess in p0!!.children) {
                                                                                var tempContent = ""
                                                                                var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                                tempContent = tempMess.content!!

                                                                                mDatabase!!.child("users").child(userFR)
                                                                                        .addValueEventListener(object : ValueEventListener {
                                                                                            override fun onCancelled(p0: DatabaseError?) {
                                                                                            }

                                                                                            override fun onDataChange(p0: DataSnapshot?) {
                                                                                                if (p0!!.value != null) {
                                                                                                    var tempUser: UserDC = p0!!.getValue(UserDC::class.java)!!
                                                                                                    var isonline: Boolean = false
                                                                                                    if (tempUser.online == 1) {
                                                                                                        isonline = true
                                                                                                    } else {
                                                                                                        isonline = false
                                                                                                    }
                                                                                                    Log.d("RRR", "" + tempUser.name + "\n " + tempContent + "\n " + tempMess.date + "\n " + tempUser.avatar + "\n " + isonline)
                                                                                                    var chat: ChatDC = ChatDC(snap!!.value.toString(), tempUser.name, tempMess.content, tempMess.date, tempUser.avatar, isonline)
                                                                                                    // kiem tra contact da co trong list
                                                                                                    if ((mRecyclerView!!.adapter as ConversationAdapter).isContactAdded(chat))
                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chat)
                                                                                                    else
                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).addItem(chat)
                                                                                                } else {
                                                                                                    Log.d("RRR", "USER KO TON TAI")
                                                                                                }
                                                                                            }

                                                                                        })
                                                                            }
                                                                        } else {
                                                                            Log.d("RRR", "NULL MESSAGE LAST")
                                                                        }
                                                                    }

                                                                })
                                                    }


                                                } else {
                                                    Log.d("RRR", "MESS NULL")
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("RRR", "NOT HAVE CONVERSATION")
                        }
                    }

                })

        return data
    }

    override fun onItemClicked(position: Int) {
        var intent = Intent(context, activity_chat_active::class.java)
        intent.putExtra("userfriend",userFR)
        startActivity(intent)
//        Toast.makeText(context, "Hello...item " + position, Toast.LENGTH_LONG).show()
    }

    override fun onItemLongClicked(position: Int): Boolean {
        val dialog = Dialog(context)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_list_chat)
        // Set dialog title
        dialog.setTitle("")

        // set values for custom dialog components - text, image and button
        val btnXoaTinNhan = dialog.findViewById<Button>(R.id.btnXoaTinNhan)
        val btnThongTin = dialog.findViewById<Button>(R.id.btnThongTin)
        val tv_user_name_chat = dialog.findViewById<TextView>(R.id.tv_user_name_chat)
        tv_user_name_chat.setText("SETTEXTTAIDAY")
        btnXoaTinNhan.setOnClickListener {
            Toast.makeText(context, "Hello xoa tin nhan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnThongTin.setOnClickListener {
            Toast.makeText(context, "Hello thong tin ", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

}
