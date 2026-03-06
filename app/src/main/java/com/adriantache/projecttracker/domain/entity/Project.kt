package com.adriantache.projecttracker.domain.entity

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: Category = Category.All,
    val tasks: Map<String, Task> = emptyMap(),
) {
    val isValid = name.isNotBlank()

    val isDone: Boolean
        get() = tasks.values.all { it.isDone }

    fun setName(name: String) = this.copy(name = name)
    fun setDescription(description: String) = this.copy(description = description)
    fun addTask(task: Task) = this.copy(tasks = this.tasks + task.toPair())
    fun removeTask(taskId: Task) = this.copy(tasks = this.tasks.toMutableMap().apply { remove(taskId.id) })

    fun toPair() = Pair(id, this)
}

