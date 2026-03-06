package com.adriantache.projecttracker.data

import android.util.Log
import com.adriantache.projecttracker.data.local.LocalDataSource
import com.adriantache.projecttracker.data.remote.RemoteDataSource
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Project

// TODO: improve error handling
class ProjectsRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : ProjectsRepositoryInterface {
    override suspend fun getProjects(fromRemote: Boolean): Result<List<Project>> {
        if (fromRemote) {
            remoteDataSource.getProjects()
                .onSuccess { remoteProjects ->
                    remoteProjects.forEach {
                        localDataSource.saveProject(it)
                    }
                }.onFailure {
                    Log.e("ProjectsRepository", "Error fetching projects from remote", it)
                }
        }

        return localDataSource.getProjects()
    }

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
