package com.ute.tinit.chatkotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.layout_flash_screen.*

class Activity_flash_screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_flash_screen)
        logoAnimation()
        logoNameAnimation()
        val handler: Handler = Handler()
        handler.postDelayed(Runnable {
            var intent: Intent = Intent(this@Activity_flash_screen, MainActivity::class.java);
            startActivity(intent)
            finish()
        }, 6400)
    }

    fun logoAnimation() {
        var animation: Animation = AnimationUtils.loadAnimation(this@Activity_flash_screen, R.anim.screenanimation)
        idLogo.startAnimation(animation)
    }

    fun logoNameAnimation() {
        var animationLogoName: Animation = AnimationUtils.loadAnimation(this@Activity_flash_screen, R.anim.screenanimation2)
        idLogoname.startAnimation(animationLogoName)
    }
}
