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

class fragment_conversation : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ConversationAdapter? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    var listUser = arrayListOf<String>()

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
        mAdapter = ConversationAdapter(context, setData())
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
                            listUser.clear()
                            for (snapConver in p0.children) {
                                Log.d("RRR", snapConver.value!!.toString())
                                //tra ve list conver user do tham gia
                                mDatabase!!.child("conversation").child(snapConver.value!!.toString())
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.value != null) {

                                                    var tempConver: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                    if (tempConver!!.isGroup == false && (tempConver!!.listUsers!!.get(0) == userid || tempConver!!.listUsers!!.get(1) == userid)) {
                                                        //truong hop userid o vi tri 1 ta lay then o vi tri 2
                                                        var checkExist = false
                                                        var userFR = ""
                                                        //get list userssss
                                                        if (tempConver!!.listUsers!!.get(0) == userid) {
                                                            userFR = tempConver!!.listUsers!!.get(1)
                                                            if (listUser != null) {
                                                                for (i in 0..listUser.size - 1) {
                                                                    if (listUser[i] == userFR) {
                                                                        checkExist = true
                                                                        break
                                                                    }
                                                                }
                                                                if (checkExist == false) {
                                                                    listUser.add(userFR)
                                                                }
                                                            } else {
                                                                listUser.add(userFR)
                                                            }
                                                        } else {
                                                            userFR = tempConver!!.listUsers!!.get(0)
                                                            if (listUser != null) {
                                                                for (i in 0..listUser.size - 1) {
                                                                    if (listUser[i] == userFR) {
                                                                        checkExist = true
                                                                        break
                                                                    }
                                                                }
                                                                if (checkExist == false) {
                                                                    listUser.add(userFR)
                                                                }
                                                            } else {
                                                                listUser.add(userFR)
                                                            }
                                                        }
                                                        mDatabase!!.child("conversation").child(snapConver!!.value.toString()).child("messages").limitToLast(1)
                                                                .addValueEventListener(object : ValueEventListener {
                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                    }

                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                        if (p0!!.value != null) {
                                                                            for (snapMess in p0!!.children) {
                                                                                var tempContent = ""
                                                                                var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                                var lastMess=""
                                                                                tempContent = tempMess.content!!
                                                                                var listFR = arrayListOf<String>()
                                                                                //list friend check phai nguoi la ko
                                                                                mDatabase!!.child("friends").child(userid)
                                                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                                                            override fun onCancelled(p0: DatabaseError?) {
                                                                                            }

                                                                                            override fun onDataChange(p0: DataSnapshot?) {
                                                                                                if (p0!!.value != null) {
                                                                                                    for (snap in p0.children) {
                                                                                                        listFR.add(snap.value.toString())
                                                                                                        Log.d("nguoila", "1 " + snap.value.toString())
                                                                                                    }


                                                                                                } else {
                                                                                                    Log.d("nguoila", "null friend")
                                                                                                }
                                                                                            }

                                                                                        })
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
                                                                                                    var seen: Boolean = false
                                                                                                    //xu ly thong bao tin nhan moi o day
                                                                                                    for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                                                        Log.d("RRR", "userSeen = " + tempMess.userSeen!![i])
                                                                                                        if (tempMess.userSeen!![i] == userid) {
                                                                                                            seen = true
                                                                                                            break
                                                                                                        }
                                                                                                    }

                                                                                                    if (seen == true) {
                                                                                                        Log.d("RRR", "Seen = true")
                                                                                                    } else {
                                                                                                        Log.d("RRR", "Seen = false")
                                                                                                    }

                                                                                                    var nguoila = true
                                                                                                    for (user in listFR) {
                                                                                                        if (user == userFR) {
                                                                                                            Log.d("nguoila", "xet nguoi la dung")
                                                                                                            nguoila = false
                                                                                                            break
                                                                                                        }
                                                                                                    }
                                                                                                    if (nguoila == true) {
                                                                                                        mDatabase!!.child("conversation").child(snapConver!!.value.toString()).child("messages").limitToFirst(1)
                                                                                                                .addValueEventListener(object : ValueEventListener {
                                                                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                                                                    }

                                                                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                                                                        if (p0!!.value != null) {
                                                                                                                            for (snapmess in p0.children) {
                                                                                                                                var firstMess: MessageDC = snapmess.getValue(MessageDC::class.java)!!
                                                                                                                                Log.d("tinnhandau", "x = " + tempMess.content)
                                                                                                                                Log.d("tinnhandau", "x = " + tempMess!!.idSender)

                                                                                                                                if(tempMess.type=="3"||tempMess.type=="4") {
                                                                                                                                    lastMess="[HÌNH ẢNH]"
                                                                                                                                }
                                                                                                                                else
                                                                                                                                {
                                                                                                                                    lastMess=""+tempMess.content
                                                                                                                                }
                                                                                                                                if (firstMess!!.idSender != userid) {
                                                                                                                                    Log.d("nguoila", "nguoi la dung")
                                                                                                                                    var chat: ChatDC = ChatDC(snapConver!!.value.toString(), "Người lạ", lastMess, tempMess.date, tempUser.avatar, isonline, seen)
                                                                                                                                    // kiem tra contact da co trong list
                                                                                                                                    if ((mRecyclerView!!.adapter as ConversationAdapter).isContactAdded(chat))
                                                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chat)
                                                                                                                                    else {
                                                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).addItem(chat)
                                                                                                                                    }
                                                                                                                                    (mRecyclerView!!.adapter as ConversationAdapter).notifyDataSetChanged()
                                                                                                                                } else {
                                                                                                                                    Log.d("nguoila", "nguoi la dung")
                                                                                                                                    var chat: ChatDC = ChatDC(snapConver!!.value.toString(), tempUser.name, lastMess, tempMess.date, tempUser.avatar, isonline, seen)
                                                                                                                                    // kiem tra contact da co trong list
                                                                                                                                    if ((mRecyclerView!!.adapter as ConversationAdapter).isContactAdded(chat))
                                                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chat)
                                                                                                                                    else {
                                                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).addItem(chat)
                                                                                                                                    }
                                                                                                                                    (mRecyclerView!!.adapter as ConversationAdapter).notifyDataSetChanged()
                                                                                                                                }
                                                                                                                            }

                                                                                                                        } else {
                                                                                                                            Log.d("tinnhandau", "x = null")

                                                                                                                        }
                                                                                                                    }

                                                                                                                })
                                                                                                    } else {
                                                                                                        Log.d("nguoila", "nguoi la sai")

                                                                                                        if(tempMess.type=="3"||tempMess.type=="4") {
                                                                                                            lastMess="[HÌNH ẢNH]"
                                                                                                        }
                                                                                                        else
                                                                                                        {
                                                                                                            lastMess=""+tempMess.content
                                                                                                        }
                                                                                                        var chat: ChatDC = ChatDC(snapConver!!.value.toString(), tempUser.name, lastMess, tempMess.date, tempUser.avatar, isonline, seen)
                                                                                                        // kiem tra contact da co trong list
                                                                                                        if ((mRecyclerView!!.adapter as ConversationAdapter).isContactAdded(chat))
                                                                                                            (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chat)
                                                                                                        else {
                                                                                                            (mRecyclerView!!.adapter as ConversationAdapter).addItem(chat)
                                                                                                        }
                                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).notifyDataSetChanged()
                                                                                                    }

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
                                                    } else { //truong hop day chinh la chat nhom
                                                        if (tempConver!!.isGroup == true) {
                                                            mDatabase!!.child("conversation").child(snapConver!!.value.toString()).child("messages").limitToLast(1)
                                                                    .addValueEventListener(object : ValueEventListener {
                                                                        override fun onCancelled(p0: DatabaseError?) {
                                                                        }

                                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                                            if (p0!!.value != null) {
                                                                                for (snapMess in p0!!.children) {
                                                                                    var tempContent = ""
                                                                                    var lastMess_gr=""
                                                                                    var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                                    if(tempMess.type=="3"||tempMess.type=="4") {
                                                                                        lastMess_gr="[HÌNH ẢNH]"
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        lastMess_gr=""+tempMess.content
                                                                                    }
                                                                                    tempContent = tempMess.content!!
                                                                                    var isonline: Boolean = false
                                                                                    var seen: Boolean = false
                                                                                    //xu ly thong bao tin nhan moi o day
                                                                                    for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                                        Log.d("RRR", "userChatNhom = " + tempMess.userSeen!![i])
                                                                                        if (tempMess.userSeen!![i] == userid) {
                                                                                            seen = true
                                                                                            break
                                                                                        }
                                                                                    }
                                                                                    var group_image = "https://firebasestorage.googleapis.com/v0/b/chatkotlin-tinjenda.appspot.com/o/group.png?alt=media&token=2fe5173a-e3d6-45c4-8a93-70af7200d075"
                                                                                    var name = ""
                                                                                    var count_online = 1
                                                                                    for (i in 0..tempConver!!.listUsers!!.size - 1) {
                                                                                        mDatabase!!.child("users").child(tempConver.listUsers!![i])
                                                                                                .addValueEventListener(object : ValueEventListener {
                                                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                                                    }

                                                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                                                        if (p0!!.value != null) {
                                                                                                            var tempUser: UserDC = (p0!!.getValue(UserDC::class.java))!!
                                                                                                            if (tempUser.online == 1) {
                                                                                                                count_online++
                                                                                                            } else {
                                                                                                                count_online--
                                                                                                            }
                                                                                                            if (i == tempConver!!.listUsers!!.size - 1) {
                                                                                                                name += tempUser.name.toString() + " "
                                                                                                                Log.d("Name", "Name " + name)
                                                                                                            } else {
                                                                                                                name += tempUser.name.toString() + ", "
                                                                                                            }
                                                                                                            // mDatabase!!.child("conversation").child(snapConver!!.value.toString()).child("conversationName").setValue(name)
                                                                                                            var chatx: ChatDC = ChatDC(snapConver!!.value.toString(), "(" + count_online + "/" + (tempConver.listUsers!!.size+1) + ") " + name, lastMess_gr, tempMess.date, group_image, isonline, seen)
                                                                                                            (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chatx)
                                                                                                        }
                                                                                                    }

                                                                                                })
                                                                                    }
                                                                                    var chat: ChatDC = ChatDC(snapConver!!.value.toString(), "(" + count_online + "/" + tempConver.listUsers!!.size + ") " + name, lastMess_gr, tempMess.date, group_image, isonline, seen)
                                                                                    // kiem tra contact da co trong list
                                                                                    if ((mRecyclerView!!.adapter as ConversationAdapter).isContactAdded(chat))
                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).notifyItemDataChange(chat)
                                                                                    else {
                                                                                        (mRecyclerView!!.adapter as ConversationAdapter).addItem(chat)
                                                                                    }
                                                                                    (mRecyclerView!!.adapter as ConversationAdapter).notifyDataSetChanged()


                                                                                }
                                                                            } else {
                                                                                Log.d("RRR", "NULL MESSAGE LAST")
                                                                            }
                                                                        }

                                                                    })
                                                        }
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


}
