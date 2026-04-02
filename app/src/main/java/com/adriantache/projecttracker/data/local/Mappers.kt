package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.local.entity.CategoryEntity
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.ProjectWithTasks
import com.adriantache.projecttracker.data.local.entity.TaskEntity
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Project.toEntity(lastUpdated: Long = System.currentTimeMillis()) = ProjectEntity(
    id = id,
    name = name,
    description = description,
    categoryId = category.id,
    lastUpdated = lastUpdated,
    isFavorite = isFavorite,
    completionTimestamp = completionTimestamp,
)

fun Category.toEntity(lastUpdated: Long = System.currentTimeMillis()) = CategoryEntity(
    id = id,
    name = name,
    description = description,
    lastUpdated = lastUpdated,
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
    isFavorite = project.isFavorite,
    completionTimestamp = project.completionTimestamp,
)

fun Task.toEntity(projectId: String, lastUpdated: Long = System.currentTimeMillis()) = TaskEntity(
    id = id,
    projectId = projectId,
    name = title,
    description = description,
    isDone = isDone,
    timestamp = timestamp.format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    lastUpdated = lastUpdated,
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = name,
    description = description,
    isDone = isDone,
    timestamp = ZonedDateTime.parse(timestamp),
)
