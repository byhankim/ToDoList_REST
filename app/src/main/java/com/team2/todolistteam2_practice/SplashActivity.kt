package com.team2.todolistteam2_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat.postDelayed

class SplashActivity : AppCompatActivity() {
    private val timeoutCount: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activateSplashScreen(timeoutCount)
    }

    private fun activateSplashScreen(count: Long) {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, count)
    }
}