package com.adriantache.projecttracker.domain.data

import com.adriantache.projecttracker.domain.entity.Project

interface ProjectsRepositoryInterface {
    suspend fun getProjects(): List<Project>
    suspend fun saveProject(newProject: Project)
    suspend fun deleteProject(projectId: String)
}
