package com.ute.tinit.chatkotlin.ChatAction

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.ute.tinit.chatkotlin.Adapter.CircleTransform
import com.ute.tinit.chatkotlin.DataClass.ChatDataDC
import com.ute.tinit.chatkotlin.DataClass.ConversationDC
import com.ute.tinit.chatkotlin.DataClass.MessageDC
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile.*

/**
 * Created by tin3p on 10/8/2017.
 */
class ChatDataAdapter// Provide a suitable constructor (depends on the kind of dataset)
(private val mContext: Context, // The items to display in your RecyclerView
 private val items: MutableList<ChatDataDC>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabase: DatabaseReference? = null
    var userid = ""

    private val DATE = 0
    private val YOU = 1
    private val ME = 2
    private val IMAGE_YOU = 3
    private val IMAGE_ME = 4
    private val EMOJI_ME=5
    private val EMOJI_YOU=6

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
        } else if (items[position].type.equals("3")) {
            return IMAGE_YOU
        } else if (items[position].type.equals("4")) {
            return IMAGE_ME
        }else if (items[position].type.equals("5")) {
            return EMOJI_ME
        }
        else if (items[position].type.equals("6")) {
            return EMOJI_YOU
        }
        return -1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!

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
            ME -> {
                val v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false)
                viewHolder = HolderMe(v)
            }
            IMAGE_ME -> {
                val v3 = inflater.inflate(R.layout.layout_holder_image_me, viewGroup, false)
                viewHolder = HolderImageMe(v3)
            }
            EMOJI_ME -> {
                val emoji_me = inflater.inflate(R.layout.layout_holder_emoji_me, viewGroup, false)
                viewHolder = HolderEmojiMe(emoji_me)
            }
            EMOJI_YOU -> {
                val emoji_you = inflater.inflate(R.layout.layout_holder_emoji_you, viewGroup, false)
                viewHolder = HolderEmojiYou(emoji_you)
            }
            else -> {
                val v4 = inflater.inflate(R.layout.layout_holder_image_you, viewGroup, false)
                viewHolder = HolderImageYou(v4)
            }
        }
        return viewHolder
    }

    fun isAdded(item: ChatDataDC): Boolean {
        for (c: ChatDataDC in items)
            if (item.id == c.id)
                return true
        return false
    }

    fun notifyItemDataChange(item: ChatDataDC) {
        for (i in 0..items.size - 1) {
            if (items[i].id == item.id) {
                items[i] = item
                notifyItemChanged(i)
                break
            }
        }
    }


    fun addItem(item: ChatDataDC) {
        items.add(item)
        notifyItemInserted(items.size)
    }



    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) =
            when (viewHolder.itemViewType) {
                DATE -> {
                    val vh1 = viewHolder as HolderDate
                    configureViewHolder1(vh1, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                YOU -> {
                    val vh2 = viewHolder as HolderYou
                    configureViewHolder2(vh2, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                IMAGE_ME -> {
                    val vh3 = viewHolder as HolderImageMe
                    configureViewHolderImageMe(vh3, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                IMAGE_YOU -> {
                    val vh3 = viewHolder as HolderImageYou
                    configureViewHolderImageYOU(vh3, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                //sua tai day
                EMOJI_YOU -> {
                    val emojiyou = viewHolder as HolderEmojiYou
                    configureViewHolderEmojiYou(emojiyou, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                EMOJI_ME -> {
                    val emoji_me = viewHolder as HolderEmojiMe
                    configureViewHolderEmojiMe(emoji_me, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
                else -> {
                    val vh = viewHolder as HolderMe
                    configureViewHolder3(vh, position)
                    viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {
                            val dialog = Dialog(v!!.context)
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_delete_message)

                            // Set dialog title
                            dialog.setTitle("")

                            // set values for custom dialog components - text, image and button
                            val btndelete = dialog.findViewById<Button>(R.id.btnXoaTN)

                            btndelete.setOnClickListener {
                                dialog.dismiss()
                                mDatabase!!.child("conversation").child(items[position].idConver).child("messages").child(items[position].id).removeValue()
                                Toast.makeText(v.context,"Đã xóa tin nhắn",Toast.LENGTH_SHORT).show()
                                items.removeAt(position)
                                notifyDataSetChanged()
                            }
                            dialog.show()
                            return true
                        }

                    })
                }
            }


    private fun configureViewHolderImageMe(vh1: HolderImageMe, position: Int) {
        vh1.time!!.setText(items[position].Time)
        Picasso.with(mContext)
                .load(items[position].text)
                .error(R.drawable.color_timeline)
                .placeholder(R.drawable.color_timeline)
                .centerCrop()
                .resize(800, 800)
                .into(vh1.image!!)
    }

    private fun configureViewHolderImageYOU(vh1: HolderImageYou, position: Int) {
        vh1.time!!.setText(items[position].Time)
        Picasso.with(mContext)
                .load(items[position].text)
                .error(R.drawable.color_timeline)
                .placeholder(R.drawable.color_timeline)
                .centerCrop()
                .resize(800, 800)
                .into(vh1.image!!)
        Picasso.with(mContext)
                .load(items[position].avarta)
                .error(R.drawable.color_timeline)
                .into(vh1.avarta!!)
    }

    private fun configureViewHolderEmojiYou(vh1: HolderEmojiYou, position: Int) {
        vh1.time!!.setText(items[position].Time)
        Picasso.with(mContext)
                .load(items[position].text)
                .error(R.drawable.color_timeline)
                .placeholder(R.drawable.color_timeline)
                .centerCrop()
                .resize(800, 800)
                .into(vh1.image!!)
        Picasso.with(mContext)
                .load(items[position].avarta)
                .error(R.drawable.color_timeline)
                .into(vh1.avarta!!)
    }
    private fun configureViewHolderEmojiMe(vh1: HolderEmojiMe, position: Int) {
        vh1.timeEmoji!!.setText(items[position].Time)
        Picasso.with(mContext)
                .load(items[position].text)
                .error(R.drawable.color_timeline)
                .placeholder(R.drawable.color_timeline)
                .centerCrop()
                .resize(800, 800)
                .into(vh1.imageEmoji!!)
    }

    private fun configureViewHolder3(vh1: HolderMe, position: Int) {
        vh1.time!!.setText(items[position].Time)
        vh1.chatText!!.setText(items[position].text)

    }

    private fun configureViewHolder2(vh1: HolderYou, position: Int) {
        vh1.time!!.setText(items[position].Time)
        vh1.chatText!!.setText(items[position].text)

        Picasso.with(mContext)
                .load(items[position].avarta)
                .error(R.drawable.default_avarta)
                .into(vh1.avarta!!)
    }

    private fun configureViewHolder1(vh1: HolderDate, position: Int) {
        vh1.date!!.setText(items[position].text)
    }
}