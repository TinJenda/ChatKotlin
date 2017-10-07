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


/**
 * Created by tin3p on 10/7/2017.
 */
class ChatAdapter(private val mContext: Context, private val mArrayList: List<ChatDC>, private val clickListener: ViewHolder.ClickListener) : SelectableAdapter<ChatAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return mArrayList.size
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

        init {
            tvName = row.findViewById(R.id.tv_user_name)
            //selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            tvTime = row.findViewById(R.id.tv_time)
            tvLastChat = row.findViewById(R.id.tv_last_chat)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            onlineView = row.findViewById(R.id.online_indicator)

            row.setOnClickListener {
                Toast.makeText(row.context, tvName.text, Toast.LENGTH_SHORT).show()
                var intent = Intent(row.context, activity_chat_active::class.java)
                startActivity(row.context, intent, Bundle())
            }
            row.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    val dialog = Dialog(row.context)
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.dialog_list_chat)
                    // Set dialog title
                    dialog.setTitle("")

                    // set values for custom dialog components - text, image and button
                    val btnXoaTinNhan = dialog.findViewById<Button>(R.id.btnXoaTinNhan)
                    val btnThongTin = dialog.findViewById<Button>(R.id.btnThongTin)
                    val tv_user_name_chat = dialog.findViewById<TextView>(R.id.tv_user_name_chat)
                    tv_user_name_chat.setText(tvName.text)
                    btnXoaTinNhan.setOnClickListener {
                        Toast.makeText(row.context, "Hello xoa tin nhan", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    btnThongTin.setOnClickListener {
                        Toast.makeText(row.context, "Hello thong tin " + tvName.text, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    dialog.show()
                    return true
                }

            })
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
