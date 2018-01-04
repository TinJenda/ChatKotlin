package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 11/15/2017.
 */
class HolderImageMe(v: View) : RecyclerView.ViewHolder(v) {
    var time: TextView? = null
    var chatText: TextView? = null
    var image:ImageView?=null

    init {
        time = v.findViewById(R.id.tv_time_me)
        image=v.findViewById(R.id.image_chat_me)
    }
}
