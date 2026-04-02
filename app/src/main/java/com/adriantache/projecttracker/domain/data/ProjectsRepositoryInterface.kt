package com.adriantache.projecttracker.domain.data

import com.adriantache.projecttracker.domain.entity.Project
import kotlinx.coroutines.flow.Flow

interface ProjectsRepositoryInterface {
    fun getProjectsFlow(): Flow<List<Project>>
    suspend fun fetchProjects(): Result<Unit>
    suspend fun saveProject(newProject: Project): Result<Unit>
    suspend fun deleteProject(projectId: String): Result<Unit>
    suspend fun completeProject(projectId: String): Result<Unit>
    suspend fun toggleFavorite(projectId: String): Result<Unit>
}
