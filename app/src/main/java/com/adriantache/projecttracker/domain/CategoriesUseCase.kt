package com.adriantache.projecttracker.domain

import android.util.Log
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.state.ProjectState
import com.adriantache.projecttracker.domain.state.ProjectState.CategoryView
import com.adriantache.projecttracker.domain.state.ProjectState.Init
import com.adriantache.projecttracker.domain.state.ProjectState.Loading
import com.adriantache.projecttracker.domain.state.ProjectState.ProjectsView
import com.adriantache.projecttracker.domain.state.ProjectState.TasksView
import com.adriantache.projecttracker.domain.util.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoriesUseCase(
    private val repository: ProjectsRepositoryInterface,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    val state: StateFlow<ProjectState>
        field: MutableStateFlow<ProjectState> = MutableStateFlow(Init(::onInit))

    var projects = emptyList<Project>()

    private fun onInit() {
        state.value = Loading

        scope.launch {
            onRefresh()
            showCategories()
        }
    }

    private suspend fun onRefresh() {
        repository.getProjects()
            .onSuccess { projects = it }
            .onFailure { Log.e("CategoriesUseCase", "Error refreshing projects", it) }
    }

    private fun showCategories() {
        val categories = projects.map { it.category }.distinct().sortedBy { it.name }

        state.value = CategoryView(
            categories = categories,
            onCategorySelected = ::onCategorySelected,
            onDeleteCategory = ::onDeleteCategory,
            onRefresh = ::onRefreshCategories,
        )
    }

    private fun onRefreshCategories() {
        scope.launch {
            onRefresh()
            showCategories()
        }
    }

    private fun onCategorySelected(categoryId: String) {
        state.value = ProjectsView(
            projects = projects.filter { it.category.id == categoryId },
            onProjectSelected = ::onProjectSelected,
            onAddProject = ::onAddProject,
            onDeleteProject = ::onDeleteProject,
            onBack = ::showCategories,
            onRefresh = {
                scope.launch {
                    state.value = Loading
                    onRefresh()
                    onCategorySelected(categoryId)
                }
            }
        )
    }

    private fun onProjectSelected(projectId: String) {
        val project = projects.first { it.id == projectId }

        state.value = TasksView(
            tasks = project.tasks.values.toList(),
            onAddTask = {
                scope.launch {
                    val newProject = project.copy(tasks = project.tasks + it)
                    repository.saveProject(newProject)
                        .onFailure { Log.e("CategoriesUseCase", "Error adding task", it) }

                    onRefreshProject(projectId)
                }
            },
            onDeleteTask = {
                scope.launch {
                    val newProject = project.copy(tasks = project.tasks - it)
                    repository.saveProject(newProject)
                        .onFailure { Log.e("CategoriesUseCase", "Error deleting task", it) }

                    onRefreshProject(projectId)
                }
            },
            onMarkTaskAsDone = { id, done ->
                scope.launch {
                    val newProject = project.copy(tasks = project.tasks.toMutableMap().apply {
                        this[id] = requireNotNull(this[id]).copy(isDone = done)
                    })
                    repository.saveProject(newProject)
                        .onFailure { Log.e("CategoriesUseCase", "Error marking task as done", it) }

                    onRefreshProject(projectId)
                }
            },
            onBack = { onCategorySelected(project.category.id) },
            onRefresh = { onRefreshProject(projectId) },
        )
    }

    private fun onRefreshProject(projectId: String) {
        scope.launch {
            onRefresh()
            onProjectSelected(projectId)
        }
    }

    private fun onDeleteCategory(categoryId: String) {
        scope.launch {
            projects.filter { it.category.id == categoryId }.forEach {
                repository.deleteProject(it.id)
                    .onFailure { Log.e("CategoriesUseCase", "Error deleting project", it) }
            }
            onRefresh()
            showCategories()
        }
    }

    private fun onAddProject(project: Project) {
        scope.launch {
            repository.saveProject(project)
                .onFailure { Log.e("CategoriesUseCase", "Error adding project", it) }
            onRefresh()
            onCategorySelected(project.category.id)
        }
    }

    private fun onDeleteProject(projectId: String) {
        scope.launch {
            repository.deleteProject(projectId)
                .onFailure { Log.e("CategoriesUseCase", "Error deleting project", it) }
            onRefresh()
            onCategorySelected(projects.first { it.id == projectId }.category.id)
        }
    }
}
