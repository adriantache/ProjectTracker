package com.adriantache.projecttracker.data.remote

import com.adriantache.projecttracker.data.remote.model.RemoteCategory
import com.adriantache.projecttracker.data.remote.model.RemoteProject
import com.adriantache.projecttracker.data.remote.model.RemoteTask
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    database: FirebaseDatabase,
    private val auth: FirebaseAuth,
) {
    private val rootRef: DatabaseReference = database.reference

    private val userProjectsRef: DatabaseReference
        get() = auth.currentUser?.uid?.let { uid ->
            rootRef.child("users").child(uid).child("projects")
        } ?: throw Exception("User not logged in")

    suspend fun getProjects(): Result<List<Project>> = runCatching {
        getProjectsWithTimestamps().getOrThrow().map { it.first }
    }

    suspend fun getProjectsWithTimestamps(): Result<List<Pair<Project, Long>>> = runCatching {
        userProjectsRef.get().await().children.mapNotNull { snapshot ->
            val remoteProject = snapshot.getValue(RemoteProject::class.java)
            remoteProject?.let {
                val timestampLong = try {
                    ZonedDateTime.parse(it.timestamp).toInstant().toEpochMilli()
                } catch (_: Exception) {
                    0L
                }
                it.toProject() to timestampLong
            }
        }
    }

    suspend fun saveProject(newProject: Project, lastUpdated: Long): Result<Unit> = runCatching {
        val isoTimestamp = Instant.ofEpochMilli(lastUpdated)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

        val remoteProject = RemoteProject(
            id = newProject.id,
            name = newProject.name,
            description = newProject.description,
            category = RemoteCategory(
                id = newProject.category.id,
                name = newProject.category.name,
                description = newProject.category.description,
                timestamp = isoTimestamp
            ),
            tasks = newProject.tasks.mapValues { (_, task) ->
                RemoteTask(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    isDone = task.isDone,
                    timestamp = task.timestamp.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                )
            },
            timestamp = isoTimestamp
        )
        userProjectsRef.child(newProject.id).setValue(remoteProject).await()
    }

    suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        userProjectsRef.child(projectId).removeValue().await()
    }

    private fun RemoteProject.toProject(): Project {
        return Project(
            id = id,
            name = name,
            description = description,
            category = Category(id = category.id, name = category.name, description = category.description),
            tasks = tasks.mapValues { (_, task) ->
                Task(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    isDone = task.isDone,
                    timestamp = try {
                        ZonedDateTime.parse(task.timestamp)
                    } catch (_: Exception) {
                        ZonedDateTime.now()
                    }
                )
            }
        )
    }
}
