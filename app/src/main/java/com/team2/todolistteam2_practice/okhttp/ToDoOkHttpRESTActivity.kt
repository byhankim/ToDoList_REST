package com.team2.todolistteam2_practice.okhttp

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.team2.todolistteam2_practice.common.GET_TODOS
import com.team2.todolistteam2_practice.common.USR_TOKEN
import com.team2.todolistteam2_practice.data.MeModel
import com.team2.todolistteam2_practice.data.ToDoEntity
import com.team2.todolistteam2_practice.data.ToDoModel
import com.team2.todolistteam2_practice.databinding.LayoutOkhttpRestActivityBinding
import okhttp3.*

class ToDoOkHttpRESTActivity: AppCompatActivity() {

    private lateinit var binding: LayoutOkhttpRestActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutOkhttpRestActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        with (binding.todoRV) {
            layoutManager = manager
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

            val testList = mutableListOf<ToDoEntity>()
            testList.add(ToDoEntity(1, "asdfasdf", true))
            adapter = ToDoRecyclerAdapter(testList, this@ToDoOkHttpRESTActivity)
        }
        getMe()
        getTodos()
    }

    private fun getMe() {
        binding.progressbar.visibility = View.VISIBLE

        var uname: String

        Thread {
            val toServer: OkHttpClient
            lateinit var response: Response
            try {
                // URL Setting. GET 방식에선 이런식으로 세팅한다
                val targetURL: HttpUrl = OkHttpManager.getOkHttpUrlMe("users", "me")
                Log.e("@@OKHTTP REST@@", targetURL.toString())

                toServer = OkHttpManager.getOkHttpClient()
                val request: Request = Request.Builder()
                    .url(targetURL)
                    .addHeader("token", USR_TOKEN)
                    .build()
                toServer.newCall(request).execute().also{ response = it }

                if (response.isSuccessful) {
                    val gson = Gson()


                    val meModel = gson.fromJson(response.body!!.string(), MeModel::class.java)
                    uname = meModel.name
                    runOnUiThread {
                        binding.meTV.text = "${uname} 님 어서오쇼!"
                    }

                    Log.e("@@@OKHTTP@@@", "_done_")
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "ME 도중 HTTP 문제 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "ME 도중 예외발생 $e", Toast.LENGTH_SHORT).show()
                }
                Log.e("@@@OKHTTP-REST-ERROR@@@", e.toString())
            }
            runOnUiThread {
                binding.progressbar.visibility = View.GONE
            }
        }.start()
    }

    private fun todoServerSend() {
        val progressDialog = ProgressDialog.show(
            this, "서버입력중", "잠시만 기다려 주시오", true
        )
        Thread {
            val flag: Boolean
            lateinit var response: Response
            val toServer: OkHttpClient

            try {
                toServer = OkHttpManager.getOkHttpClient()

                // 요청 form 세팅
            } catch (e: Exception) {

            }
        }
    }

    private fun getTodos() {
        binding.progressbar.visibility = View.VISIBLE
        Thread {
            val toServer: OkHttpClient
            lateinit var response: Response
            try {
                // URL Setting. GET 방식에선 이런식으로 세팅한다
                val targetURL: HttpUrl = OkHttpManager.getOkHttpUrl("todos")
                Log.e("@@OKHTTP REST@@", targetURL.toString())

                toServer = OkHttpManager.getOkHttpClient()
                val request: Request = Request.Builder()
                    .url(targetURL)
                    .addHeader("token", USR_TOKEN)
                    .build()
                toServer.newCall(request).execute().also{ response = it }
                Log.e("@@@OKHTTP@@@", "_rest executed! ${GET_TODOS}_")

                if (response.isSuccessful) {
                    Log.e("@@@OKHTTP@@@", "_successful response_")
                    val gson = Gson()
                    val todoModel = gson.fromJson(response.body!!.string(), ToDoModel::class.java)
                    val todoList = todoModel.todos
                    Log.e("@@_Printing_todoList_@@", todoList.toString())
                    runOnUiThread {
                        with (binding.todoRV) {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ToDoRecyclerAdapter(todoList, this@ToDoOkHttpRESTActivity)
                        }
                    }

                    Log.e("@@@OKHTTP@@@", "_done_")
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "HTTP 문제 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "예외발생 $e", Toast.LENGTH_SHORT).show()
                }
                Log.e("@@@OKHTTP-REST-ERROR@@@", e.toString())
            }
            runOnUiThread {
                binding.progressbar.visibility = View.GONE
            }
        }.start()
    }
}