package com.adriantache.projecttracker.domain.entity

import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
) {
    fun setName(name: String) = this.copy(name = name)
    fun setDescription(description: String) = this.copy(description = description)

    companion object {
        val All = Category(name = "All", description = "All categories")
    }
}
