package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_chat_active
import com.ute.tinit.chatkotlin.Activity.activity_friend_profile
import com.ute.tinit.chatkotlin.DataClass.Contact2DC
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/7/2017.
 */
class RequestFriendAdapter(private val mContext: Context, private val mArrayList: ArrayList<Contact2DC>) : SelectableAdapter<RequestFriendAdapter.ViewHolder>()
{

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun isContactAdded(contact: Contact2DC): Boolean {
        for (c: Contact2DC in mArrayList)
            if (contact.idUser == c.idUser)
                return true
        return false
    }

    fun notifyItemDataChange(contact: Contact2DC) {
        for (i in 0..mArrayList.size-1) {
            if (mArrayList[i].idUser == contact.idUser) {
                mArrayList[i] = contact
                notifyItemChanged(i)
                break
            }
        }
    }


    fun addItem(contact: Contact2DC) {
        mArrayList.add(contact)
        notifyItemInserted(mArrayList.size)
    }

    fun removeItem(contactId: String) {
        for(i in 0..mArrayList.size-1) {
            if (mArrayList[i].idUser == contactId) {
                mArrayList.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }

    fun clear() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_friend_request, null)

        val viewHolder = ViewHolder(itemLayoutView)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].mName)
        Picasso.with(mContext).load(mArrayList[position].mImage)
                .error(R.drawable.default_avarta)
                .into(viewHolder.userPhoto)
    }


    inner class ViewHolder
    //private final View selectedOverlay;
    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
            var possitionItem= getAdapterPosition()
            Toast.makeText(v!!.context, "hello " + tvName.text.toString() + "id " + mArrayList[possitionItem].idUser, Toast.LENGTH_SHORT).show()
            var intent= Intent(v.context, activity_friend_profile::class.java)
            intent.putExtra("userfriend",mArrayList[possitionItem].idUser)
            ContextCompat.startActivity(v.context, intent, null)
        }
        var tvName: TextView
        var userPhoto: ImageView
        init {

            tvName = row.findViewById(R.id.tv_user_name)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            row.setOnLongClickListener(this)
            row.setOnClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            return true
        }
    }
}