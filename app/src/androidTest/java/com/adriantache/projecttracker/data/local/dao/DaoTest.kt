package com.adriantache.projecttracker.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adriantache.projecttracker.data.local.AppDatabase
import com.adriantache.projecttracker.data.local.entity.CategoryEntity
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DaoTest {
    private lateinit var db: AppDatabase
    private lateinit var projectDao: ProjectDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var taskDao: TaskDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        projectDao = db.projectDao()
        categoryDao = db.categoryDao()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addProjectWithNewCategory() = runBlocking {
        // Case: adding a project with a new category
        val category = CategoryEntity("cat_new", "New Category", "Description New")
        categoryDao.insertCategory(category)

        val project = ProjectEntity("proj1", "Project 1", "Description 1", "cat_new")
        projectDao.insertProject(project)

        val results = projectDao.getProjectsWithTasksFlow().first()
        assertEquals(1, results.size)
        assertEquals("cat_new", results[0].category.id)
        assertEquals("New Category", results[0].category.name)
    }

    @Test
    fun addProjectWithExistingCategory() = runBlocking {
        // Setup: existing category
        val category = CategoryEntity("cat_existing", "Existing Category", "Description")
        categoryDao.insertCategory(category)

        // Case: adding a project with an existing category
        val project = ProjectEntity("proj2", "Project 2", "Description 2", "cat_existing")
        projectDao.insertProject(project)

        val results = projectDao.getProjectsWithTasksFlow().first()
        val resultProject = results.find { it.project.id == "proj2" }
        assertTrue(resultProject != null)
        assertEquals("cat_existing", resultProject?.category?.id)
    }

    @Test
    fun editProject() = runBlocking {
        val category = CategoryEntity("cat1", "Category", "Desc")
        categoryDao.insertCategory(category)
        val project = ProjectEntity("proj1", "Original Name", "Original Desc", "cat1")
        projectDao.insertProject(project)

        // Case: editing a project
        val updatedProject = project.copy(name = "Updated Name", description = "Updated Desc")
        projectDao.updateProject(updatedProject)

        val results = projectDao.getProjectsWithTasksFlow().first()
        assertEquals("Updated Name", results[0].project.name)
        assertEquals("Updated Desc", results[0].project.description)
    }

    @Test
    fun addTaskToProject() = runBlocking {
        val category = CategoryEntity("cat1", "Category", "Desc")
        categoryDao.insertCategory(category)
        val project = ProjectEntity("proj1", "Project 1", "Desc", "cat1")
        projectDao.insertProject(project)

        // Case: adding a task to a project
        val task = TaskEntity("task1", "Task 1", false, "proj1")
        taskDao.insertTask(task)

        val results = projectDao.getProjectsWithTasksFlow().first()
        assertEquals(1, results[0].tasks.size)
        assertEquals("Task 1", results[0].tasks[0].name)
        assertEquals("proj1", results[0].tasks[0].projectId)
    }

    @Test
    fun markTaskAsDone() = runBlocking {
        val category = CategoryEntity("cat1", "Category", "Desc")
        categoryDao.insertCategory(category)
        val project = ProjectEntity("proj1", "Project 1", "Desc", "cat1")
        projectDao.insertProject(project)
        val task = TaskEntity("task1", "Incomplete Task", false, "proj1")
        taskDao.insertTask(task)

        // Verify initial state
        var results = projectDao.getProjectsWithTasksFlow().first()
        assertFalse(results[0].tasks[0].isDone)

        // Case: marking a task as done
        val doneTask = task.copy(isDone = true)
        taskDao.updateTask(doneTask)

        results = projectDao.getProjectsWithTasksFlow().first()
        assertTrue(results[0].tasks[0].isDone)
    }

    @Test
    fun deleteProjectRemovesProjectAndTasks() = runBlocking {
        val category = CategoryEntity("cat1", "Category", "Desc")
        categoryDao.insertCategory(category)
        val project = ProjectEntity("proj1", "Project 1", "Desc", "cat1")
        projectDao.insertProject(project)
        taskDao.insertTask(TaskEntity("task1", "Task 1", false, "proj1"))

        projectDao.deleteProject("proj1")

        val results = projectDao.getProjectsWithTasksFlow().first()
        assertTrue(results.isEmpty())
    }
}
