package com.chatkotlin.tintt.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.DataClass.SelectFriendsDC
import com.ute.tinit.chatkotlin.R
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Created by tin3p on 9/27/2017.
 */
class AdapterListSelectFriends constructor(var context:Context,var arrSelectFriend:ArrayList<SelectFriendsDC>) : BaseAdapter() { //co the bo contrucor di
    var checkedFriend= arrayListOf<String>()
    class ViewHolder(row:View) {
        var tv_user_name: TextView
        var iv_user_photo:CircleImageView
        var id_checkbox:CheckBox
        init {
            tv_user_name=row.findViewById(R.id.tv_user_name)
            iv_user_photo=row.findViewById(R.id.iv_user_photo)
            id_checkbox=row.findViewById(R.id.id_checkbox)
        }
    }

    fun isContactAdded(item: SelectFriendsDC): Boolean {
        for (c in arrSelectFriend)
            if (item.userID == c.userID)
                return true
        return false
    }

    fun notifyItemDataChange(item: SelectFriendsDC) {
        for (i in 0..arrSelectFriend.size-1) {
            if (arrSelectFriend[i].userID == item.userID) {
                arrSelectFriend[i] = item
                notifyDataSetChanged()
                break
            }
        }
    }


    fun addItem(item: SelectFriendsDC) {
        arrSelectFriend.add(item)
        notifyDataSetChanged()
    }

    fun getListFriendChecked():ArrayList<String>
    {
        return checkedFriend
    }
    override fun getView(position: Int, convertview: View?, p2: ViewGroup?): View? {
        var view:View?
        var viewholder:ViewHolder
        if(convertview==null)
        {
            var layoutInflater:LayoutInflater= LayoutInflater.from(context)
            view=layoutInflater.inflate(R.layout.list_item_select_friend,null)
            viewholder= ViewHolder(view)
            view.tag=viewholder
        }
        else
        {
            view=convertview
            viewholder= convertview.tag as ViewHolder
        }
        var select_friend:SelectFriendsDC= getItem(position) as SelectFriendsDC
        viewholder.tv_user_name.text=select_friend.name
        Picasso.with(context).load(select_friend.avarta).into(  viewholder.iv_user_photo)
        viewholder.id_checkbox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    checkedFriend.add(arrSelectFriend.get(position).userID!!)
                } else {
                    checkedFriend.remove(arrSelectFriend.get(position).userID!!)
                }
            }
        })
        return view
    }

    override fun getItem(possition: Int): Any {
       return arrSelectFriend.get(possition)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong();
    }

    override fun getCount(): Int {
        return arrSelectFriend.size
    }
}