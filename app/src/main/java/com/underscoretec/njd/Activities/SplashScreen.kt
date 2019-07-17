package com.underscoretec.njd.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.underscoretec.njd.R

class SplashScreen : AppCompatActivity() {
    lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)



        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        Handler().postDelayed({
            if (sharedPref.getBoolean("loginStatus", false)) {
                var intent = Intent(this@SplashScreen, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var intent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}
