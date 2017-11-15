package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ute.tinit.chatkotlin.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by tin3p on 10/8/2017.
 */
class HolderYou(v: View) : RecyclerView.ViewHolder(v) {

    var time: TextView? = null
    var chatText: TextView? = null
    var avarta: CircleImageView?=null

    init {
        time = v.findViewById(R.id.tv_time)
        chatText = v.findViewById(R.id.tv_chat_text)
        avarta=v.findViewById(R.id.avarta_you)
    }
}
