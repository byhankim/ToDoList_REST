package com.team2.todolistteam2_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.team2.todolistteam2_practice.databinding.ActivityMainBinding
import com.team2.todolistteam2_practice.okhttp.ToDoOkHttpRESTActivity
import com.team2.todolistteam2_practice.okhttp_retrofit.OkHttpRetrofitRESTActivity
import com.team2.todolistteam2_practice.retrofit.RetrofitRESTActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okhttpActivityTv.setOnClickListener {
            val intent: Intent = Intent(this, ToDoOkHttpRESTActivity::class.java)
            startActivity(intent)
        }
        binding.retrofitActivityTv.setOnClickListener {
            val intent = Intent(this, RetrofitRESTActivity::class.java)
            startActivity(intent)
        }
        binding.okhttpRetrofitActivityTv.setOnClickListener {
            val intent = Intent(this, OkHttpRetrofitRESTActivity::class.java)
            startActivity(intent)
        }
    }
}