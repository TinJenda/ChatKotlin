package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_friend_profile
import com.ute.tinit.chatkotlin.Activity.activity_profile
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.FindFriendsAdapter.ViewHolder
import com.ute.tinit.chatkotlin.DataClass.FindFriendDC
import com.ute.tinit.chatkotlin.DataClass.RequestFriendDC
import com.ute.tinit.chatkotlin.DataClass.UserDC
import com.ute.tinit.chatkotlin.MainActivity
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/7/2017.
 */
class FindFriendsAdapter(private val mContext: Context, private val mArrayList: ArrayList<FindFriendDC>) : SelectableAdapter<ViewHolder>() {
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""
    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun isAdded(findfriend: FindFriendDC): Boolean {
        for (c: FindFriendDC in mArrayList)
            if (findfriend.userID == c.userID)
                return true
        return false;
    }

    fun notifyChange(findfriend: FindFriendDC) {
        for (i in 0..mArrayList.size - 1) {
            if (mArrayList[i].userID == findfriend.userID)
            {
                mArrayList[i] = findfriend
                notifyItemChanged(i)
                break
            }
        }
    }

    fun addItem(findfriend: FindFriendDC) {
        mArrayList.add(findfriend)
        notifyItemInserted(mArrayList.size)
    }
    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_findfriend, null)

        val viewHolder = ViewHolder(itemLayoutView)

        return viewHolder
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].name)
        Picasso.with(mContext).load(mArrayList[position].avatar)
                .error(R.drawable.default_avarta)
                .into(viewHolder.userPhoto)

    }


     inner class ViewHolder
    //private final View selectedOverlay;
    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
        // Toast.makeText(v!!.context, "", Toast.LENGTH_SHORT).show()
            var possitionItem= getAdapterPosition()
            mAuth = FirebaseAuth.getInstance()
            userid = mAuth!!.uid!!
            if(userid==mArrayList[possitionItem].userID)
            {
                var intent=Intent(v!!.context,activity_profile::class.java)
                ContextCompat.startActivity(v.context,intent,null)
            }
            else
            {
                var intent=Intent(v!!.context,activity_friend_profile::class.java)
                intent.putExtra("userfriend",mArrayList[possitionItem].userID)
                ContextCompat.startActivity(v.context,intent,null)
            }

        }

        override fun onLongClick(v: View?): Boolean {
            return true
        }

        var tvName: TextView
        var userPhoto: ImageView

        init {
            tvName = row.findViewById(R.id.tv_user_name)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            row.setOnClickListener(this)
            row.setOnLongClickListener(this)
        }

    }
}