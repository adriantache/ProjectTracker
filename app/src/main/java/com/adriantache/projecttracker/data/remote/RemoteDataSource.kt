package com.adriantache.projecttracker.data.remote

import com.adriantache.projecttracker.data.DataSource
import com.adriantache.projecttracker.domain.entity.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RemoteDataSource : DataSource {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val userProjectsRef: DatabaseReference
        get() = auth.currentUser?.uid?.let { uid ->
            database.child("users").child(uid).child("projects")
        } ?: throw Exception("User not logged in")

    override suspend fun getProjects(): Result<List<Project>> = runCatching {
        userProjectsRef.get().await().children.mapNotNull {
            it.getValue(Project::class.java)
        }
    }

    override suspend fun saveProject(newProject: Project): Result<Unit> = runCatching {
        userProjectsRef.child(newProject.id).setValue(newProject).await()
    }

    override suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        userProjectsRef.child(projectId).removeValue().await()
    }
}
