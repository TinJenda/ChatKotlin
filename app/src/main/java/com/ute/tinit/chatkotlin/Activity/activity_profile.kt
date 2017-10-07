package com.ute.tinit.chatkotlin.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ute.tinit.chatkotlin.R
import android.graphics.BitmapFactory
import android.view.MenuItem

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import kotlinx.android.synthetic.main.custom_tab_contact.*
import kotlinx.android.synthetic.main.layout_activity_profile.*

class activity_profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        blurImage()
        nhatky_hinhanh()
        floatClick()
    }

    fun blurImage() {
        val resultBmp = BlurBuilder.blur(this@activity_profile, BitmapFactory.decodeResource(resources, R.drawable.avarta))
        toolbarImage.setImageBitmap(resultBmp)
    }

    fun nhatky_hinhanh() {

        btnAnh.setOnClickListener {
            //            btnAnh.setBackgroundResource(R.drawable.click_btn_contact)
//            btnNhatKy.setBackgroundResource(R.drawable.tab_bg_unselected)
            txtNhatKy.setTextColor(getResources().getColorStateList(R.color.colorDefaultText));
            txtAnh.setTextColor(getResources().getColorStateList(R.color.mainColor))
            liner_nhatky.visibility = View.GONE
            liner_hinhanh.visibility = View.VISIBLE
        }
        btnNhatKy.setOnClickListener {
            txtNhatKy.setTextColor(getResources().getColorStateList(R.color.mainColor));
            txtAnh.setTextColor(getResources().getColorStateList(R.color.colorDefaultText))
            liner_nhatky.visibility = View.VISIBLE
            liner_hinhanh.visibility = View.GONE
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

    fun floatClick() {
        btnFloatProfile.setBackgroundTintList(getResources().getColorStateList(R.color.mainColor));
        btnFloatProfile.setOnClickListener {
            var intent = Intent(this@activity_profile, activity_profile_more::class.java)
            startActivity(intent)
        }
    }
}
