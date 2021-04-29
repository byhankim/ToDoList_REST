package com.team2.todolistteam2_practice.okhttp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team2.todolistteam2_practice.R
import com.team2.todolistteam2_practice.data.ToDoEntity

class ToDoRecyclerAdapter (
    private val todoList: MutableList<ToDoEntity>,
    private val owner: Activity
): RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoHolder>() {

    inner class ToDoHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val isComplete: CheckBox = itemView.findViewById(R.id.complete_cb)
        val task: TextView = itemView.findViewById(R.id.task_tv)
        val fixBtn: TextView = itemView.findViewById(R.id.fix_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoHolder {
        val view = LayoutInflater.from(owner).inflate(R.layout.todo_recycler_item, parent, false)
        return ToDoHolder(view)
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


        ///////////////////////

        // GET/PATCH/DETETE/POST시 -> notifyDatasetChanged() 해줘야됨!
    }
    override fun getItemCount() = todoList.size
}