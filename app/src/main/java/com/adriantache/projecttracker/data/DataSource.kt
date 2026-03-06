package com.adriantache.projecttracker.data

import com.adriantache.projecttracker.domain.entity.Project

interface DataSource {
    suspend fun getProjects(): Result<List<Project>>
    suspend fun saveProject(newProject: Project): Result<Unit>
    suspend fun deleteProject(projectId: String): Result<Unit>
}
