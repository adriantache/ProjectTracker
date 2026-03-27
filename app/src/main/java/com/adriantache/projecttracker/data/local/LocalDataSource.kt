package com.adriantache.projecttracker.data.local

import androidx.room.withTransaction
import com.adriantache.projecttracker.data.local.dao.CategoryDao
import com.adriantache.projecttracker.data.local.dao.ProjectDao
import com.adriantache.projecttracker.data.local.dao.TaskDao
import com.adriantache.projecttracker.domain.entity.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val database: AppDatabase,
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao,
) {
    fun getProjectsFlow(): Flow<List<Project>> =
        projectDao.getProjectsWithTasksFlow().map { list ->
            list.map { it.toProject() }
        }

    suspend fun saveProject(newProject: Project): Result<Unit> = saveProject(newProject, System.currentTimeMillis())

    suspend fun saveProject(newProject: Project, timestamp: Long): Result<Unit> = runCatching {
        database.withTransaction {
            val newCategory = newProject.category.toEntity(timestamp)
            categoryDao.upsertCategory(newCategory)

            projectDao.upsertProject(newProject.toEntity(timestamp))

            // Reconcile tasks for this project
            val taskIds = newProject.tasks.keys.toList()
            taskDao.deleteTasksForProjectExcept(newProject.id, taskIds)

            newProject.tasks.values.forEach { task ->
                taskDao.upsertTask(task.toEntity(newProject.id, timestamp))
            }
        }
    }

    suspend fun completeProject(projectId: String, completionTimestamp: Long): Result<Project> = runCatching {
        database.withTransaction {
            val projectWithTasks = projectDao.getProjectWithTasks(projectId) ?: throw Exception("Project not found")
            val updatedProjectEntity = projectWithTasks.project.copy(
                completionTimestamp = completionTimestamp,
                lastUpdated = completionTimestamp
            )
            projectDao.upsertProject(updatedProjectEntity)
            projectWithTasks.copy(project = updatedProjectEntity).toProject()
        }
    }

    suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        database.withTransaction {
            taskDao.deleteTasksForProject(projectId)
            projectDao.deleteProject(projectId)
        }
    }

    /**
     * Reconciles the local database with the remote projects list.
     * Deletes projects and tasks that are not present in the remote list.
     * Only updates if the remote timestamp is newer.
     */
    suspend fun syncProjects(remoteProjects: List<Pair<Project, Long>>): Result<Unit> = runCatching {
        database.withTransaction {
            // 1. Delete projects not in remote
            val remoteProjectIds = remoteProjects.map { it.first.id }
            projectDao.deleteProjectsExcept(remoteProjectIds)

            // 2. Update/Insert remote projects if newer
            remoteProjects.forEach { (project, remoteTimestamp) ->
                val localTimestamp = projectDao.getProject(project.id)?.lastUpdated

                if (localTimestamp == null || remoteTimestamp > localTimestamp) {
                    // Upsert category (if newer)
                    val localCategory = categoryDao.getCategory(project.category.id)
                    if (localCategory == null || remoteTimestamp > localCategory.lastUpdated) {
                        categoryDao.upsertCategory(project.category.toEntity(remoteTimestamp))
                    }

                    // Upsert project
                    projectDao.upsertProject(project.toEntity(remoteTimestamp))

                    // Reconcile tasks for this project
                    val remoteTaskIds = project.tasks.keys.toList()
                    taskDao.deleteTasksForProjectExcept(project.id, remoteTaskIds)

                    project.tasks.values.forEach { task ->
                        val localTask = taskDao.getTask(task.id)
                        if (localTask == null || remoteTimestamp > localTask.lastUpdated) {
                            taskDao.upsertTask(task.toEntity(project.id, remoteTimestamp))
                        }
                    }
                }
            }
        }
    }
}
