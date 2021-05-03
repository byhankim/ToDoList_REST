package com.team2.todolistteam2_practice.okhttp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team2.todolistteam2_practice.common.LOGIN_ADDRESS
import com.team2.todolistteam2_practice.common.TOKEN
import com.team2.todolistteam2_practice.data.SignInEntity
import com.team2.todolistteam2_practice.databinding.ActivityTodoSigninBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

class OkHttpSignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTodoSigninBinding
    private val loginDTO = SignInEntity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with (binding.signinBtnTv) {
            setOnClickListener {
                loginDTO.password = binding.signinPwEt.text.toString()

                // do the login process
                if (checkValidate()) {
                    sendLoginInfoToServer()
                }
            }
        }
        binding.signupBtnTv.setOnClickListener {
            startActivity(Intent(this, OkHttpSignUpActivity::class.java))
        }
    }

    private fun checkValidate(): Boolean {
        var flag = false
        with (binding.signinIdEt) {
            if (binding.signinIdEt.text.isNotEmpty()) {
                loginDTO.userId = binding.signinIdEt.text.toString()
                flag = true
            } else {
                requestFocus()
            }
        }
        with (binding.signinPwEt) {
            if (binding.signinPwEt.text.isNotEmpty()) {
                loginDTO.password = binding.signinPwEt.text.toString()
                flag = true
            } else {
                requestFocus()
            }
        }
        if (!flag) {
            Toast.makeText(this, "id, pw를 확인해주시요", Toast.LENGTH_SHORT).show()
        }
        return flag
    }

    private fun sendLoginInfoToServer() {
        Thread {
            val flag: Boolean
            lateinit var response: Response
            val toServer: OkHttpClient

            try {
                toServer = OkHttpManager.getOkHttpClient()

                // 요청 Form setting
                val postBody: RequestBody = FormBody.Builder()
                        .add("userId", loginDTO.userId)
                        .add("password", loginDTO.password)
                        .build()

                // 요청 setting form (query string) 방식의 pose
                val request: Request = Request.Builder()
                        .url(LOGIN_ADDRESS)
                        .addHeader("token", TOKEN)
                        .post(postBody)
                        .build()

                // synchronous 방식
                response = toServer.newCall(request).execute()
                flag = response.isSuccessful

                var responseJSON = ""

                if (flag) {
                    responseJSON = response.body!!.string()
                    try {
                        val jsonObject = JSONObject(responseJSON)
                        runOnUiThread {
                            Toast.makeText(this, jsonObject.optString("ok"), Toast.LENGTH_SHORT).show()
                            Log.e("[LOGIN successful!!!]", "new token string: ${jsonObject.optString("token")}")

                            // pass token and start activity
                            val intent = Intent(this, ToDoOkHttpRESTActivity::class.java)
                            intent.putExtra("token", jsonObject.optString("token"))
                            startActivity(intent)
                        }
                    } catch (json: JSONException) {
                        Log.e("ERROR!!!", json.toString())
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "HTTP Error 발생!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "예외발생! $e", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}