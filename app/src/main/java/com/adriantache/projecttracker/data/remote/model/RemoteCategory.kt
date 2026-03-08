package com.adriantache.projecttracker.data.remote.model

import androidx.annotation.Keep
import com.google.firebase.database.PropertyName

@Keep
data class RemoteCategory(
    @get:PropertyName("id")
    val id: String = "",
    @get:PropertyName("name")
    val name: String = "",
    @get:PropertyName("description")
    val description: String = "",
    @get:PropertyName("timestamp")
    val timestamp: String = "",
)
