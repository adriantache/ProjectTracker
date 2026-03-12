package com.adriantache.projecttracker.data

import app.cash.turbine.test
import com.adriantache.projecttracker.data.local.LocalDataSource
import com.adriantache.projecttracker.data.remote.RemoteDataSource
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProjectsRepositoryTest {

    private lateinit var repository: ProjectsRepository
    private val localDataSource: LocalDataSource = mockk()
    private val remoteDataSource: RemoteDataSource = mockk()

    @Before
    fun setup() {
        repository = ProjectsRepository(localDataSource, remoteDataSource)
    }

    @Test
    fun `getProjectsFlow returns flow from local data source`() = runTest {
        val projects = listOf(Project(name = "Test Project", category = Category.All))
        every { localDataSource.getProjectsFlow() } returns flowOf(projects)

        repository.getProjectsFlow().test {
            assertEquals(projects, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `fetchProjects fetches from remote and saves to local`() = runTest {
        val projects = listOf(Project(name = "Remote Project", category = Category.All) to 123L)
        coEvery { remoteDataSource.getProjectsWithTimestamps() } returns Result.success(projects)
        coEvery { localDataSource.syncProjects(any()) } returns Result.success(Unit)

        val result = repository.fetchProjects()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteDataSource.getProjectsWithTimestamps() }
        coVerify(exactly = 1) { localDataSource.syncProjects(projects) }
    }

    @Test
    fun `fetchProjects returns failure when remote fails`() = runTest {
        val exception = Exception("Remote error")
        coEvery { remoteDataSource.getProjectsWithTimestamps() } returns Result.failure(exception)

        val result = repository.fetchProjects()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { localDataSource.syncProjects(any()) }
    }

    @Test
    fun `saveProject saves to local then remote`() = runTest {
        val project = Project(name = "New Project", category = Category.All)
        coEvery { localDataSource.saveProject(project, any()) } returns Result.success(Unit)
        coEvery { remoteDataSource.saveProject(project, any()) } returns Result.success(Unit)

        val result = repository.saveProject(project)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { localDataSource.saveProject(project, any()) }
        coVerify(exactly = 1) { remoteDataSource.saveProject(project, any()) }
    }

    @Test
    fun `saveProject returns failure when local fails`() = runTest {
        val project = Project(name = "New Project", category = Category.All)
        val exception = Exception("Local error")
        coEvery { localDataSource.saveProject(project, any()) } returns Result.failure(exception)

        val result = repository.saveProject(project)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { remoteDataSource.saveProject(any(), any()) }
    }

    @Test
    fun `deleteProject deletes from local then remote`() = runTest {
        val projectId = "123"
        coEvery { localDataSource.deleteProject(projectId) } returns Result.success(Unit)
        coEvery { remoteDataSource.deleteProject(projectId) } returns Result.success(Unit)

        val result = repository.deleteProject(projectId)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { localDataSource.deleteProject(projectId) }
        coVerify(exactly = 1) { remoteDataSource.deleteProject(projectId) }
    }

    @Test
    fun `deleteProject returns failure when local fails`() = runTest {
        val projectId = "123"
        val exception = Exception("Local error")
        coEvery { localDataSource.deleteProject(projectId) } returns Result.failure(exception)

        val result = repository.deleteProject(projectId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 0) { remoteDataSource.deleteProject(any()) }
    }
}
