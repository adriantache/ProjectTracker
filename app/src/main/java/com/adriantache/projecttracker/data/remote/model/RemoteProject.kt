package com.adriantache.projecttracker.data.remote.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class RemoteProject(
    @get:PropertyName("id")
    val id: String = "",
    @get:PropertyName("name")
    val name: String = "",
    @get:PropertyName("description")
    val description: String = "",
    @get:PropertyName("category")
    val category: RemoteCategory = RemoteCategory(),
    @get:PropertyName("tasks")
    val tasks: Map<String, RemoteTask> = emptyMap(),
    @get:PropertyName("timestamp")
    val timestamp: String = "",
    @get:PropertyName("isFavorite")
    val isFavorite: Boolean = false,
    @get:PropertyName("completionTimestamp")
    val completionTimestamp: Long? = null,
)
