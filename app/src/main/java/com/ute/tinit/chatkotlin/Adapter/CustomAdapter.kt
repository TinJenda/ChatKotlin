package com.ute.tinit.chatkotlin.Adapter

/**
 * Created by tin3p on 1/8/2018.
 */
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.R

class CustomAdapter(internal var context: Context, internal var logos: ArrayList<String>) : BaseAdapter() {
    internal var inflter: LayoutInflater

    init {
        inflter = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return logos.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup?): View? {
        var view = inflter.inflate(R.layout.custom_gridview, null) // inflate the layout
        val icon = view.findViewById<ImageView>(R.id.icon) as ImageView // get the reference of ImageView
        Picasso.with(view.context)
                .load(logos[i])
                .error(R.drawable.ic_emoji)
                .placeholder(R.drawable.color_timeline)
                .into(icon)
        return view
    }
}
