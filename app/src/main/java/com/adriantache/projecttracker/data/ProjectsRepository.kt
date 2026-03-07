package com.adriantache.projecttracker.data

import android.util.Log
import com.adriantache.projecttracker.data.local.LocalDataSource
import com.adriantache.projecttracker.data.remote.RemoteDataSource
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Project
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// TODO: improve error handling
class ProjectsRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : ProjectsRepositoryInterface {
    override fun getProjectsFlow(): Flow<List<Project>> =
        localDataSource.getProjectsFlow()

    override suspend fun fetchProjects(): Result<Unit> =
        remoteDataSource.getProjects()
            .onSuccess { remoteProjects ->
                remoteProjects.forEach {
                    localDataSource.saveProject(it)
                }
            }.map { } // Convert Result<List<Project>> to Result<Unit>

    override suspend fun saveProject(newProject: Project): Result<Unit> =
        localDataSource.saveProject(newProject)
            .onSuccess {
                remoteDataSource.saveProject(newProject)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error saving project to remote", it)
                    }
            }

    override suspend fun deleteProject(projectId: String): Result<Unit> =
        localDataSource.deleteProject(projectId)
            .onSuccess {
                remoteDataSource.deleteProject(projectId)
                    .onFailure {
                        Log.e("ProjectsRepository", "Error deleting project from remote", it)
                    }
            }
}
