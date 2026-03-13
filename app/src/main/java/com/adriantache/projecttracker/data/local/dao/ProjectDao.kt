package com.adriantache.projecttracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.ProjectWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Transaction
    suspend fun upsertProject(project: ProjectEntity) {
        val id = insertProject(project)
        if (id == -1L) {
            updateProject(project)
        }
    }

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProject(projectId: String): ProjectEntity?

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectWithTasks(projectId: String): ProjectWithTasks?

    @Transaction
    @Query("SELECT * FROM projects")
    fun getProjectsWithTasksFlow(): Flow<List<ProjectWithTasks>>

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    @Query("DELETE FROM projects WHERE id NOT IN (:projectIds)")
    suspend fun deleteProjectsExcept(projectIds: List<String>)
}
