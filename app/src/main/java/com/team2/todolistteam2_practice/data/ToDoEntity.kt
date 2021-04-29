package com.team2.todolistteam2_practice.data

data class ToDoEntity(
    val id: Int = -1,
    var todo: String = "",
    var completed: Boolean = false
)