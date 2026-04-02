package com.adriantache.projecttracker.ui.model

import com.adriantache.projecttracker.domain.entity.Category

data class ProjectUi(
    val id: String,
    val name: String,
    val description: String,
    val category: Category,
    val tasksText: String,
    val tasks: List<TaskUi>,
    val progress: Float,
    val isCompleted: Boolean,
    val canBeCompleted: Boolean,
    val isFavorite: Boolean,
)
