package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.ute.tinit.chatkotlin.Chat_Adapter.ChatAdapter
import com.ute.tinit.chatkotlin.DataClass.ChatDC
import com.ute.tinit.chatkotlin.R
import java.util.ArrayList

class fragment_chat : Fragment(), ChatAdapter.ViewHolder.ClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatAdapter? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_chat, null, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mAdapter = ChatAdapter(context, setData(), this@fragment_chat)
        mRecyclerView!!.adapter = mAdapter

        return view
    }

    fun setData(): List<ChatDC> {
        val data = ArrayList<ChatDC>()
        val name = arrayOf("Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris")
        val lastchat = arrayOf("Hi Laura Owens", "Hi there how are you", "Can we meet?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "How are you?")
        val online = booleanArrayOf(true, false, true, false, true, true, true, false, false, true)

        for (i in 0..9) {
            var chat=ChatDC(name[i],lastchat[i],"5:42","",online[i])
            data.add(chat)
        }
        return data
    }

    override fun onItemClicked(position: Int) {
//        startActivity(Intent(activity, Conversation::class.java))
    }

    override fun onItemLongClicked(position: Int): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

}
