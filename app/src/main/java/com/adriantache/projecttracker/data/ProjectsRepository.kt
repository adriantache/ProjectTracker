package com.adriantache.projecttracker.data

import android.util.Log
import com.adriantache.projecttracker.data.local.LocalDataSource
import com.adriantache.projecttracker.data.remote.RemoteDataSource
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Project
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectsRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : ProjectsRepositoryInterface {
    override fun getProjectsFlow(): Flow<List<Project>> =
        localDataSource.getProjectsFlow()

    override suspend fun fetchProjects(): Result<Unit> =
        remoteDataSource.getProjectsWithTimestamps()
            .onSuccess { remoteProjects ->
                localDataSource.syncProjects(remoteProjects)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error syncing projects to local database", it)
                    }
            }.map { }

    override suspend fun saveProject(newProject: Project): Result<Unit> {
        val timestamp = System.currentTimeMillis()
        return localDataSource.saveProject(newProject, timestamp)
            .onSuccess {
                remoteDataSource.saveProject(newProject, timestamp)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error saving project to remote", it)
                    }
            }
    }

    override suspend fun saveProjects(projects: List<Project>): Result<Unit> = runCatching {
        val timestamp = System.currentTimeMillis()
        localDataSource.saveProjects(projects, timestamp).getOrThrow()
        remoteDataSource.saveProjects(projects, timestamp).getOrThrow()
    }

    override suspend fun completeProject(projectId: String): Result<Unit> {
        val completionTimestamp = System.currentTimeMillis()
        return localDataSource.completeProject(projectId, completionTimestamp)
            .onSuccess { updatedProject ->
                remoteDataSource.saveProject(updatedProject, completionTimestamp)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error saving completed project to remote", it)
                    }
            }.map { }
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> =
        localDataSource.deleteProject(projectId)
            .onSuccess {
                remoteDataSource.deleteProject(projectId)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error deleting project from remote", it)
                    }
            }

    override suspend fun toggleFavorite(projectId: String): Result<Unit> =
        localDataSource.toggleFavorite(projectId)
            .onSuccess { updatedProject ->
                remoteDataSource.saveProject(updatedProject, System.currentTimeMillis())
                    .onFailure {
                        Log.e("ProjectsRepository", "Error saving favorited project to remote", it)
                    }
            }.map { }
}
