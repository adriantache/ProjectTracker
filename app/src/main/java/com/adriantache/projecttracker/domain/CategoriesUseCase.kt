package com.adriantache.projecttracker.domain

import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import com.adriantache.projecttracker.domain.state.ProjectState
import com.adriantache.projecttracker.domain.state.ProjectState.CategoryView
import com.adriantache.projecttracker.domain.state.ProjectState.DashboardView
import com.adriantache.projecttracker.domain.state.ProjectState.Init
import com.adriantache.projecttracker.domain.state.ProjectState.Loading
import com.adriantache.projecttracker.domain.state.ProjectState.ProjectsView
import com.adriantache.projecttracker.domain.state.ProjectState.TasksView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesUseCase @Inject constructor(
    private val repository: ProjectsRepositoryInterface,
    private val scope: CoroutineScope, // Injected for testability
) {
    private var projectsJob: Job? = null

    val state: StateFlow<ProjectState>
        field: MutableStateFlow<ProjectState> = MutableStateFlow(Init(::onInit))

    var projects = emptyList<Project>()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: List<Category>
        get() = _categories.value

    private fun onInit() {
        state.value = Loading

        // Start collecting database changes reactively
        projectsJob?.cancel()
        projectsJob = repository.getProjectsFlow()
            .onEach { newList ->
                projects = newList
                val uniqueCategories = newList.map { it.category }.distinctBy { it.id }.sortedBy { it.sortOrder }
                _categories.value = uniqueCategories

                // Update UI state based on new data
                updateStateWithNewData(newList = newList)
            }
            .launchIn(scope)

        // Trigger initial remote refresh with a timeout to prevent infinite loading
        scope.launch {
            try {
                withTimeoutOrNull(10_000) {
                    repository.fetchProjects().onFailure {
                        handleError("Error fetching projects: ${it.message}")
                    }
                }
            } finally {
                // Always ensure we transition out of loading after some time if no data came from DB
                delay(500) // Give a moment for the DB emission to trigger updateStateWithNewData
                if (state.value is Loading) {
                    showDashboard()
                }
            }
        }
    }

    private fun updateStateWithNewData(oldList: List<Project> = emptyList(), newList: List<Project>) {
        when (val currentState = state.value) {
            is DashboardView -> showDashboard()

            is CategoryView -> showCategories()

            is ProjectsView -> {
                val categoryId = newList.find { p -> oldList.any { it.id == p.id } }?.category?.id
                    ?: projects.find { it.category.id == currentState.category.id }?.category?.id
                    ?: projects.firstOrNull()?.category?.id

                if (categoryId != null) {
                    onCategorySelected(categoryId)
                } else {
                    showDashboard()
                }
            }

            is TasksView -> {
                val currentProjectId = currentState.project.id
                val updatedProject = newList.find { it.id == currentProjectId }
                if (updatedProject != null) {
                    onProjectSelected(currentProjectId)
                } else {
                    showDashboard()
                }
            }

            is Loading, is ProjectState.Error, is Init -> {
                // Transition to main view as soon as we have any response from DB
                showDashboard()
            }
        }
    }

    private fun showDashboard() {
        val pendingProjects = projects.filter { !it.isCompleted }
            .sortedWith(compareBy<Project> { it.sortOrder }.thenByDescending { it.isFavorite }.thenBy { it.name })
        val completedProjects = projects.filter { it.isCompleted }

        val oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
        val projectsCompletedThisWeek = completedProjects.count { (it.completionTimestamp ?: 0L) > oneWeekAgo }

        val allTasks = projects.flatMap { it.tasks.values }
        val totalTasksCount = allTasks.size
        val completedTasksCount = allTasks.count { it.isDone }
        val tasksCompletedThisWeek = allTasks.count { it.isDone && (it.completionTimestamp ?: 0L) > oneWeekAgo }

        state.value = DashboardView(
            totalProjects = projects.size,
            pendingProjectsCount = pendingProjects.size,
            completedProjectsCount = completedProjects.size,
            projectsCompletedThisWeek = projectsCompletedThisWeek,
            totalTasksCount = totalTasksCount,
            completedTasksCount = completedTasksCount,
            tasksCompletedThisWeek = tasksCompletedThisWeek,
            recentProjects = pendingProjects.take(5),
            categories = categories,
            onProjectSelected = ::onProjectSelected,
            onCategorySelected = ::onCategorySelected,
            onViewAllCategories = ::showCategories,
            onRefresh = ::onRefreshDashboard,
            onToggleFavorite = ::onToggleFavorite,
        )
    }

    private fun onRefreshDashboard() {
        scope.launch {
            repository.fetchProjects().onFailure {
                handleError("Error refreshing dashboard: ${it.message}")
            }
            showDashboard()
        }
    }

    private fun showCategories() {
        val activeProjects = projects.filter { !it.isCompleted }
        val projectCounts = activeProjects.groupBy { it.category.id }.mapValues { it.value.size }

        state.value = CategoryView(
            categories = categories,
            projectCounts = projectCounts,
            onCategorySelected = ::onCategorySelected,
            onProjectSelected = ::onProjectSelected,
            onEditCategory = ::onEditCategory,
            onDeleteCategory = ::onDeleteCategory,
            onMoveCategory = ::onMoveCategory,
            onRefresh = ::onRefreshCategories,
            onAddProject = ::onAddProject,
            onBack = ::showDashboard,
        )
    }

    private fun onRefreshCategories() {
        scope.launch {
            state.value = Loading
            repository.fetchProjects().onFailure {
                handleError("Error refreshing categories: ${it.message}")
            }
            if (state.value is Loading) showCategories()
        }
    }

    private fun onCategorySelected(categoryId: String) {
        val targetCategory = categories.find { it.id == categoryId }

        val filteredProjects = if (targetCategory != null) {
            projects.filter { it.category.id == targetCategory.id }
        } else {
            emptyList()
        }
        val pendingProjects = filteredProjects.filter { !it.isCompleted }
            .sortedWith(compareBy<Project> { it.sortOrder }.thenByDescending { it.isFavorite }.thenBy { it.name })
        val completedProjects = filteredProjects.filter { it.isCompleted }.sortedByDescending { it.completionTimestamp }

        state.value = ProjectsView(
            category = targetCategory ?: Category(id = categoryId, name = "Unknown", description = ""),
            pendingProjects = pendingProjects,
            completedProjects = completedProjects,
            categories = categories,
            onProjectSelected = ::onProjectSelected,
            onEditProject = ::onEditProject,
            onAddProject = ::onAddProject,
            onDeleteProject = ::onDeleteProject,
            onMoveProject = { from, to -> onMoveProject(categoryId, from, to) },
            onBack = ::showCategories,
            onRefresh = {
                scope.launch {
                    state.value = Loading
                    repository.fetchProjects().onFailure {
                        handleError("Error refreshing projects for category: ${it.message}")
                    }
                    if (state.value is Loading) onCategorySelected(categoryId)
                }
            },
            onToggleFavorite = ::onToggleFavorite,
        )
    }

    private fun onProjectSelected(projectId: String) {
        val project = projects.find { it.id == projectId }
        if (project == null) {
            showDashboard()
            return
        }

        state.value = TasksView(
            project = project,
            categories = categories,
            onEditProject = { n, d, c -> onEditProject(projectId, n, d, c) },
            onAddTask = { task -> onAddTask(project, task) },
            onEditTask = { taskId, title, description -> onEditTask(project, taskId, title, description) },
            onDeleteTask = { taskId -> onDeleteTask(project, taskId) },
            onMarkTaskAsDone = { id, done -> onMarkTaskAsDone(project, id, done) },
            onCompleteProject = { onCompleteProject(projectId) },
            onBack = {
                if (project.isCompleted) {
                    showDashboard()
                } else {
                    onCategorySelected(project.category.id)
                }
            },
            onRefresh = { onRefreshProject(projectId) },
            onToggleFavorite = ::onToggleFavorite,
        )
    }

    private fun onToggleFavorite(projectId: String) {
        scope.launch {
            repository.toggleFavorite(projectId).onFailure {
                handleError("Error toggling favorite: ${it.message}")
            }
        }
    }

    private fun onCompleteProject(projectId: String) {
        scope.launch {
            repository.completeProject(projectId).onFailure {
                handleError("Error completing project: ${it.message}")
            }
        }
    }

    private fun onMarkTaskAsDone(project: Project, id: String, done: Boolean) {
        scope.launch {
            val task = project.tasks[id] ?: return@launch
            val updatedTask = task.setDone(done)
            val newProject = project.copy(
                tasks = project.tasks + (id to updatedTask),
                completionTimestamp = if (!done) null else project.completionTimestamp
            )
            repository.saveProject(newProject).onFailure {
                handleError("Error saving task status: ${it.message}")
            }
        }
    }

    private fun onDeleteTask(project: Project, taskId: String) {
        scope.launch {
            val newProject = project.copy(tasks = project.tasks - taskId)
            repository.saveProject(newProject).onFailure {
                handleError("Error deleting task: ${it.message}")
            }
        }
    }

    private fun onAddTask(project: Project, task: Task) {
        scope.launch {
            val newProject = project.copy(
                tasks = project.tasks + (task.id to task),
                completionTimestamp = null
            )
            repository.saveProject(newProject).onFailure {
                handleError("Error adding task: ${it.message}")
            }
        }
    }

    private fun onEditTask(project: Project, taskId: String, title: String, description: String) {
        scope.launch {
            val task = project.tasks[taskId] ?: return@launch
            val updatedTask = task.setTitle(title).setDescription(description)
            val updatedProject = project.copy(tasks = project.tasks + (taskId to updatedTask))
            repository.saveProject(updatedProject).onFailure {
                handleError("Error editing task: ${it.message}")
            }
        }
    }

    private fun onRefreshProject(projectId: String) {
        scope.launch {
            state.value = Loading
            repository.fetchProjects().onFailure {
                handleError("Error refreshing project: ${it.message}")
            }
            if (state.value is Loading) onProjectSelected(projectId)
        }
    }

    private fun onDeleteCategory(categoryId: String) {
        state.value = Loading
        scope.launch {
            val projectsToDelete = projects.filter { it.category.id == categoryId }
            projectsToDelete.forEach {
                repository.deleteProject(it.id).onFailure { e ->
                    handleError("Error deleting projects for category: ${e.message}")
                }
            }
            if (state.value is Loading) showDashboard()
        }
    }

    private fun onEditCategory(categoryId: String, name: String, description: String) {
        scope.launch {
            val projectsToUpdate = projects.filter { it.category.id == categoryId }
            projectsToUpdate.forEach { project ->
                val newCategory = project.category.setName(name).setDescription(description)
                repository.saveProject(project.setCategory(newCategory)).onFailure {
                    handleError("Error editing category: ${it.message}")
                }
            }
        }
    }

    private fun onMoveCategory(fromIndex: Int, toIndex: Int) {
        val currentCategories = categories.toMutableList()
        if (fromIndex !in currentCategories.indices || toIndex !in currentCategories.indices) return

        val category = currentCategories.removeAt(fromIndex)
        currentCategories.add(toIndex, category)

        val updatedCategories = currentCategories.mapIndexed { index, cat ->
            cat.setSortOrder(index)
        }

        scope.launch {
            // Updating categories means updating all projects in those categories
            val projectsToUpdate = projects.map { project ->
                val updatedCategory = updatedCategories.find { it.id == project.category.id }
                if (updatedCategory != null) {
                    project.setCategory(updatedCategory)
                } else {
                    project
                }
            }
            repository.saveProjects(projectsToUpdate).onFailure {
                handleError("Error moving category: ${it.message}")
            }
        }
    }

    private fun onMoveProject(categoryId: String, fromIndex: Int, toIndex: Int) {
        val filteredProjects = projects.filter { it.category.id == categoryId && !it.isCompleted }
            .sortedWith(compareBy<Project> { it.sortOrder }.thenByDescending { it.isFavorite }.thenBy { it.name })
            .toMutableList()

        if (fromIndex !in filteredProjects.indices || toIndex !in filteredProjects.indices) return

        val project = filteredProjects.removeAt(fromIndex)
        filteredProjects.add(toIndex, project)

        val updatedProjects = filteredProjects.mapIndexed { index, p ->
            p.setSortOrder(index)
        }

        scope.launch {
            repository.saveProjects(updatedProjects).onFailure {
                handleError("Error moving project: ${it.message}")
            }
        }
    }

    private fun onAddProject(project: Project) {
        scope.launch {
            val existingCategory = categories.find { it.name.equals(project.category.name, ignoreCase = true) }
            val finalProject = if (existingCategory != null) {
                project.setCategory(existingCategory)
            } else {
                val nextSortOrder = (categories.maxOfOrNull { it.sortOrder } ?: -1) + 1
                project.setCategory(project.category.setSortOrder(nextSortOrder))
            }

            val nextProjectSortOrder =
                (projects.filter { it.category.id == finalProject.category.id }.maxOfOrNull { it.sortOrder } ?: -1) + 1
            val finalProjectWithSort = finalProject.setSortOrder(nextProjectSortOrder)

            repository.saveProject(finalProjectWithSort).onFailure {
                handleError("Error adding project: ${it.message}")
            }
        }
    }

    private fun onEditProject(projectId: String, name: String, description: String, category: Category) {
        scope.launch {
            val project = projects.find { it.id == projectId } ?: return@launch
            val updatedProject = project.setName(name).setDescription(description).setCategory(category)
            repository.saveProject(updatedProject).onFailure {
                handleError("Error editing project: ${it.message}")
            }
        }
    }

    private fun onDeleteProject(projectId: String) {
        scope.launch {
            repository.deleteProject(projectId).onFailure {
                handleError("Error deleting project: ${it.message}")
            }
        }
    }

    private fun handleError(message: String) {
        state.value = ProjectState.Error(message) { showDashboard() }
    }
}
