package com.adriantache.projecttracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.adriantache.projecttracker.data.local.entity.TaskEntity

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Transaction
    suspend fun upsertTask(task: TaskEntity) {
        val id = insertTask(task)
        if (id == -1L) {
            updateTask(task)
        }
    }
}
