package com.adriantache.projecttracker.domain.state

import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task

sealed interface ProjectState {
    data class Init(val onInit: () -> Unit) : ProjectState
    data object Loading : ProjectState
    data class CategoryView(
        val categories: List<Category>,
        val projectCounts: Map<String, Int>,
        val completedProjects: List<Project>,
        val onCategorySelected: (String) -> Unit,
        val onProjectSelected: (String) -> Unit,
        val onEditCategory: (String, String, String) -> Unit,
        val onDeleteCategory: (String) -> Unit,
        val onRefresh: () -> Unit,
        val onAddProject: (Project) -> Unit,
    ) : ProjectState

    data class ProjectsView(
        val category: Category,
        val projects: List<Project>,
        val categories: List<Category>,
        val onProjectSelected: (String) -> Unit,
        val onEditProject: (String, String, String, Category) -> Unit,
        val onAddProject: (Project) -> Unit,
        val onDeleteProject: (String) -> Unit,
        val onBack: () -> Unit,
        val onRefresh: () -> Unit,
    ) : ProjectState

    data class TasksView(
        val project: Project,
        val categories: List<Category>,
        val onEditProject: (String, String, Category) -> Unit,
        val onAddTask: (Task) -> Unit,
        val onEditTask: (String, String, String) -> Unit,
        val onDeleteTask: (String) -> Unit,
        val onBack: () -> Unit,
        val onRefresh: () -> Unit,
        val onMarkTaskAsDone: (taskId: String, isDone: Boolean) -> Unit,
        val onCompleteProject: () -> Unit,
    ) : ProjectState

    data class Error(val message: String) : ProjectState
}
