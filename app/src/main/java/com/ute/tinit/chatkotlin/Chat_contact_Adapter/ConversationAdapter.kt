package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_chat_active
import com.ute.tinit.chatkotlin.DataClass.ChatDC
import com.ute.tinit.chatkotlin.R
import android.widget.*
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.DataClass.ConversationDC
import com.ute.tinit.chatkotlin.DataClass.MessageDC


/**
 * Created by tin3p on 10/7/2017.
 */
class ConversationAdapter(private val mContext: Context, private val mArrayList: ArrayList<ChatDC>) : SelectableAdapter<ConversationAdapter.ViewHolder>() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun getItemCount(): Int {
        return mArrayList.size
    }


    fun notifyItemDataChange(chat: ChatDC) {
        for (i in 0..mArrayList.size - 1) {
            if (mArrayList[i].idConversation == chat.idConversation) {
                mArrayList[i] = chat
                notifyItemChanged(i)
                break
            }
        }
    }

    fun isContactAdded(chat: ChatDC): Boolean {
        for (c: ChatDC in mArrayList)
            if (chat.idConversation == c.idConversation)
                return true
        return false
    }

    fun addItem(chat: ChatDC) {
        mArrayList.add(chat)
        notifyItemInserted(mArrayList.size)
    }

//    fun removeItem(contactId: String) {
//        for(i in 0..mArrayList.size-1) {
//            if (mArrayList[i].id == contactId) {
//                mArrayList.removeAt(i)
//                notifyItemRemoved(i)
//            }
//        }
//    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ConversationAdapter.ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_conversation, null)

        val viewHolder = ViewHolder(itemLayoutView)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].mName)

        viewHolder.tvTime.setText(mArrayList[position].mTime)
//        viewHolder.userPhoto.setImageResource(mArrayList.get(position).mImage)
        Picasso.with(mContext).load(mArrayList[position].mImage)
                .into(viewHolder.userPhoto);
        if (mArrayList[position].online!!) {
            viewHolder.onlineView.visibility = View.VISIBLE
        } else
            viewHolder.onlineView.visibility = View.GONE

        viewHolder.tvLastChat.setText(mArrayList[position].mLastChat)
        if (mArrayList[position].seen == true) {
            viewHolder.newMess.visibility = View.GONE
        } else {
            viewHolder.newMess.visibility = View.VISIBLE
        }
    }


    inner class ViewHolder(row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            val dialog = Dialog(v!!.context)
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_list_chat)
            // Set dialog title
            dialog.setTitle("")

            // set values for custom dialog components - text, image and button
            val btnXoaTinNhan = dialog.findViewById<Button>(R.id.btnXoaTinNhan)
            val btnThongTin = dialog.findViewById<Button>(R.id.btnThongTin)
            val tv_user_name_chat = dialog.findViewById<TextView>(R.id.tv_user_name_chat)
            tv_user_name_chat.setText("CHỌN")
            btnXoaTinNhan.setOnClickListener {


                mDatabase!!.child("conversation").child(mArrayList[position].idConversation!!).removeValue { databaseError, databaseReference ->
                    mArrayList.removeAt(position)
                    notifyDataSetChanged()
                    Toast.makeText(v.context, "Đã xóa cuộc hội thoại", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            dialog.show()
            return true
        }

        var tvName: TextView
        var tvTime: TextView
        var tvLastChat: TextView
        var userPhoto: ImageView
        var online = false
        val onlineView: View
        var newMess: TextView

        init {
            mDatabase = FirebaseDatabase.getInstance().getReference()
            mAuth = FirebaseAuth.getInstance()
            userid = mAuth!!.uid!!
            tvName = row.findViewById(R.id.tv_user_name)
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = row.findViewById(R.id.tv_time)
            tvLastChat = row.findViewById(R.id.tv_last_chat)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            onlineView = row.findViewById(R.id.online_indicator)
            newMess = row.findViewById(R.id.newMessage)
            row.setOnLongClickListener(this)
            row.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            //tra ve list conver user do tham gia

     //          Toast.makeText(v.context,"id "+mArrayList[position].idConversation!!.toString(),Toast.LENGTH_SHORT).show()
            mDatabase!!.child("conversation").child(mArrayList[position].idConversation!!.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }
                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                var tempConver: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                if (tempConver!!.isGroup == false) {
                                    if ((tempConver!!.listUsers!!.get(0) == userid || tempConver!!.listUsers!!.get(1) == userid)) {
//                                        //truong hop userid o vi tri 1 ta lay then o vi tri 2
                                        var userFR = ""
//                                        //get list userssss
                                        if (tempConver!!.listUsers!!.get(0) == userid) {
                                            userFR = tempConver!!.listUsers!!.get(1)
                                        } else {
                                            userFR = tempConver!!.listUsers!!.get(0)
                                        }
                                        var intent = Intent(v.context, activity_chat_active::class.java)
                                       intent.putExtra("userfriend", userFR)
                                        intent.putExtra("conversation", mArrayList[position].idConversation!!.toString())
                                        intent.putExtra("group_check", false)
                                        intent.putExtra("isfriend",mArrayList[position].mName)
                                    //   Log.d("RRR", "userfr " + userFR)
                                        Log.d("RRR", "userfr " + position)
                                        startActivity(v.context, intent, null)
                                    }
                                } else {
                                    var intent = Intent(v.context, activity_chat_active::class.java)
                                    intent.putExtra("conversation", mArrayList[position].idConversation!!.toString())
                                    intent.putExtra("group_check", true)
                                    intent.putExtra("nameconver",mArrayList[position].mName)
                                    startActivity(v.context, intent, null)
                                }
                            } else {
                                Log.d("RRR", "MESS NULL")
                            }
                            mDatabase!!.child("conversation").child(mArrayList[position].idConversation!!.toString()).removeEventListener(this)
                        }
                    })
        }

    }


}

