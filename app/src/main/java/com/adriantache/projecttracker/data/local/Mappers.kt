package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.ProjectWithTasks
import com.adriantache.projecttracker.data.local.entity.TaskEntity
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task

fun Project.toEntity() = ProjectEntity(
    id = id,
    name = name,
    description = description,
    categoryId = category.id,
)

// This is a placeholder until we have a proper way to get a category from an id.
private val placeholderCategory = Category.All

fun ProjectWithTasks.toProject() = Project(
    id = project.id,
    name = project.name,
    description = project.description,
    category = placeholderCategory,
    tasks = tasks.map { it.toTask() }.associateBy { it.id },
)

fun Task.toEntity(projectId: String) = TaskEntity(
    id = id,
    projectId = projectId,
    name = title,
    isDone = isDone,
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = name,
    isDone = isDone,
)
