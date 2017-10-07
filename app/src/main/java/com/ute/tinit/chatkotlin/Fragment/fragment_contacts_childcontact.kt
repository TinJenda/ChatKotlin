package com.ute.tinit.chatkotlin.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.ute.tinit.chatkotlin.Chat_Adapter.ContactAdapter
import com.ute.tinit.chatkotlin.DataClass.ContactDC
import com.ute.tinit.chatkotlin.R
import java.util.ArrayList

class fragment_contacts_childcontact : Fragment(), ContactAdapter.ViewHolder.ClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ContactAdapter? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_fragment_contact_contactchild, null, false)

        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mAdapter = ContactAdapter(context, setData(), this)
        mRecyclerView!!.adapter = mAdapter

        return view
    }

    fun setData(): List<ContactDC> {
        val data = ArrayList<ContactDC>()
        val name = arrayOf("Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris")
        val online = booleanArrayOf(true, true, true, true, true, true, false, false, false, true)
        for (i in 0..9) {
            val contact = ContactDC(name[i],"",online[i])
            data.add(contact)
        }
        return data
    }

    override fun onItemClicked(position: Int) {

    }

    override fun onItemLongClicked(position: Int): Boolean {
        toggleSelection(position)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun toggleSelection(position: Int) {
        mAdapter!!.toggleSelection(position)
    }

}
