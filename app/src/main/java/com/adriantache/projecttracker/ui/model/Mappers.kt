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

fun Project.toUi(): ProjectUi = ProjectUi(
    id = id,
    name = name,
    description = description,
    categoryName = category.name,
    tasksText = "${tasks.values.count { it.isDone }}/${tasks.size}",
    tasks = tasks.values.sortedByDescending { it.timestamp }.map { it.toUi() },
    isCompleted = isCompleted,
    canBeCompleted = !isCompleted && isDone && tasks.isNotEmpty()
)

fun Task.toUi(): TaskUi = TaskUi(
    id = id,
    title = title,
    description = description,
    isDone = isDone
)
