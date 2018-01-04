package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ute.tinit.chatkotlin.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by tin3p on 11/15/2017.
 */
class HolderImageYou(v: View) : RecyclerView.ViewHolder(v) {

    var time: TextView? = null
    var image: ImageView? = null
    var avarta: CircleImageView? = null

    init {
        time = v.findViewById(R.id.tv_time_you)
        image = v.findViewById(R.id.image_chat_you)
        avarta = v.findViewById(R.id.avarta_you_you)
    }
}
