package com.adriantache.projecttracker.domain.entity

import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val sortOrder: Int = 0,
) {
    fun setName(name: String) = this.copy(name = name)
    fun setDescription(description: String) = this.copy(description = description)
    fun setSortOrder(sortOrder: Int) = this.copy(sortOrder = sortOrder)

    companion object {
        val All = Category(name = "All", description = "All categories", sortOrder = -1)
    }
}
