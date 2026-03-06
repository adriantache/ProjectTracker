package com.adriantache.projecttracker.domain.state

import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task

sealed interface ProjectState {
    data class Init(val onInit: () -> Unit) : ProjectState
    data object Loading : ProjectState
    data class CategoryView(
        val categories: List<Category>,
        val onCategorySelected: (String) -> Unit,
        val onDeleteCategory: (String) -> Unit,
        val onRefresh: () -> Unit,
    ) : ProjectState

    data class ProjectsView(
        val projects: List<Project>,
        val onProjectSelected: (String) -> Unit,
        val onAddProject: (Project) -> Unit,
        val onDeleteProject: (String) -> Unit,
        val onBack: () -> Unit,
        val onRefresh: () -> Unit,
    ) : ProjectState

    data class TasksView(
        val tasks: List<Task>,
        val onAddTask: (Task) -> Unit,
        val onDeleteTask: (String) -> Unit,
        val onBack: () -> Unit,
        val onRefresh: () -> Unit,
        val onMarkTaskAsDone: (taskId: String, isDone: Boolean) -> Unit,
    ) : ProjectState

    data class Error(val message: String) : ProjectState
}
