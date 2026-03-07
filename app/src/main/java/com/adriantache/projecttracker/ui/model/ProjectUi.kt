package com.adriantache.projecttracker.ui.model

data class ProjectUi(
    val id: String,
    val name: String,
    val description: String,
    val categoryName: String,
    val tasksText: String,
    val tasks: List<TaskUi>,
)
