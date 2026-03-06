package com.adriantache.projecttracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("projectId")],
)
data class TaskEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isDone: Boolean,
    val projectId: String,
)
