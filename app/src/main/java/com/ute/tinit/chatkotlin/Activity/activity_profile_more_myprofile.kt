package com.ute.tinit.chatkotlin.Activity

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import com.ute.tinit.chatkotlin.R
import kotlinx.android.synthetic.main.layout_activity_profile.*
import kotlinx.android.synthetic.main.layout_activity_profile_more.*
import kotlinx.android.synthetic.main.layout_activity_profile_more_myprofile.*

class activity_profile_more_myprofile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile_more_myprofile)
        setSupportActionBar(toolbar1)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        blurImage()
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

    override fun onBackPressed() {
        finish()
    }

    fun blurImage()
    {
        val resultBmp = BlurBuilder.blur(this@activity_profile_more_myprofile, BitmapFactory.decodeResource(resources, R.drawable.avarta))
        toolbarImage1.setImageBitmap(resultBmp)
    }
}
