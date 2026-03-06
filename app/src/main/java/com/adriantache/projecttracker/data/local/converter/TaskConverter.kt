package com.adriantache.projecttracker.data.local.converter

import androidx.room.TypeConverter
import com.adriantache.projecttracker.domain.entity.Task
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

@Serializable
private data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val isDone: Boolean,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val timestamp: ZonedDateTime,
)

class TaskConverter {
    @TypeConverter
    fun fromString(value: String): Map<String, Task> {
        val dtoMap = Json.decodeFromString<Map<String, TaskDto>>(value)
        return dtoMap.mapValues { (_, dto) ->
            Task(
                id = dto.id,
                title = dto.title,
                description = dto.description,
                isDone = dto.isDone,
                timestamp = dto.timestamp
            )
        }
    }

    @TypeConverter
    fun fromMap(map: Map<String, Task>): String {
        val dtoMap = map.mapValues { (_, task) ->
            TaskDto(
                id = task.id,
                title = task.title,
                description = task.description,
                isDone = task.isDone,
                timestamp = task.timestamp
            )
        }
        return Json.encodeToString(dtoMap)
    }
}
