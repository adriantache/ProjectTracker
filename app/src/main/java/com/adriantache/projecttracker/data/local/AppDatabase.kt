package com.adriantache.projecttracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adriantache.projecttracker.data.local.dao.CategoryDao
import com.adriantache.projecttracker.data.local.dao.ProjectDao
import com.adriantache.projecttracker.data.local.dao.TaskDao
import com.adriantache.projecttracker.data.local.entity.CategoryEntity
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.TaskEntity

@Database(entities = [ProjectEntity::class, CategoryEntity::class, TaskEntity::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "project_tracker_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
