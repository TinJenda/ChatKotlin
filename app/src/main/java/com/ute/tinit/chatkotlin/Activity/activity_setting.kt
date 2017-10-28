package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile.*
import kotlinx.android.synthetic.main.layout_activity_setting.*

class activity_setting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_setting)
        toolbarSetting.setTitle("")
        setSupportActionBar(toolbarSetting)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        logout()
    }

    fun logout()
    {
        btn_linear_dangxuat.setOnClickListener {

            AuthUI.getInstance().signOut(this@activity_setting).addOnCompleteListener {
                // do something here
                var intent=Intent(this@activity_setting,activity_login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("logout","logout")
                startActivity(intent)
            }
        }
    }
    override fun onBackPressed() {
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
}
