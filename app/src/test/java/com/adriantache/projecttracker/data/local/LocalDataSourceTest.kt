package com.adriantache.projecttracker.data.local

import androidx.room.withTransaction
import app.cash.turbine.test
import com.adriantache.projecttracker.data.local.dao.CategoryDao
import com.adriantache.projecttracker.data.local.dao.ProjectDao
import com.adriantache.projecttracker.data.local.dao.TaskDao
import com.adriantache.projecttracker.data.local.entity.CategoryEntity
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.ProjectWithTasks
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSource
    private val database: AppDatabase = mockk()
    private val projectDao: ProjectDao = mockk()
    private val taskDao: TaskDao = mockk()
    private val categoryDao: CategoryDao = mockk()

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")
        localDataSource = LocalDataSource(database, projectDao, taskDao, categoryDao)
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `getProjectsFlow maps entities to domain models`() = runTest {
        val categoryEntity = CategoryEntity("cat1", "Category 1", "Desc")
        val projectEntity = ProjectEntity("p1", "Project 1", "Desc", "cat1")
        val projectWithTasks = ProjectWithTasks(
            project = projectEntity,
            category = categoryEntity,
            tasks = emptyList()
        )

        every { projectDao.getProjectsWithTasksFlow() } returns flowOf(listOf(projectWithTasks))

        localDataSource.getProjectsFlow().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("p1", result[0].id)
            assertEquals("Project 1", result[0].name)
            assertEquals("cat1", result[0].category.id)
            awaitComplete()
        }
    }

    @Test
    fun `saveProject upserts category, project and tasks`() = runTest {
        val project = Project(
            id = "p1",
            name = "Project 1",
            category = Category(id = "cat1", name = "Cat", description = "Desc"),
            tasks = mapOf("t1" to Task(id = "t1", title = "Task 1"))
        )

        // Mock database.withTransaction to just execute the block
        coEvery { database.withTransaction<Unit>(any()) } coAnswers {
            val block = secondArg<suspend () -> Unit>()
            block()
        }
        coEvery { categoryDao.upsertCategory(any()) } returns Unit
        coEvery { projectDao.upsertProject(any()) } returns Unit
        coEvery { taskDao.upsertTask(any()) } returns Unit

        val result = localDataSource.saveProject(project)

        assert(result.isSuccess)
        coVerify(exactly = 1) { categoryDao.upsertCategory(any()) }
        coVerify(exactly = 1) { projectDao.upsertProject(any()) }
        coVerify(exactly = 1) { taskDao.upsertTask(any()) }
    }

    @Test
    fun `deleteProject calls projectDao delete`() = runTest {
        val projectId = "p1"
        coEvery { database.withTransaction<Unit>(any()) } coAnswers {
            val block = secondArg<suspend () -> Unit>()
            block()
        }
        coEvery { taskDao.deleteTasksForProject(projectId) } returns Unit
        coEvery { projectDao.deleteProject(projectId) } returns Unit

        val result = localDataSource.deleteProject(projectId)

        assert(result.isSuccess)
        coVerify(exactly = 1) { taskDao.deleteTasksForProject(projectId) }
        coVerify(exactly = 1) { projectDao.deleteProject(projectId) }
    }
}
