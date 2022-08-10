package com.naufal.koshka_app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var handler= Handler()
        handler.postDelayed({
            var intent= Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}