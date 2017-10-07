package com.ute.tinit.chatkotlin.ChatAction

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/8/2017.
 */
class HolderDate(v: View) : RecyclerView.ViewHolder(v) {

    var date: TextView? = null

    init {
        date = v.findViewById(R.id.tv_date)
    }
}
