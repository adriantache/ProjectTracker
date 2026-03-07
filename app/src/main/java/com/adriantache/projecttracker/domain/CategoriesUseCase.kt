package com.adriantache.projecttracker.domain

import android.util.Log
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import com.adriantache.projecttracker.domain.state.ProjectState
import com.adriantache.projecttracker.domain.state.ProjectState.CategoryView
import com.adriantache.projecttracker.domain.state.ProjectState.Init
import com.adriantache.projecttracker.domain.state.ProjectState.Loading
import com.adriantache.projecttracker.domain.state.ProjectState.ProjectsView
import com.adriantache.projecttracker.domain.state.ProjectState.TasksView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoriesUseCase @Inject constructor(
    private val repository: ProjectsRepositoryInterface,
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private var projectsJob: Job? = null

    val state: StateFlow<ProjectState>
        field: MutableStateFlow<ProjectState> = MutableStateFlow(Init(::onInit))

    var projects = emptyList<Project>()
    val categories: List<Category>
        get() = projects.map { it.category }.distinctBy { it.name }.sortedBy { it.name }

    private fun onInit() {
        state.value = Loading

        // Start collecting database changes reactively
        projectsJob?.cancel()
        projectsJob = repository.getProjectsFlow()
            .onEach { newList ->
                val oldProjects = projects
                projects = newList

                // If we are in a view that depends on the project list, update it automatically
                updateStateWithNewData(oldProjects, newList)
            }
            .launchIn(scope)

        // Trigger initial remote refresh
        scope.launch {
            repository.fetchProjects()
                .onFailure {
                    Log.e("CategoriesUseCase", "Initial remote refresh failed", it)
                }
        }
    }

    private fun updateStateWithNewData(oldList: List<Project>, newList: List<Project>) {
        when (val currentState = state.value) {
            is CategoryView -> showCategories()
            is ProjectsView -> {
                // Determine which category was being viewed
                val categoryId = newList.find { p -> oldList.any { it.id == p.id } }?.category?.id
                    ?: projects.find { it.category.id == currentState.category.id }?.category?.id
                    ?: projects.firstOrNull()?.category?.id

                if (categoryId != null) {
                    onCategorySelected(categoryId)
                } else {
                    showCategories()
                }
            }

            is TasksView -> {
                val currentProjectId = currentState.project.id
                val updatedProject = newList.find { it.id == currentProjectId }
                if (updatedProject != null) {
                    onProjectSelected(currentProjectId)
                } else {
                    showCategories()
                }
            }

            else -> {
                if (newList.isNotEmpty() && currentState is Loading) {
                    showCategories()
                }
            }
        }
    }

    private fun showCategories() {
        val projectCounts = projects.groupBy { it.category.id }.mapValues { it.value.size }

        state.value = CategoryView(
            categories = categories,
            projectCounts = projectCounts,
            onCategorySelected = ::onCategorySelected,
            onDeleteCategory = ::onDeleteCategory,
            onRefresh = ::onRefreshCategories,
            onAddProject = ::onAddProject,
        )
    }

    private fun onRefreshCategories() {
        scope.launch {
            state.value = Loading
            repository.fetchProjects()
        }
    }

    private fun onCategorySelected(categoryId: String) {
        val targetCategory = categories.find { it.id == categoryId }
            ?: categories.find { it.name == categoryId }

        val filteredProjects = if (targetCategory != null) {
            projects.filter { it.category.name == targetCategory.name }
        } else {
            projects.filter { it.category.id == categoryId }
        }

        state.value = ProjectsView(
            category = targetCategory ?: Category(id = categoryId, name = "Unknown", description = ""),
            projects = filteredProjects,
            categories = categories,
            onProjectSelected = ::onProjectSelected,
            onAddProject = ::onAddProject,
            onDeleteProject = ::onDeleteProject,
            onBack = ::showCategories,
            onRefresh = {
                scope.launch {
                    state.value = Loading
                    repository.fetchProjects()
                }
            }
        )
    }

    private fun onProjectSelected(projectId: String) {
        val project = projects.find { it.id == projectId }
        if (project == null) {
            showCategories()
            return
        }

        state.value = TasksView(
            project = project,
            onAddTask = { task -> onAddTask(project, task) },
            onDeleteTask = { taskId -> onDeleteTask(project, taskId) },
            onMarkTaskAsDone = { id, done -> onMarkTaskAsDone(project, id, done) },
            onBack = { onCategorySelected(project.category.id) },
            onRefresh = { onRefreshProject() },
        )
    }

    private fun onMarkTaskAsDone(project: Project, id: String, done: Boolean) {
        scope.launch {
            val task = project.tasks[id] ?: return@launch
            val newProject = project.copy(tasks = project.tasks + (id to task.copy(isDone = done)))
            repository.saveProject(newProject)
        }
    }

    private fun onDeleteTask(project: Project, taskId: String) {
        scope.launch {
            val newProject = project.copy(tasks = project.tasks - taskId)
            repository.saveProject(newProject)
        }
    }

    private fun onAddTask(project: Project, task: Task) {
        scope.launch {
            val newProject = project.copy(tasks = project.tasks + (task.id to task))
            repository.saveProject(newProject)
        }
    }

    private fun onRefreshProject() {
        scope.launch {
            state.value = Loading
            repository.fetchProjects()
        }
    }

    private fun onDeleteCategory(categoryId: String) {
        state.value = Loading
        scope.launch {
            val projectsToDelete = projects.filter { it.category.id == categoryId }
            projectsToDelete.forEach { repository.deleteProject(it.id) }
        }
    }

    private fun onAddProject(project: Project) {
        scope.launch {
            val existingCategory = categories.find { it.name.equals(project.category.name, ignoreCase = true) }
            val finalProject = if (existingCategory != null) {
                project.copy(category = existingCategory)
            } else {
                project
            }

            repository.saveProject(finalProject)
        }
    }

    private fun onDeleteProject(projectId: String) {
        scope.launch {
            repository.deleteProject(projectId)
        }
    }
}
