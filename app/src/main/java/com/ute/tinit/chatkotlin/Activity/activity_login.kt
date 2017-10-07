package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_login.*

class activity_login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_login)
        dangKyTK()
    }

    fun dangKyTK()
    {
        tv_dangkytk.setOnClickListener{
            var intent=Intent(this@activity_login,activity_new_account::class.java)
            startActivity(intent)
        }
    }
}
