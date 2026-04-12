package com.adriantache.projecttracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val categoryId: String,
    val lastUpdated: Long = 0L,
    val isFavorite: Boolean = false,
    val completionTimestamp: Long? = null,
    val sortOrder: Int = 0,
)

data class ProjectWithTasks(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "projectId"
    )
    val tasks: List<TaskEntity>,
)
