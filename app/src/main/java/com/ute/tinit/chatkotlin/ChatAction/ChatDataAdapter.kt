package com.ute.tinit.chatkotlin.ChatAction

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ute.tinit.chatkotlin.DataClass.ChatDataDC
import com.ute.tinit.chatkotlin.R

/**
 * Created by tin3p on 10/8/2017.
 */
class ChatDataAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private val mContext: Context, // The items to display in your RecyclerView
 private val items: MutableList<ChatDataDC>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DATE = 0
    private val YOU = 1
    private val ME = 2

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun getItemViewType(position: Int): Int {
        //More to come
        if (items[position].type.equals("0")) {
            return DATE
        } else if (items[position].type.equals("1")) {
            return YOU
        } else if (items[position].type.equals("2")) {
            return ME
        }
        return -1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(viewGroup.context)

        when (viewType) {
            DATE -> {
                val v1 = inflater.inflate(R.layout.layout_holder_date, viewGroup, false)
                viewHolder = HolderDate(v1)
            }
            YOU -> {
                val v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false)
                viewHolder = HolderYou(v2)
            }
            else -> {
                val v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false)
                viewHolder = HolderMe(v)
            }
        }
        return viewHolder
    }

    fun addItem(item: List<ChatDataDC>) {
        items.addAll(item)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            DATE -> {
                val vh1 = viewHolder as HolderDate
                configureViewHolder1(vh1, position)
            }
            YOU -> {
                val vh2 = viewHolder as HolderYou
                configureViewHolder2(vh2, position)
            }
            else -> {
                val vh = viewHolder as HolderMe
                configureViewHolder3(vh, position)
            }
        }
    }

    private fun configureViewHolder3(vh1: HolderMe, position: Int) {
        vh1.time!!.setText(items[position].Time)
        vh1.chatText!!.setText(items[position].text)
    }

    private fun configureViewHolder2(vh1: HolderYou, position: Int) {
        vh1.time!!.setText(items[position].Time)
        vh1.chatText!!.setText(items[position].text)
    }

    private fun configureViewHolder1(vh1: HolderDate, position: Int) {
        vh1.date!!.setText(items[position].text)
    }
}
