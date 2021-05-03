package com.team2.todolistteam2_practice.okhttp

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.team2.todolistteam2_practice.R
import com.team2.todolistteam2_practice.common.*
import com.team2.todolistteam2_practice.data.MeModel
import com.team2.todolistteam2_practice.data.ToDoEntity
import com.team2.todolistteam2_practice.data.ToDoModel
import com.team2.todolistteam2_practice.data.ToDoUpdateModel
import com.team2.todolistteam2_practice.databinding.LayoutOkhttpRestActivityBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

class ToDoOkHttpRESTActivity: AppCompatActivity() {

    private lateinit var binding: LayoutOkhttpRestActivityBinding
    lateinit var tokenValue: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // how to getExtra? -> just use it!
        tokenValue = intent.getStringExtra("token")!!
        Log.e("[INTENT token]", "$tokenValue")

        binding = LayoutOkhttpRestActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        with (binding.todoRV) {
            layoutManager = manager
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

            val testList = mutableListOf<ToDoEntity>()
            testList.add(ToDoEntity(1, "asdfasdf", true))
            adapter = ToDoRecyclerAdapter(testList, this@ToDoOkHttpRESTActivity) {
                setOnClickListener {
//                    addOnItemTouchListener()
                }
            }

        }
        binding.fab.setOnClickListener {
            val todoDTO = ToDoEntity()

            Log.e("CREATE", "1")

            // create a dialog
            val li = LayoutInflater.from(this)
            val dialogView: View = li.inflate(R.layout.dialog_fragment, null)

            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)

            // set xml to alertdialog builder
            dialogBuilder.setView(dialogView)

            val userInput: EditText = dialogView.findViewById(R.id.dialog_task_et)

            // set dialog message
            dialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    todoDTO.todo = userInput.text.toString()

                    Log.e("TRYNA CREATE A TODO", "asdf")
                    createTodo(todoDTO.todo)
                }
                .setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int -> }

            // create alert dialog
            val alertDialog: androidx.appcompat.app.AlertDialog = dialogBuilder.create()

            // show it
            alertDialog.show()

            Log.e("CREATE", "2")
        }
        getMe()
        getTodos()
    }

    private fun createTodo(todoContent: String) {
//        binding.progressbar.visibility = View.VISIBLE

        val progressDialog = ProgressDialog.show(
            this,
            "서버 입력중", "좀 기다리라구~", true
        )

        // thread 기반으로 시작
        Thread {
            var flag: Boolean
            val toServer: OkHttpClient
            lateinit var response: Response
            try {
                // POST 방식
                toServer = OkHttpManager.getOkHttpClient()

                // 요청 Form setting
                val postBody: RequestBody = FormBody.Builder()
                    .add("todo", todoContent)
                    .build()

                // 요청 세팅 form (Query String) 방식의 포스트
                val request: Request = Request.Builder()
                    .url(TODOS_ADDRESS)
                    .post(postBody)
                    .addHeader("token", tokenValue )
                    .build()

                // 동기방식
                response = toServer.newCall(request).execute()
                flag = response.isSuccessful

                var responseJSON = ""
                if (flag) {
                    responseJSON = response.body!!.string()
                    try {
                        val jsonObject = JSONObject(responseJSON)
                        runOnUiThread {
                            Toast.makeText(this, "create todo 결과: ${jsonObject.optString("ok")}", Toast.LENGTH_SHORT).show()
                        }

                        // if succeeded, get todos again
                        getTodos()
                    } catch (json: JSONException) {
                        Log.e("ERROR", json.toString())
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "HTTP 에러 발생", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "예외 발생: $e", Toast.LENGTH_SHORT).show()
                }
            }
            runOnUiThread {
                progressDialog.dismiss()
            }
        }.start()
    }

    private fun updateTodo(holder: ToDoRecyclerAdapter.ToDoHolder, todoId: Int, todo: String) {
        if (todo.isEmpty()) {
            Toast.makeText(this, "please write down soemthing to fix", Toast.LENGTH_SHORT).show()
            return
        }
        val updateToDoDTO = ToDoUpdateModel(todoId, todo)

        Thread {
            val flag: Boolean
            lateinit var response: Response
            val toServer: OkHttpClient

            try {
                toServer = OkHttpManager.getOkHttpClient()

                // request form set up
                val putBody: RequestBody = FormBody.Builder()
                        .add("todoId", updateToDoDTO.todoId.toString())
                        .add("todo", updateToDoDTO.todo)
                        .build()

                // request setting (form, Query String type PUT)
                val request: Request = Request.Builder()
                        .url(TODOS_ADDRESS)
                        .put(putBody)
                        .addHeader("token", TOKEN)
                        .build()

                // synchronized way
                response = toServer.newCall(request).execute()
                flag = response.isSuccessful
                var responseJSON = ""

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
                        Toast.makeText(this, "HTTP error!!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "예외발생 $e", Toast.LENGTH_SHORT).show()
                    Log.e("[Exception create]", e.toString())
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
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
                    .addHeader("token", tokenValue)
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
                    .addHeader("token", tokenValue)
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
                            adapter = ToDoRecyclerAdapter(todoList, this@ToDoOkHttpRESTActivity) {
                                //
                            }
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
                    Toast.makeText(this, "get todos 예외발생 $e", Toast.LENGTH_SHORT).show()
                }
                Log.e("@@@OKHTTP-REST-ERROR@@@", e.toString())
            }
            runOnUiThread {
                binding.progressbar.visibility = View.GONE
            }
        }.start()
    }
}