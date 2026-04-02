package com.adriantache.projecttracker.ui.model

import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task

fun Category.toUi(numProjects: Int): CategoryUi = CategoryUi(
    id = id,
    name = name,
    description = description,
    numProjects = numProjects
)

fun Project.toUi(): ProjectUi {
    val doneTasks = tasks.values.count { it.isDone }
    val totalTasks = tasks.size
    val progress = if (totalTasks > 0) doneTasks.toFloat() / totalTasks else 0f

    return ProjectUi(
        id = id,
        name = name,
        description = description,
        category = category,
        tasksText = "$doneTasks/$totalTasks",
        tasks = tasks.values.sortedByDescending { it.timestamp }.map { it.toUi() },
        progress = progress,
        isCompleted = isCompleted,
        canBeCompleted = !isCompleted && isDone && tasks.isNotEmpty(),
        isFavorite = isFavorite,
    )
}

fun Task.toUi(): TaskUi = TaskUi(
    id = id,
    title = title,
    description = description,
    isDone = isDone
)
