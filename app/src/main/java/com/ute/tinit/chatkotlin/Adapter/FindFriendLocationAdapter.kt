package com.ute.tinit.chatkotlin.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Activity.activity_friend_profile
import com.ute.tinit.chatkotlin.Chat_contact_Adapter.SelectableAdapter
import com.ute.tinit.chatkotlin.DataClass.FindFriendLocationDC
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 12/1/2017.
 */
class FindFriendLocationAdapter(private val mContext: Context, private val mArrayList: ArrayList<FindFriendLocationDC>) : SelectableAdapter<FindFriendLocationAdapter.ViewHolder>()
{

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun isContactAdded(contact: FindFriendLocationDC): Boolean {
        for (c: FindFriendLocationDC in mArrayList)
            if (contact.userID == c.userID)
                return true
        return false
    }

    fun notifyItemDataChange(contact: FindFriendLocationDC) {
        for (i in 0..mArrayList.size-1) {
            if (mArrayList[i].userID == contact.userID) {
                mArrayList[i] = contact
                notifyItemChanged(i)
                break
            }
        }
    }


    fun addItem(contact: FindFriendLocationDC) {
        mArrayList.add(contact)
        notifyItemInserted(mArrayList.size)
    }

    fun removeItem(contactId: String) {
        for(i in 0..mArrayList.size-1) {
            if (mArrayList[i].userID == contactId) {
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
                R.layout.list_item_findfriendlocation, null)

        val viewHolder = ViewHolder(itemLayoutView)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.tvName.setText(mArrayList[position].name)
        Picasso.with(mContext).load(mArrayList[position].avatar)
                .error(R.drawable.default_avarta)
                .into(viewHolder.userPhoto)
        if(mArrayList[position].sex.equals("Khác"))
        {
            viewHolder.img_sex.setImageResource(R.drawable.male_female_icon)
        }
        else
        {
            if(mArrayList[position].sex.equals("Nam"))
            {
                viewHolder.img_sex.setImageResource(R.drawable.ic_male)
            }
            else
            {
                viewHolder.img_sex.setImageResource(R.drawable.ic_female)

            }
        }

        viewHolder.tv_age.setText(mArrayList[position].age)
        viewHolder.tv_khoangcach.setText(mArrayList[position].distance)

    }


    inner class ViewHolder
    //private final View selectedOverlay;
    (row: View) : RecyclerView.ViewHolder(row), View.OnClickListener, View.OnLongClickListener {
        override fun onClick(v: View?) {
            var possitionItem= getAdapterPosition()
            var intent= Intent(v!!.context, activity_friend_profile::class.java)
            intent.putExtra("userfriend",mArrayList[possitionItem].userID)
            ContextCompat.startActivity(v.context, intent, null)
        }
        var tvName: TextView
        var userPhoto: ImageView
        var img_sex:ImageView
        var tv_age:TextView
        var tv_khoangcach:TextView
        init {

            tvName = row.findViewById(R.id.tv_user_name)
            userPhoto = row.findViewById(R.id.iv_user_photo)
            img_sex=row.findViewById(R.id.img_sex)
            tv_age = row.findViewById(R.id.tv_age)
            tv_khoangcach=row.findViewById(R.id.tv_khoangcach)
            row.setOnLongClickListener(this)
            row.setOnClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            return true
        }
    }}
