package com.ute.tinit.chatkotlin.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ute.tinit.chatkotlin.R
import android.graphics.BitmapFactory
import android.view.View
import android.widget.LinearLayout
import com.ute.tinit.chatkotlin.Adapter.BlurBuilder
import kotlinx.android.synthetic.main.layout_activity_profile.*


class activity_profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_profile)
        blurImage()
        nhatky_hinhanh()
    }

    fun blurImage()
    {
       val resultBmp = BlurBuilder.blur(this@activity_profile, BitmapFactory.decodeResource(resources, R.drawable.avarta))
        anh_bia.setImageBitmap(resultBmp)
    }

    fun nhatky_hinhanh()
    {
        btnAnh.setBackgroundResource(R.drawable.click_btn_contact)
        btnAnh.setOnClickListener{
            btnAnh.setBackgroundResource(R.drawable.click_btn_contact)
            btnNhatKy.setBackgroundResource(R.drawable.tab_bg_unselected)
            liner_nhatky.visibility= View.GONE
            liner_hinhanh.visibility=View.VISIBLE
        }
        btnNhatKy.setOnClickListener {
            btnNhatKy.setBackgroundResource(R.drawable.click_btn_contact)
            btnAnh.setBackgroundResource(R.drawable.tab_bg_unselected)
            liner_nhatky.visibility= View.VISIBLE
            liner_hinhanh.visibility=View.GONE
        }
    }
}
