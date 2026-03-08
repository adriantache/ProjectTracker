package com.adriantache.projecttracker.data.remote.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class RemoteTask(
    @get:PropertyName("id")
    val id: String = "",
    @get:PropertyName("title")
    val title: String = "",
    @get:PropertyName("description")
    val description: String = "",
    @get:PropertyName("isDone")
    val isDone: Boolean = false,
    @get:PropertyName("timestamp")
    val timestamp: String = "",
)
