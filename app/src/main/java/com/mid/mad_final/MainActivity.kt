package com.mid.mad_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun register(view: View) {
        val intent : Intent = Intent(this,Signup_Login :: class.java)
        startActivity(intent)
    }
    fun login(view: View) {
        val intent : Intent = Intent(this,My_Location :: class.java)
        startActivity(intent)
    }
}