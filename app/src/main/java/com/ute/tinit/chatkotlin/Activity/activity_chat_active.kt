package com.ute.tinit.chatkotlin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ute.tinit.chatkotlin.ChatAction.ChatDataAdapter
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.content_chat_activity.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import com.ute.tinit.chatkotlin.DataClass.*
import io.vrinda.kotlinpermissions.PermissionCallBack
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.collections.ArrayList


class activity_chat_active : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatDataAdapter? = null
    private var text: EditText? = null
    private var mDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    var userid = ""
    var userFR = ""
    var group_check = false
    var converID: String = ""
    var currentConver = ""
    var checkExitsConver: Boolean = false
    var isBlock = false
    val data = arrayListOf<ChatDataDC>()
    private var DATA_UPDATE: ByteArray? = null
    private var mStorageRef: StorageReference? = null
    var imgUploadLink: String = ""
    var imgUri: Uri? = null
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_chat_active)
        toolbar_chat.title = ""
        setSupportActionBar(toolbar_chat)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        userid = mAuth!!.uid!!
        var intent = intent
        group_check = intent.extras.getBoolean("group_check")
        if (group_check.equals(false)) {
            userFR = intent.getStringExtra("userfriend")
            setData()
            //xem cai nay ton tai ko. dc mo tu conversaion
            if (intent.hasExtra("isfriend")) {
                if (intent.getStringExtra("isfriend").equals("Người lạ")) {
                    //kiem tra tin nhan dau tien, neu la minh ko hien thong bao, nguoc lai
                    var temp = intent.getStringExtra("conversation")
                    mDatabase!!.child("conversation").child(temp).child("messages").limitToFirst(1)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.value != null) {
                                        for (snap in p0.children) {
                                            var tempMess: MessageDC = snap.getValue(MessageDC::class.java)!!
                                            Log.d("tinnhandau", "x = " + tempMess.content)
                                            Log.d("tinnhandau", "x = " + tempMess!!.idSender)

                                            if (tempMess!!.idSender != userid) {
                                                isFriendDialog()
                                            }
                                        }

                                    } else {
                                        Log.d("tinnhandau", "x = null")

                                    }
                                }

                            })
                }
            }
        } else {
            converID = intent.getStringExtra("conversation")
            // nameconver = intent.getStringExtra("nameconver")
            setDataChatGroup()
        }
        checkExitsConversation()
        checkBlock()
        btnSend()
        loadDATA()
        textEmply()
        profileFriend()
    }


    fun checkExitsConversation() {
        mDatabase!!.child("user_listconver").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {

                        if (p0!!.getValue() != null) {
                            for (snap in p0!!.children) {
                                //vao id cua conversation ma user do chat
                                mDatabase!!.child("conversation").child("" + snap!!.value)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.value != null) {
                                                    var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                    if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                            (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {
                                                        //xu ly them chat trong nay
                                                        checkExitsConver = true
                                                        currentConver = snap!!.value.toString()
                                                        //them chat
                                                    }
                                                } else {
                                                    checkExitsConver = false
                                                }
                                            }

                                        })
                            }
                        }
                    }
                })
    }

    fun isFriendDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this@activity_chat_active)
        // khởi tạo dialog
        alertDialogBuilder.setMessage("Người này không có trong danh sách bạn, bạn có muốn trò chuyện cùng người này không?")
        // thiết lập nội dung cho dialog
        alertDialogBuilder.setNegativeButton("Có") { dialog, which ->
            dialog.dismiss()
            // button "no" ẩn dialog đi
        }
        alertDialogBuilder.setPositiveButton("Block") { arg0, arg1 ->
            //xoa request la ban
            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.getValue() != null) {
                                for (postSnapshot in p0!!.getChildren()) {
                                    Log.d("keyrf", postSnapshot.key)
                                    mDatabase!!.child("request_friend").child(postSnapshot.key)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError?) {
                                                }

                                                override fun onDataChange(p0: DataSnapshot?) {
                                                    if (p0!!.getValue() != null) {
                                                        var getRF: RequestFriendDC
                                                        getRF = p0!!.getValue(RequestFriendDC::class.java)!!
                                                        if (getRF.userid2 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusx", "Du lieu user1 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid1").equalTo(userid).removeEventListener(this)
                        }
                    })
            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.getValue() != null) {
                                for (postSnapshot in p0!!.getChildren()) {
                                    Log.d("keyrf", postSnapshot.key)
                                    mDatabase!!.child("request_friend").child(postSnapshot.key)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onCancelled(p0: DatabaseError?) {
                                                }

                                                override fun onDataChange(p0: DataSnapshot?) {
                                                    if (p0!!.getValue() != null) {
                                                        var getRF: RequestFriendDC
                                                        getRF = p0!!.getValue(RequestFriendDC::class.java)!!
                                                        if (getRF.userid1 == userFR) {
                                                            Log.d("statusx", "key = " + p0.key)
                                                            mDatabase!!.child("request_friend").child(p0.key).removeValue()
                                                        }
                                                    }
                                                    mDatabase!!.child("request_friend").child(postSnapshot.key).removeEventListener(this)
                                                }
                                            })
                                }
                            } else {
                                Log.d("statusxxx", "Du lieu user2 null")
                            }
                            mDatabase!!.child("request_friend").orderByChild("userid2").equalTo(userid).removeEventListener(this)
                        }
                    })
            val handler: Handler = Handler()
            handler.postDelayed(Runnable {
                var setRF = RequestFriendDC(userid, userFR, userid, "3")
                mDatabase!!.child("request_friend").push().setValue(setRF)
            }, 2000)
            finish()
        }
        val alertDialog = alertDialogBuilder.create()
        // tạo dialog
        alertDialog.show()
    }

    fun checkBlock() {
        mDatabase!!.child("request_friend").orderByChild("status").equalTo("3")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            for (snap in p0!!.children) {
                                //  Log.d("ddd",snap.key +" "+snap.value)
                                var tempRF: RequestFriendDC = snap.getValue(RequestFriendDC::class.java)!!
                                if ((tempRF.userid1 == userid && tempRF.userid2 == userFR) || (tempRF.userid1 == userFR && tempRF.userid2 == userid)) {
                                    isBlock = true
                                    if (isBlock == true) {
                                        Log.d("ddd", "dunggg")
                                    } else {
                                        Log.d("ddd", "saiiii")

                                    }

                                }
                            }
                        } else {
                            Log.d("ddd", "nullllll")
                        }
                        mDatabase!!.child("request_friend").orderByChild("status").equalTo("3").removeEventListener(this)
                    }

                })
        isBlock = false
    }

    fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi: Boolean = false
        var haveConnectedMobile: Boolean = false
        var cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var netInfo: Array<NetworkInfo> = cm.getAllNetworkInfo();
        for (ni in netInfo) {
            if (ni.getTypeName().equals("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equals("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    fun profileFriend() {
//        tv_nameuser.setOnClickListener {
//            var intent = Intent(this@activity_chat_active, activity_friend_profile::class.java)
//            intent.putExtra("userfriend",userFR)
//            startActivity(intent)
//        }

        btnMoreChat.setOnClickListener {
            // Toast.makeText(this@activity_chat_active, "Ahih", Toast.LENGTH_SHORT).show()
            if (haveNetworkConnection()) {
                Toast.makeText(this@activity_chat_active, "Ahih", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@activity_chat_active, "Ahih x", Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun getNameById(idUser: String): String {
        var temp = ""
        mDatabase!!.child("users").child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.getValue() != null) {
                    var getFriend: UserDC = p0.getValue(UserDC::class.java)!!
                    temp = getFriend.name!!
                    Log.d("userFR", temp)
                    Log.d("userFR", getFriend.userID)
                }
            }
        })
        return temp
    }

    fun aniBtnSendMore_activesendmore() {
        var animationLogoName: Animation = AnimationUtils.loadAnimation(this@activity_chat_active, R.anim.zoomandrotate)
        btnSendMore.startAnimation(animationLogoName)
    }

    fun animation_BtnSend_actives() {
        var animationLogoName: Animation = AnimationUtils.loadAnimation(this@activity_chat_active, R.anim.zoomandrotate)
        btnSend.startAnimation(animationLogoName)
    }

    fun removeAnimotionbtnSendmore() {
        btnSendMore.animate().cancel()
        btnSendMore.clearAnimation()
    }

    fun removeAnimotionbtnSend() {
        btnSend.animate().cancel()
        btnSend.clearAnimation()
    }

    fun textEmply() {
        btnSend.visibility = View.GONE
        btnSendMore.visibility = View.VISIBLE
        et_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    animation_BtnSend_actives()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("AAA", "onTextChanged " + et_message.text)
                var checkAni: Boolean = true
                if (et_message.text.toString().equals("") || (et_message.text.toString().equals(null))) {
                    Log.d("AAA", "NULL TEXT")
                    btnSend.visibility = View.GONE
                    btnSendMore.visibility = View.VISIBLE
                    aniBtnSendMore_activesendmore()
                    removeAnimotionbtnSend()
                    var checkAni = false
                } else {
                    removeAnimotionbtnSendmore()
                    Log.d("AAA", "NOT NULL TEXT")
                    btnSend.visibility = View.VISIBLE
                    btnSendMore.visibility = View.GONE
                }
            }
        })
    }

    fun btnSend() {
        btnSend.setOnClickListener {
            Log.d("ddd", "nullllll")
            if (isBlock == true) {
                Log.d("ddd", "dunggg")
            } else {
                Log.d("ddd", "saiiii")

            }

            if (checkExitsConver == true) {
                Log.d("ddd", "ton tai")
            } else {
                Log.d("ddd", "ko ton tai")
            }

            if ((!text!!.text.equals("") || !text!!.text.equals(null)) && haveNetworkConnection() == true) {
                //chat don
                if (group_check == false) {
                    if (isBlock == false) { //xem bi block chua
//                        edit_text_name.isFocusable=false
//                        btnSend.isEnabled = false
//                        btnSendMore.isEnabled = false
                        if (checkExitsConver == true) {
                            Log.d("ddd", "ton tai")
                            //vao id cua conversation ma user do chat
                            //xu ly them chat trong nay
                            //them chat
                            var myRefMess = mDatabase!!.child("conversation").child(currentConver).child("messages").push()
                            Log.d("TTT", "THEM CHAT")
                            val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                            val currentTime = df.format(Calendar.getInstance().time)
                            var userSeen = arrayListOf<String>(userid)
                            var mess = MessageDC(myRefMess.key, currentConver, userid, et_message.text.toString(), "2", currentTime, userSeen)
                            myRefMess.setValue(mess)
                            text!!.setText("")

                        } else {
                            Log.d("ddd", "ko ton tai")
                            var tempListUser = arrayListOf<String>(userid, userFR)
                            var listMessage = arrayListOf<MessageDC>()
                            var myRef = mDatabase!!.child("conversation").push()
                            var conver = ConversationDC(myRef.key, "", tempListUser, false, listMessage)
                            myRef.setValue(conver).addOnCompleteListener {
                                mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                                mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                                //them chat
                                //type=2 is me
                                val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                val currentTime = df.format(Calendar.getInstance().time)
                                var userSeen = arrayListOf<String>(userid)
                                var myRefMess = mDatabase!!.child("conversation").child(myRef.key).child("messages").push()
                                var mess = MessageDC(myRefMess.key, "" + myRef.key, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                myRefMess.setValue(mess)
                                text!!.setText("")
                            }
                        }
                    } else {
//                        edit_text_name.isFocusable = true
//                        btnSend.isEnabled = true
//                        btnSendMore.isEnabled = true
                    }

                } else { //la group
                    mDatabase!!.child("conversation").child(converID)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.value != null) {
                                        var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!

                                        //xu ly them chat trong nay
                                        //them chat
                                        var myRefMess = mDatabase!!.child("conversation").child(converID).child("messages").push()
                                        Log.d("TTT", "THEM CHAT")
                                        val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                        val currentTime = df.format(Calendar.getInstance().time)
                                        var userSeen = arrayListOf<String>(userid)
                                        var mess = MessageDC(myRefMess.key, converID, userid, et_message.text.toString(), "2", currentTime, userSeen)
                                        myRefMess.setValue(mess)
                                        text!!.setText("")
                                        mDatabase!!.child("conversation").child(converID).removeEventListener(this)

                                    }
                                }
                            })
                }
            } else {
                Toast.makeText(this@activity_chat_active, "Không có kết nối Internet!!!", Toast.LENGTH_SHORT).show()
                createNetErrorDialog()
            }
        }

        btnSendMore.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this@activity_chat_active, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@activity_chat_active, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@activity_chat_active, "ABC", Toast.LENGTH_SHORT).show()
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 2)
            } else {
                Toast.makeText(this@activity_chat_active, "Vui lòng bật quyền vị trí!!!", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(this@activity_chat_active, PERMISSIONS_STORAGE, 1)
            }


        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@activity_chat_active, "Bạn đã không chấp nhận quyền :( ", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    fun createNetErrorDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this);
        builder.setMessage("Mở kết nối Wifi hoặc mạng di động")
                .setTitle("Không có kết nối Internet")
                .setCancelable(false)
                .setPositiveButton("Wifi Settting",
                        DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                            var i: Intent = Intent(ACTION_WIFI_SETTINGS)
                            startActivity(i)
                        }
                )
                .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->

                        }
                )
        var alert: AlertDialog = builder.create()
        alert.show()
    }

    fun loadDATA() {
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = ChatDataAdapter(this@activity_chat_active, data as MutableList<ChatDataDC>)
        mRecyclerView!!.adapter = mAdapter


        text = findViewById(R.id.et_message)
        text!!.setOnClickListener { mRecyclerView!!.postDelayed({ mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1) }, 400) }
        if (group_check == false) {
            mDatabase!!.child("users").child(userFR)
                    .addValueEventListener(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.getValue() != null) {
                                        var getFriend: UserDC = p0.getValue(UserDC::class.java)!!
                                        tv_nameuser.setText("" + getFriend.name!!)
                                    }
                                }

                            })
        } else {
            tv_nameuser.setText("Nhóm")
        }
    }

//  private fun mCheckInforInServer(child: String) {
//        FirebaseQuery().mReadDataOnce(child, object : OnGetDataListener {
//            override fun onStart() {
//                //DO SOME THING WHEN START GET DATA HERE
//            }
//
//            override fun onSuccess(data: DataSnapshot) {
//                //DO SOME THING WHEN GET DATA SUCCESS HERE
//            }
//
//            override fun onFailed(databaseError: DatabaseError) {
//                //DO SOME THING WHEN GET DATA FAILED HERE
//            }
//        })
//
//    }

    //    override fun onPause() {
//        Log.d("chatactive","OnPause")
//        if (seenListener != null && mDatabase!=null) {
//            Log.d("chatactive","OnPause seenListener")
//            mDatabase!!.child("user_listconver").child(userid).removeEventListener(seenListener);
//        }
//        super.onPause()
//    }
//
//    override fun onStop() {
//        Log.d("chatactive","onStop")
//        if (seenListener != null && mDatabase!=null) {
//            Log.d("chatactive","onStop seenListener")
//            mDatabase!!.child("user_listconver").child(userid).removeEventListener(seenListener);
//        }
//        super.onStop()
//
//    }
    fun setData() {
        //vào ds cuộc trò chuyện người mình
        mDatabase!!.child("user_listconver").child(userid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.getValue() != null) {
                            for (snap in p0!!.children) {
                                //vao id cua conversation ma user do chat
                                //vào cuộc trò chuyện
                                mDatabase!!.child("conversation").child("" + snap!!.value)
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {
                                            }

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                if (p0!!.value != null) {
                                                    var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                    Log.d("TTT", temp.idConver)
                                                    //kiểm tra đâu là cuộc trò chuyện mình
                                                    if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                            (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {
                                                        // load danh sach nguoi = mang
                                                        var userList = ArrayList<String>(temp!!.listUsers)
                                                        var avatar = ""
                                                        mDatabase!!.child("users").child(userFR).child("avatar")
                                                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                                                    }

                                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                                        if (p0!!.value != null) {
                                                                            avatar = p0!!.value.toString()
                                                                        } else {
                                                                            avatar = "http://1.gravatar.com/avatar/1771f433d2eed201bd40e6de0c3a74a7?s=1024&d=mm&r=g"
                                                                        }
                                                                    }

                                                                })

                                                        // load tin nhan

                                                        mDatabase!!.child("conversation").child(temp.idConver).child("messages")
                                                                .addChildEventListener(object : ChildEventListener {
                                                                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                                                    }

                                                                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                                                    }

                                                                    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                                                                        if (p0!!.value != null) {

                                                                            var getMessage: MessageDC = p0!!.getValue(MessageDC::class.java)!!
                                                                            var checkType = getMessage.type

                                                                            if (getMessage.type == "0") {
                                                                                checkType = "0"
                                                                            } else {
                                                                                if (getMessage.idSender == userid) {
                                                                                    checkType = getMessage.type
                                                                                } else {
                                                                                    if (getMessage.type == "2") {
                                                                                        checkType = "1"
                                                                                    } else {
                                                                                        if (getMessage.type == "4")
                                                                                            checkType = "3"
                                                                                    }
                                                                                }
                                                                            }
                                                                            Log.d("ABCC", "check: " + checkType)

                                                                            //lấy định dạng ngày mặt định
                                                                            val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                                            //parse ngày ở firebase sang dụng hh:mm
                                                                            val t = df.parse(getMessage.date)
                                                                            df.applyPattern("hh:mm")
//
                                                                            var itemx = ChatDataDC(getMessage.id, avatar, checkType, getMessage.content, df.format(t))
                                                                            if ((mRecyclerView!!.adapter as ChatDataAdapter).isAdded(itemx))
                                                                                (mRecyclerView!!.adapter as ChatDataAdapter).notifyDataSetChanged()
                                                                            else
                                                                                (mRecyclerView!!.adapter as ChatDataAdapter).addItem(itemx)
                                                                            mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1)
                                                                        } else {
                                                                            Log.d("TTT", "MESSAGE NULL")
                                                                        }
                                                                    }

                                                                    override fun onChildRemoved(p0: DataSnapshot?) {
                                                                    }

                                                                    override fun onCancelled(p0: DatabaseError?) {
                                                                    }
                                                                })
                                                    }
                                                } else {
                                                    Log.d("TTT", "ConverNull")
                                                }
                                            }
                                        })
                            }
                        } else {
                            Log.d("TTT", "user_listconver null")
                        }
                    }
                })
    }

    fun setDataChatGroup() {

        //vao id cua conversation ma user do chat
        //vào cuộc trò chuyện
        mDatabase!!.child("conversation").child(converID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0!!.value != null) {
                            var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                            Log.d("TTT", temp.idConver)
                            //kiểm tra đâu là cuộc trò chuyện mình
                            // load danh sach nguoi = mang

                            var avatar = "http://1.gravatar.com/avatar/1771f433d2eed201bd40e6de0c3a74a7?s=1024&d=mm&r=g"
                            // load tin nhan
                            mDatabase!!.child("conversation").child(converID).child("messages")
                                    .addChildEventListener(object : ChildEventListener {
                                        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                                        }

                                        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                                        }

                                        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                                            if (p0!!.value != null) {
                                                var getMessage: MessageDC = p0!!.getValue(MessageDC::class.java)!!
                                                mDatabase!!.child("users").child(getMessage.idSender).child("avatar")
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError?) {
                                                            }

                                                            override fun onDataChange(p0: DataSnapshot?) {
                                                                var checkType = getMessage.type
                                                                if (getMessage.type == "0") {
                                                                    checkType = "0"
                                                                } else {
                                                                    if (getMessage.idSender == userid) {
                                                                        checkType = getMessage.type
                                                                    } else {
                                                                        if (getMessage.type == "2") {
                                                                            checkType = "1"
                                                                        } else {
                                                                            if (getMessage.type == "4")
                                                                                checkType = "3"
                                                                        }
                                                                    }
                                                                }
                                                                Log.d("ABCC", "checkType: " + checkType)

                                                                //lấy định dạng ngày mặt định
                                                                val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                                                //parse ngày ở firebase sang dụng hh:mm
                                                                val t = df.parse(getMessage.date)
                                                                df.applyPattern("hh:mm")
                                                                var itemx = ChatDataDC(getMessage.id, p0!!.getValue().toString(), checkType, getMessage.content, df.format(t))
                                                                if ((mRecyclerView!!.adapter as ChatDataAdapter).isAdded(itemx))
                                                                    (mRecyclerView!!.adapter as ChatDataAdapter).notifyDataSetChanged()
                                                                else
                                                                    (mRecyclerView!!.adapter as ChatDataAdapter).addItem(itemx)
                                                                mRecyclerView!!.smoothScrollToPosition(mRecyclerView!!.adapter.itemCount - 1)
                                                            }
                                                        })
                                            } else {
                                                Log.d("TTT", "MESSAGE NULL")
                                            }
                                        }

                                        override fun onChildRemoved(p0: DataSnapshot?) {
                                        }

                                        override fun onCancelled(p0: DatabaseError?) {
                                        }
                                    })
                        } else {
                            Log.d("TTT", "ConverNull")
                        }
                    }
                })
    }

    override fun onBackPressed() {
        //seenlistener
        if (group_check == false) {
            mDatabase!!.child("user_listconver").child(userid)
                    .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError?) {
                                }

                                override fun onDataChange(p0: DataSnapshot?) {
                                    if (p0!!.getValue() != null) {
                                        Log.d("ABC", "SU KIEN")
                                        var checkExitsConver: Boolean = false
                                        for (snap in p0!!.children) {
                                            //vao id cua conversation ma user do chat
                                            mDatabase!!.child("conversation").child("" + snap!!.value)
                                                    .addValueEventListener(object : ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError?) {
                                                        }

                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                            if (p0!!.value != null) {
                                                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                                                if (temp!!.isGroup == false && ((temp!!.listUsers!!.get(0) == userid && temp!!.listUsers!!.get(1) == userFR) ||
                                                                        (temp!!.listUsers!!.get(1) == userid && temp!!.listUsers!!.get(0) == userFR))) {
                                                                    //xu ly them chat trong nay

                                                                    //addseen
                                                                    mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1)
                                                                            .addListenerForSingleValueEvent(
                                                                                    object : ValueEventListener {
                                                                                        override fun onDataChange(p0: DataSnapshot?) {
                                                                                            if (p0!!.value != null) {
                                                                                                for (snapMess in p0!!.children) {
                                                                                                    var kiemTraTonTai = false
                                                                                                    var tempContent = ""
                                                                                                    var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                                                    for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                                                        Log.d("ABC", "User " + tempMess.userSeen!![i])
                                                                                                        if (tempMess.userSeen!![i].equals(userid)) {
                                                                                                            kiemTraTonTai = true
                                                                                                            Log.d("ABC", "REMOVE USER RIGHT")
                                                                                                            break
                                                                                                        }
                                                                                                    }
                                                                                                    if (kiemTraTonTai == false) {
                                                                                                        tempMess.userSeen!!.add(userid)
                                                                                                        Log.d("ABC", "REmove listener")
                                                                                                        mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").child(tempMess.id).setValue(tempMess)
                                                                                                    }

                                                                                                }
                                                                                                mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1).removeEventListener(this)
                                                                                            } else {
                                                                                                mDatabase!!.child("conversation").child(snap!!.value.toString()).child("messages").limitToLast(1).removeEventListener(this)
                                                                                                Log.d("RRR", "NULL MESSAGE LAST")
                                                                                            }
                                                                                        }

                                                                                        override fun onCancelled(p0: DatabaseError?) {
                                                                                        }


                                                                                    })
                                                                }
                                                                mDatabase!!.child("conversation").child("" + snap!!.value).removeEventListener(this)
                                                            } else {
                                                                mDatabase!!.child("conversation").child("" + snap!!.value).removeEventListener(this)

                                                                Log.d("AAA", "DATA Conver NUll")
                                                            }
                                                        }

                                                    })
                                        }
                                        mDatabase!!.child("user_listconver").child(userid).removeEventListener(this)
                                    }
                                }

                            }
                    )
        } else {
            //vao id cua conversation ma user do chat
            mDatabase!!.child("conversation").child(converID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!
                                //xu ly them chat trong nay
                                //addseen
                                mDatabase!!.child("conversation").child(converID).child("messages").limitToLast(1)
                                        .addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onDataChange(p0: DataSnapshot?) {
                                                        if (p0!!.value != null) {
                                                            for (snapMess in p0!!.children) {
                                                                var kiemTraTonTai = false
                                                                var tempContent = ""
                                                                var tempMess: MessageDC = snapMess!!.getValue(MessageDC::class.java)!!
                                                                for (i in 0..tempMess.userSeen!!.size - 1) {
                                                                    Log.d("ABC", "User " + tempMess.userSeen!![i])
                                                                    if (tempMess.userSeen!![i].equals(userid)) {
                                                                        kiemTraTonTai = true
                                                                        Log.d("ABC", "REMOVE USER RIGHT")
                                                                        break
                                                                    }
                                                                }
                                                                if (kiemTraTonTai == false) {
                                                                    tempMess.userSeen!!.add(userid)
                                                                    Log.d("ABC", "REmove listener")
                                                                    mDatabase!!.child("conversation").child(converID).child("messages").child(tempMess.id).setValue(tempMess)
                                                                }

                                                            }
                                                        }
                                                        mDatabase!!.child("conversation").child(converID).child("messages").limitToLast(1).removeEventListener(this)
                                                    }

                                                    override fun onCancelled(p0: DatabaseError?) {
                                                    }
                                                })
                                mDatabase!!.child("conversation").child(converID).removeEventListener(this)

                            }
                        }
                    })
        }
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        when (id) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            Log.d("AAA", "requestCode = " + requestCode)
            Log.d("AAA", "RESULT_OK = " + RESULT_OK)
            Log.d("AAA", "resultCode = " + resultCode)

            if (requestCode == 2 && resultCode == RESULT_OK) {
                try {
                    val selectedImage = data!!.getData()

                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

                    val baos = ByteArrayOutputStream()
                    //giam dung luong truoc khi day len firebase :(
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                    DATA_UPDATE = baos.toByteArray()
                    imgUri = selectedImage
                    Toast.makeText(this@activity_chat_active, "Updating...", Toast.LENGTH_SHORT).show()
                    Upload()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        } else {
            Log.d("AAA", "DATA NULL")
        }
    }

    fun Upload() {
        if (DATA_UPDATE != null) {
            var dialog = ProgressDialog(this@activity_chat_active)
            dialog.setTitle("Uploading image")
            dialog.show()
            //resize
            var ref: StorageReference = mStorageRef!!.child("IMAGESEND/" + System.currentTimeMillis() + "." + "JPEG")

            ref.putBytes(DATA_UPDATE!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            dialog.dismiss()
                            imgUploadLink = p0!!.getDownloadUrl().toString()
                            Toast.makeText(this@activity_chat_active,
                                    "Image Uploaded -> " + imgUploadLink, Toast.LENGTH_SHORT).show()
                            //set image test
                            Log.d("BBB", imgUploadLink)
                            sendLinkImage()
                        }
                    })
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(this@activity_chat_active,
                                "Image Error", Toast.LENGTH_SHORT).show()

                    }
                    .addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot> {
                        override fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                            var progress: Long = (100 * taskSnapshot!!.bytesTransferred) / taskSnapshot.totalByteCount
                            dialog.setMessage("Uploading " + progress.toInt() + "%")
                        }

                    })
        } else {
            Toast.makeText(this@activity_chat_active,
                    "Select Image Error", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendLinkImage()
    {
        if (group_check == false) {
            if (isBlock == false) { //xem bi block chua
//                        edit_text_name.isFocusable=false
//                        btnSend.isEnabled = false
//                        btnSendMore.isEnabled = false
                if (checkExitsConver == true) {
                    Log.d("ddd", "ton tai")
                    //vao id cua conversation ma user do chat
                    //xu ly them chat trong nay
                    //them chat
                    var myRefMess = mDatabase!!.child("conversation").child(currentConver).child("messages").push()
                    Log.d("TTT", "THEM CHAT")
                    val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                    val currentTime = df.format(Calendar.getInstance().time)
                    var userSeen = arrayListOf<String>(userid)
                    var mess = MessageDC(myRefMess.key, currentConver, userid, imgUploadLink, "4", currentTime, userSeen)
                    myRefMess.setValue(mess)
                    text!!.setText("")

                } else {
                    Log.d("ddd", "ko ton tai")
                    var tempListUser = arrayListOf<String>(userid, userFR)
                    var listMessage = arrayListOf<MessageDC>()
                    var myRef = mDatabase!!.child("conversation").push()
                    var conver = ConversationDC(myRef.key, "", tempListUser, false, listMessage)
                    myRef.setValue(conver).addOnCompleteListener {
                        mDatabase!!.child("user_listconver").child(userid).push().setValue(myRef.key)
                        mDatabase!!.child("user_listconver").child(userFR).push().setValue(myRef.key)
                        //them chat
                        //type=2 is me
                        val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                        val currentTime = df.format(Calendar.getInstance().time)
                        var userSeen = arrayListOf<String>(userid)
                        var myRefMess = mDatabase!!.child("conversation").child(myRef.key).child("messages").push()
                        var mess = MessageDC(myRefMess.key, "" + myRef.key, userid, imgUploadLink, "4", currentTime, userSeen)
                        myRefMess.setValue(mess)
                        text!!.setText("")
                    }
                }
            } else {
//                        edit_text_name.isFocusable = true
//                        btnSend.isEnabled = true
//                        btnSendMore.isEnabled = true
            }

        } else { //la group
            mDatabase!!.child("conversation").child(converID)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if (p0!!.value != null) {
                                var temp: ConversationDC = p0!!.getValue(ConversationDC::class.java)!!

                                //xu ly them chat trong nay
                                //them chat
                                var myRefMess = mDatabase!!.child("conversation").child(converID).child("messages").push()
                                Log.d("TTT", "THEM CHAT")
                                val df = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
                                val currentTime = df.format(Calendar.getInstance().time)
                                var userSeen = arrayListOf<String>(userid)
                                var mess = MessageDC(myRefMess.key, converID, userid, imgUploadLink, "4", currentTime, userSeen)
                                myRefMess.setValue(mess)
                                text!!.setText("")
                                mDatabase!!.child("conversation").child(converID).removeEventListener(this)

                            }
                        }
                    })
        }
    }
}


