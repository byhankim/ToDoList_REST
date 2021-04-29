package com.team2.todolistteam2_practice.okhttp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.team2.todolistteam2_practice.databinding.ActivityTodoSigninBinding

class ToDoSignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTodoSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}