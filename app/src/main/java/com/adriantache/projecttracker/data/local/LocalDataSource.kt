package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.DataSource
import com.adriantache.projecttracker.data.local.dao.ProjectDao
import com.adriantache.projecttracker.data.local.dao.TaskDao
import com.adriantache.projecttracker.domain.entity.Project

class LocalDataSource(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
) : DataSource {
    override suspend fun getProjects(): Result<List<Project>> = runCatching {
        projectDao.getProjectsWithTasks().map { it.toProject() }
    }

    override suspend fun saveProject(newProject: Project): Result<Unit> = runCatching {
        projectDao.insertProject(newProject.toEntity())
        newProject.tasks.values.forEach { task ->
            taskDao.insertTask(task.toEntity(newProject.id))
        }
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        projectDao.deleteProject(projectId)
    }
}
