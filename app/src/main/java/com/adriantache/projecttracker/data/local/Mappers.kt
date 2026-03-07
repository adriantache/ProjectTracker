package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.local.entity.CategoryEntity
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

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    description = description,
)

fun CategoryEntity.toCategory() = Category(
    id = id,
    name = name,
    description = description,
)

fun ProjectWithTasks.toProject() = Project(
    id = project.id,
    name = project.name,
    description = project.description,
    category = category.toCategory(),
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
