package com.adriantache.projecttracker.domain.entity

import java.time.ZonedDateTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
) {
    val isValid = title.isNotBlank()

    fun setTitle(title: String) = this.copy(title = title)
    fun setDescription(description: String) = this.copy(description = description)
    fun setDone(isDone: Boolean) = this.copy(isDone = isDone)

    fun toPair() = Pair(id, this)
}
