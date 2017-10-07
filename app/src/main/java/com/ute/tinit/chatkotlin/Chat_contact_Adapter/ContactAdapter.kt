package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ute.tinit.chatkotlin.DataClass.ContactDC
import com.ute.tinit.chatkotlin.R
import com.squareup.picasso.Picasso
/**
 * Created by tin3p on 10/7/2017.
 */
class ContactAdapter(private val mContext: Context, private val mArrayList: List<ContactDC>, private val clickListener: ContactAdapter.ViewHolder.ClickListener) : SelectableAdapter<ContactAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return mArrayList.size
    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                           viewType: Int): ContactAdapter.ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_contact, null)

        val viewHolder = ContactAdapter.ViewHolder(itemLayoutView, clickListener)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ContactAdapter.ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].mName)
        Picasso.with(mContext).load("http://taihinhanhdep.xyz/wp-content/uploads/2015/11/anh-dep-cho-dien-thoai-2.jpg")
                .into(viewHolder.userPhoto);
        if (mArrayList[position].online) {
            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_online)
        } else
            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_offline)
    }


    class ViewHolder
    //private final View selectedOverlay;


    (row: View, private val listener: ContactAdapter.ViewHolder.ClickListener?) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {

        var tvName: TextView
        var userPhoto: ImageView
        val onlineView: View
        var online = false
        init {

            tvName = row.findViewById(R.id.tv_user_name)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            onlineView = row.findViewById(R.id.online_indicator)
            row.setOnClickListener(this)

            row.setOnLongClickListener(this)
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
