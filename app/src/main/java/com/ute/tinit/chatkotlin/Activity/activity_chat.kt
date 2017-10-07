package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ute.tinit.chatkotlin.R

class activity_chat : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_chat)
    }

    override fun onBackPressed() {
        finish()
    }
}
