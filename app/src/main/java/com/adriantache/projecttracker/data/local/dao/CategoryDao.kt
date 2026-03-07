package com.adriantache.projecttracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.adriantache.projecttracker.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Transaction
    suspend fun upsertCategory(category: CategoryEntity) {
        val id = insertCategory(category)
        if (id == -1L) {
            updateCategory(category)
        }
    }

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}
