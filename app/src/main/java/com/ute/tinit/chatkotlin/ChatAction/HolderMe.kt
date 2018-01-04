package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/8/2017.
 */
class HolderMe(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    override fun onClick(v: View?) {
    }

    var time: TextView? = null
    var chatText: TextView? = null

    init {
        time = v.findViewById(R.id.tv_time)
        chatText = v.findViewById(R.id.tv_chat_text)
    }
}
