package com.ute.tinit.chatkotlin.Chat_Activity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.DataClass.ChatDC
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/7/2017.
 */
class ChatAdapter(private val mContext: Context, private val mArrayList: List<ChatDC>, private val clickListener: ViewHolder.ClickListener) : SelectableAdapter<ChatAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return  mArrayList.size
    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                           viewType: Int): ChatAdapter.ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_chat, null)

        val viewHolder = ViewHolder(itemLayoutView, clickListener)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].mName)
        if (isSelected(position)) {
            viewHolder.checked.isChecked = true
            viewHolder.checked.visibility = View.VISIBLE
        } else {
            viewHolder.checked.isChecked = false
            viewHolder.checked.visibility = View.GONE
        }
        viewHolder.tvTime.setText(mArrayList[position].mTime)
//        viewHolder.userPhoto.setImageResource(mArrayList.get(position).mImage)
        Picasso.with(mContext).load("http://taihinhanhdep.xyz/wp-content/uploads/2015/11/anh-dep-cho-dien-thoai-2.jpg")
                .into(viewHolder.userPhoto);
        if (mArrayList[position].online) {
            viewHolder.onlineView.visibility = View.VISIBLE
        } else
            viewHolder.onlineView.visibility = View.GONE

        viewHolder.tvLastChat.setText(mArrayList[position].mLastChat)
    }


    class ViewHolder(row: View, private val listener: ClickListener?) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {

        var tvName: TextView
        var tvTime: TextView
        var tvLastChat: TextView
        var userPhoto: ImageView
        var online = false
        val onlineView: View
        var checked: CheckBox

        init {

            tvName = row.findViewById(R.id.tv_user_name)
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = row.findViewById(R.id.tv_time)
            tvLastChat = row.findViewById(R.id.tv_last_chat)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            onlineView = row.findViewById(R.id.online_indicator)
            checked = row.findViewById(R.id.chk_list)

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
