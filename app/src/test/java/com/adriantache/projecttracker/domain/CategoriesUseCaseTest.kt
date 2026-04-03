package com.adriantache.projecttracker.domain

import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import com.adriantache.projecttracker.domain.state.ProjectState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesUseCaseTest {

    private val repository: ProjectsRepositoryInterface = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var useCase: CategoriesUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any<String>(), any<String>()) } returns 0
        every { android.util.Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { android.util.Log.w(any<String>(), any<String>()) } returns 0

        useCase = CategoriesUseCase(repository, testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic("android.util.Log")
    }

    @Test
    fun `initial state is Init`() = runTest {
        assertTrue(useCase.state.value is ProjectState.Init)
    }

    @Test
    fun `onInit transitions to DashboardView when data is received`() = runTest {
        val projects = listOf(Project(id = "p1", name = "Project 1", category = Category(id = "c1", name = "Cat 1", description = "Desc")))
        every { repository.getProjectsFlow() } returns flowOf(projects)
        coEvery { repository.fetchProjects() } returns Result.success(Unit)

        val initState = useCase.state.value as ProjectState.Init
        initState.onInit()

        // Advance time to skip delays and allow coroutines to run
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(useCase.state.value is ProjectState.DashboardView)
        assertEquals(1, (useCase.state.value as ProjectState.DashboardView).totalProjects)
    }

    @Test
    fun `showCategories transitions state to CategoryView`() = runTest {
        val projects = listOf(Project(id = "p1", name = "Project 1", category = Category(id = "c1", name = "Cat 1", description = "Desc")))
        every { repository.getProjectsFlow() } returns flowOf(projects)
        coEvery { repository.fetchProjects() } returns Result.success(Unit)

        (useCase.state.value as ProjectState.Init).onInit()
        testDispatcher.scheduler.advanceUntilIdle()

        val dashboard = useCase.state.value as ProjectState.DashboardView
        dashboard.onViewAllCategories()

        assertTrue(useCase.state.value is ProjectState.CategoryView)
        val categoryView = useCase.state.value as ProjectState.CategoryView
        assertEquals(1, categoryView.categories.size)
        assertEquals("Cat 1", categoryView.categories[0].name)
    }

    @Test
    fun `onCategorySelected transitions state to ProjectsView`() = runTest {
        val category = Category(id = "c1", name = "Cat 1", description = "Desc")
        val projects = listOf(Project(id = "p1", name = "Project 1", category = category))
        every { repository.getProjectsFlow() } returns flowOf(projects)
        coEvery { repository.fetchProjects() } returns Result.success(Unit)

        (useCase.state.value as ProjectState.Init).onInit()
        testDispatcher.scheduler.advanceUntilIdle()

        val dashboard = useCase.state.value as ProjectState.DashboardView
        dashboard.onCategorySelected("c1")

        assertTrue(useCase.state.value is ProjectState.ProjectsView)
        val projectsView = useCase.state.value as ProjectState.ProjectsView
        assertEquals("Cat 1", projectsView.category.name)
        assertEquals(1, projectsView.pendingProjects.size)
    }

    @Test
    fun `onProjectSelected transitions state to TasksView`() = runTest {
        val project = Project(id = "p1", name = "Project 1", category = Category(id = "c1", name = "Cat 1", description = "Desc"))
        every { repository.getProjectsFlow() } returns flowOf(listOf(project))
        coEvery { repository.fetchProjects() } returns Result.success(Unit)

        (useCase.state.value as ProjectState.Init).onInit()
        testDispatcher.scheduler.advanceUntilIdle()

        val dashboard = useCase.state.value as ProjectState.DashboardView
        dashboard.onProjectSelected("p1")

        assertTrue(useCase.state.value is ProjectState.TasksView)
        val tasksView = useCase.state.value as ProjectState.TasksView
        assertEquals("Project 1", tasksView.project.name)
    }

    @Test
    fun `onToggleFavorite calls repository`() = runTest {
        val project = Project(id = "p1", name = "Project 1", category = Category(id = "c1", name = "Cat 1", description = "Desc"))
        every { repository.getProjectsFlow() } returns flowOf(listOf(project))
        coEvery { repository.fetchProjects() } returns Result.success(Unit)
        coEvery { repository.toggleFavorite("p1") } returns Result.success(Unit)

        (useCase.state.value as ProjectState.Init).onInit()
        testDispatcher.scheduler.advanceUntilIdle()

        val dashboard = useCase.state.value as ProjectState.DashboardView
        dashboard.onToggleFavorite("p1")

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.toggleFavorite("p1") }
    }

    @Test
    fun `onAddTask calls repository saveProject`() = runTest {
        val project = Project(id = "p1", name = "Project 1", category = Category(id = "c1", name = "Cat 1", description = "Desc"))
        every { repository.getProjectsFlow() } returns flowOf(listOf(project))
        coEvery { repository.fetchProjects() } returns Result.success(Unit)
        coEvery { repository.saveProject(any()) } returns Result.success(Unit)

        (useCase.state.value as ProjectState.Init).onInit()
        testDispatcher.scheduler.advanceUntilIdle()

        val dashboard = useCase.state.value as ProjectState.DashboardView
        dashboard.onProjectSelected("p1")
        val tasksView = useCase.state.value as ProjectState.TasksView

        val newTask = Task(id = "t1", title = "New Task")
        tasksView.onAddTask(newTask)

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.saveProject(match { it.tasks.containsKey("t1") }) }
    }
}
