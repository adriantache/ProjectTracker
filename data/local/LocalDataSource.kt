package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.DataSource
import com.adriantache.projecttracker.data.local.dao.CategoryDao
import com.adriantache.projecttracker.data.local.dao.ProjectDao
import com.adriantache.projecttracker.data.local.dao.TaskDao
import com.adriantache.projecttracker.domain.entity.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao,
) : DataSource {
    fun getProjectsFlow(): Flow<List<Project>> =
        projectDao.getProjectsWithTasksFlow().map { list ->
            list.map { it.toProject() }
        }

    override suspend fun getProjects(): Result<List<Project>> = runCatching {
        projectDao.getProjectsWithTasks().map { it.toProject() }
    }

    override suspend fun saveProject(newProject: Project): Result<Unit> = runCatching {
        categoryDao.insertCategory(newProject.category.toEntity())

        projectDao.insertProject(newProject.toEntity())
        newProject.tasks.values.forEach { task ->
            taskDao.insertTask(task.toEntity(newProject.id))
        }
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        projectDao.deleteProject(projectId)
    }
}
