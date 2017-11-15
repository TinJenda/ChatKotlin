package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
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
import com.ute.tinit.chatkotlin.DataClass.MessageDC


/**
 * Created by tin3p on 10/7/2017.
 */
class ConversationAdapter(private val mContext: Context, private val mArrayList: ArrayList<ChatDC>, private val clickListener: ViewHolder.ClickListener) : SelectableAdapter<ConversationAdapter.ViewHolder>() {
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

        val viewHolder = ViewHolder(itemLayoutView, clickListener)

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

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid= mAuth!!.uid!!

        mDatabase!!.child("conversation").child(mArrayList[position].idConversation).child("messages").limitToLast(1)
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if(p0!!.value!=null)
                        {
                            for(snap in p0.children)
                            {
                                var tempMess:MessageDC=snap.getValue(MessageDC::class.java)!!

                            }
                        }
                    }

                })
    }


    class ViewHolder(row: View, private val listener: ClickListener?) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {

        var tvName: TextView
        var tvTime: TextView
        var tvLastChat: TextView
        var userPhoto: ImageView
        var online = false
        val onlineView: View
        var newMess: TextView

        init {
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
            listener?.onItemClicked(adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            if (listener != null) {
                return listener.onItemLongClicked(adapterPosition)
            }
            return false
        }

        interface ClickListener {
            fun onItemClicked(position: Int)

            fun onItemLongClicked(position: Int): Boolean

            fun onCreateOptionsMenu(menu: Menu): Boolean
        }

    }
}
