package com.ivar7284.catalogcraft

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import java.util.TimerTask

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(applicationContext, LoginRegisterActivity::class.java))
                finish()
            }
        }, 500)
    }
}