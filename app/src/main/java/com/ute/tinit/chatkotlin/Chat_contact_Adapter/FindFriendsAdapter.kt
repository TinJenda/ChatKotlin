package com.ute.tinit.chatkotlin.Chat_contact_Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.DataClass.FindFriendDC
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/7/2017.
 */
class FindFriendsAdapter(private val mContext: Context, private val mArrayList: List<FindFriendDC>) : SelectableAdapter<FindFriendsAdapter.ViewHolder>()
{
    override fun getItemCount(): Int {
        return mArrayList.size
    }

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FindFriendsAdapter.ViewHolder {

        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_findfriend, null)

        val viewHolder = FindFriendsAdapter.ViewHolder(itemLayoutView)

        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: FindFriendsAdapter.ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].name)
        Picasso.with(mContext).load(mArrayList[position].avatar)
                .error(R.drawable.default_avarta)
                .into(viewHolder.userPhoto)
        if(mArrayList[position].status.equals("0")) //đã gửi yêu cầu
        {
            viewHolder.btnCancel.visibility=View.VISIBLE
            viewHolder.btnAddFriend.visibility=View.GONE
            viewHolder.btnAccept.visibility=View.GONE
            viewHolder.btnDeny.visibility=View.GONE
        }
        else
        {
            if(mArrayList[position].status.equals("1"))//được nhận yêu cầu
            {
                viewHolder.btnCancel.visibility=View.GONE
                viewHolder.btnAddFriend.visibility=View.GONE
                viewHolder.btnAccept.visibility=View.VISIBLE
                viewHolder.btnDeny.visibility=View.VISIBLE
            }
            else
            {
                //mArray.status==2 : giờ gửi yêu cầu
                viewHolder.btnCancel.visibility=View.GONE
                viewHolder.btnAddFriend.visibility=View.VISIBLE
                viewHolder.btnAccept.visibility=View.GONE
                viewHolder.btnDeny.visibility=View.GONE
            }
        }
//        if (mArrayList[position].e) {
//            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_online)
//        } else
//            viewHolder.onlineView.setBackgroundResource(R.drawable.bg_offline)
    }


    class ViewHolder
    //private final View selectedOverlay;


    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
            Toast.makeText(v!!.context,"Ajojo",Toast.LENGTH_SHORT).show()
        }

        override fun onLongClick(v: View?): Boolean {
            return true
        }

        var tvName: TextView
        var userPhoto: ImageView
        var btnAccept:Button
        var btnDeny:Button
        var btnAddFriend : Button
        var btnCancel:Button
        init {

            tvName = row.findViewById(R.id.tv_user_name)
            btnAddFriend=row.findViewById(R.id.id_sendrequest_add_friend)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            btnAccept=row.findViewById(R.id.idDongY)
            btnDeny=row.findViewById(R.id.idKhongDongY)
            btnCancel=row.findViewById(R.id.idhuyYeuCau)
            row.setOnClickListener(this)

            row.setOnLongClickListener(this)
        }

    }
}