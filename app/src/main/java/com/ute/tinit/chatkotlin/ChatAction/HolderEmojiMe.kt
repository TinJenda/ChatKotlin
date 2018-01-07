package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 11/15/2017.
 */
class HolderEmojiMe(v: View) : RecyclerView.ViewHolder(v) {
    var timeEmoji: TextView? = null
    var imageEmoji: ImageView?=null

    init {
        timeEmoji = v.findViewById(R.id.tv_time_emoji_me)
        imageEmoji=v.findViewById(R.id.emoji_chat_me)
    }
}