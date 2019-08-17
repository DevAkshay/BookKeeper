package com.drunkenbee.bookkeeper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_TIME_OUT = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //This method is used so that your splash activity
        //can cover the entire screen.

        //this will bind your MainActivity.class file with activity_main.

        Handler().postDelayed(Runnable {

            val sharedPref = getSharedPreferences(getString(R.string.shared_preference_key),Context.MODE_PRIVATE)
            val isFirstTime = sharedPref.getBoolean(getString(R.string.first_time), true)
            if (isFirstTime) {
                val i = Intent(
                    this@SplashActivity,
                    IntroActivity::class.java
                )

                startActivity(i)
            }
            else {
                val i = Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )

                startActivity(i)
            }
            finish()
            //the current activity will get finished.
        }, SPLASH_SCREEN_TIME_OUT)
    }
}
