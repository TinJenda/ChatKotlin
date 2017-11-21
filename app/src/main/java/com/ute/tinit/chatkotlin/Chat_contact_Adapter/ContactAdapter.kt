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
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.ContactAdapter.ViewHolder
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/7/2017.
 */
class ContactAdapter(private val mContext: Context, private val mArrayList: ArrayList<AdapterContact>) : SelectableAdapter<ViewHolder>()
{

    class AdapterContact (var id:String, var mName:String, var mImage:String, var online:Boolean)

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun isContactAdded(contact: AdapterContact): Boolean {
        for (c: AdapterContact in mArrayList)
            if (contact.id == c.id)
                return true
        return false
    }

    fun notifyItemDataChange(contact: AdapterContact) {
        for (i in 0..mArrayList.size-1) {
            if (mArrayList[i].id == contact.id) {
                mArrayList[i] = contact
                notifyItemChanged(i)
                break
            }
        }
    }


    fun addItem(contact: AdapterContact) {
        mArrayList.add(contact)
        notifyItemInserted(mArrayList.size)
    }

    fun removeItem(contactId: String) {
        for(i in 0..mArrayList.size-1) {
            if (mArrayList[i].id == contactId) {
                mArrayList.removeAt(i)
                notifyItemRemoved(i)
                break
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
                R.layout.list_item_contact, null)

        val viewHolder = ViewHolder(itemLayoutView)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].mName)
        Picasso.with(mContext).load(mArrayList[position].mImage)
                .error(R.drawable.default_avarta)
                .into(viewHolder.userPhoto)
        if (mArrayList[position].online) {
            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_online)
        } else
            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_offline)
    }


    inner class ViewHolder
    //private final View selectedOverlay;
    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
            var possitionItem= getAdapterPosition()
            Toast.makeText(v!!.context,"hello "+tvName.text.toString() +"id "+mArrayList[possitionItem].id,Toast.LENGTH_SHORT).show()
            var intent=Intent(v.context,activity_chat_active::class.java)
            intent.putExtra("userfriend",mArrayList[possitionItem].id)
            ContextCompat.startActivity(v.context,intent,null)
        }
        var tvName: TextView
        var userPhoto: ImageView
        val onlineView: View
        init {

            tvName = row.findViewById(R.id.tv_user_name)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            onlineView = row.findViewById(R.id.online_indicator)
            row.setOnLongClickListener(this)
            row.setOnClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            return true
        }
    }
}
