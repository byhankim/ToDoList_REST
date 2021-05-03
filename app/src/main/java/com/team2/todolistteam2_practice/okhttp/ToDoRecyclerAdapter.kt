package com.team2.todolistteam2_practice.okhttp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.team2.todolistteam2_practice.R
import com.team2.todolistteam2_practice.common.TODOS_ADDRESS
import com.team2.todolistteam2_practice.common.TOKEN
import com.team2.todolistteam2_practice.data.ToDoEntity
import com.team2.todolistteam2_practice.data.ToDoUpdateModel
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject

class ToDoRecyclerAdapter (
    private val todoList: MutableList<ToDoEntity>,
    private val owner: Activity,
    private val itemListener: (ToDoEntity) -> Unit
): RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoHolder>() {

    inner class ToDoHolder(itemView: View, itemListener: (ToDoEntity) -> Unit): RecyclerView.ViewHolder(itemView) {
        val isComplete: CheckBox = itemView.findViewById(R.id.complete_cb)
        val task: TextView = itemView.findViewById(R.id.task_tv)
        val fixBtn: TextView = itemView.findViewById(R.id.fix_btn)
        val rmBtn: TextView = itemView.findViewById(R.id.rm_btn)

        fun bindListener(item: ToDoEntity) {
            itemView.setOnClickListener { itemListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoHolder {
        val view = LayoutInflater.from(owner).inflate(R.layout.todo_recycler_item, parent, false)
        return ToDoHolder(view, itemListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ToDoHolder, position: Int) {
        val todoEntity = todoList[position]
        with (holder) {
            task.text = todoEntity.todo
            isComplete.isChecked = todoEntity.completed
        }
        // click listener here? yes!
        holder.fixBtn.setOnClickListener {
            // create a dialog
            val li = LayoutInflater.from(owner)
            val dialogView: View = li.inflate(R.layout.dialog_fragment, null)
//            val todoUpdateDTO = ToDoUpdateModel()

            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(owner)

            // set xml to alertdialog builder
            dialogBuilder.setView(dialogView)

            val userInput: EditText = dialogView.findViewById(R.id.dialog_task_et)

            // set dialog message
            dialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(dialog: DialogInterface, id: Int) {
                        holder.task.text = userInput.text
                    }
                }
                .setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(dialog: DialogInterface, id: Int) {
                        dialog.cancel()
                    }
                }

            // create alert dialog
            val alertDialog: androidx.appcompat.app.AlertDialog = dialogBuilder.create()

            // show it
            alertDialog.show()
        }

        holder.rmBtn.setOnClickListener {
            // go on without double checking

        }


        ///////////////////////

        // GET/PATCH/DETETE/POST시 -> notifyDatasetChanged() 해줘야됨!
    }


    private fun createTodo() {

    }

    private fun removeTodo() {

    }


    override fun getItemCount() = todoList.size
}