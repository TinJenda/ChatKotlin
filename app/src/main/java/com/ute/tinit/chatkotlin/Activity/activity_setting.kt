package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
