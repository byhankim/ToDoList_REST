package com.team2.todolistteam2_practice.okhttp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team2.todolistteam2_practice.common.SIGNUP_ADDRESS
import com.team2.todolistteam2_practice.data.SignUpEntity
import com.team2.todolistteam2_practice.databinding.ActivityTodoSignupBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

class OkHttpSignUpActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTodoSignupBinding
    private var signUpDTO = SignUpEntity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, OkHttpSignInActivity::class.java))
        }

        binding.signupBtn.setOnClickListener {
            if (formValidityChecked()) {
                sendServerSignUpInfo()

                // move to login screen OR log in for the user
                startActivity(Intent(this, OkHttpSignInActivity::class.java))
            }
        }
    }

    private fun formValidityChecked(): Boolean {
        var flag = false

        with (binding.signupIdEt) {
            if (binding.signupIdEt.text.isNotEmpty()) {
                signUpDTO.userId = binding.signupIdEt.text.toString()
                flag = true
            } else {
                requestFocus()
            }
        }
        with (binding.signupUnameEt) {
            if (binding.signupUnameEt.text.isNotEmpty()) {
                signUpDTO.name = binding.signupUnameEt.text.toString()
                flag = true
            } else {
                requestFocus()
            }
        }
        with (binding.signupPwEt) {
            if (binding.signupPwEt.text.isNotEmpty()) {
                signUpDTO.password = binding.signupPwEt.text.toString()
                flag = true
            } else {
                requestFocus()
            }
        }
        with (binding.signupPwCheckEt) {
            if (binding.signupPwCheckEt.text.isNotEmpty() ||
                    binding.signupPwEt.text.toString().equals(binding.signupPwCheckEt.text.toString())) {
                flag = true
            } else {
                requestFocus()
            }
        }
        return flag
    }

    private fun sendServerSignUpInfo() {
        Thread {
            val flag: Boolean
            lateinit var response: Response
            val toServer: OkHttpClient

            try {
                toServer = OkHttpManager.getOkHttpClient()

                // 요청 form setting
                val postBody: RequestBody = FormBody.Builder()
                        .add("userId", signUpDTO.userId)
                        .add("name", signUpDTO.name)
                        .add("password", signUpDTO.password)
                        .build()

                // request form -> Query string 방식의 post
                val request: Request = Request.Builder()
                        .url(SIGNUP_ADDRESS)
                        .post(postBody)
                        .build()

                // synchronous 방식
                response = toServer.newCall(request).execute()
                flag = response.isSuccessful

                var responseJSON = ""

                // if successful
                if (flag) {
                    responseJSON = response.body!!.string()
                    try {
                        val jsonObject = JSONObject(responseJSON)
                        runOnUiThread {
                            Toast.makeText(this, jsonObject.optString("ok"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (json: JSONException) {
                        Log.e("ERROR", json.toString())
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "HTTP ERORR 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "예외발생, $e", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}