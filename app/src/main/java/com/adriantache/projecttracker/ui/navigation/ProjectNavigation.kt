package com.adriantache.projecttracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import com.adriantache.projecttracker.domain.state.ProjectState
import com.adriantache.projecttracker.ui.ProjectsViewModel
import com.adriantache.projecttracker.ui.model.toUi
import com.adriantache.projecttracker.ui.view.CategoriesView
import com.adriantache.projecttracker.ui.view.ErrorView
import com.adriantache.projecttracker.ui.view.LoadingView
import com.adriantache.projecttracker.ui.view.ProjectView
import com.adriantache.projecttracker.ui.view.ProjectsView

sealed class Screen(val route: String) {
    data object Categories : Screen("categories")
    data object Projects : Screen("projects")
    data object ProjectDetails : Screen("project")
}

@Composable
fun ProjectNavigation(
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()

    // Handle initial state trigger
    if (state is ProjectState.Init) {
        LaunchedEffect(state) {
            (state as ProjectState.Init).onInit()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Categories.route,
        modifier = modifier
    ) {
        composable(Screen.Categories.route) {
            when (val currentState = state) {
                is ProjectState.Init, ProjectState.Loading -> LoadingView()

                is ProjectState.CategoryView -> {
                    CategoriesView(
                        categories = currentState.categories.map {
                            it.toUi(currentState.projectCounts[it.id] ?: 0)
                        },
                        allCategories = currentState.categories,
                        onAddProject = { name, description, category ->
                            currentState.onAddProject(
                                Project(
                                    name = name,
                                    description = description,
                                    category = category
                                )
                            )
                        },
                        onCategoryClick = { categoryId ->
                            currentState.onCategorySelected(categoryId)
                        },
                        onDeleteCategory = { categoryId ->
                            currentState.onDeleteCategory(categoryId)
                        },
                    )
                }

                is ProjectState.Error -> ErrorView(currentState.message)

                is ProjectState.ProjectsView -> {
                    LaunchedEffect(Unit) {
                        if (navController.currentDestination?.route != Screen.Projects.route) {
                            navController.navigate(Screen.Projects.route)
                        }
                    }
                }

                is ProjectState.TasksView -> {
                    LaunchedEffect(Unit) {
                        if (navController.currentDestination?.route != Screen.ProjectDetails.route) {
                            navController.navigate(Screen.ProjectDetails.route)
                        }
                    }
                }
            }
        }

        composable(Screen.Projects.route) {
            val currentState = state

            // Sync system back button with UseCase state
            BackHandler {
                if (currentState is ProjectState.ProjectsView) {
                    currentState.onBack()
                }
                navController.popBackStack()
            }

            when (currentState) {
                is ProjectState.ProjectsView -> {
                    ProjectsView(
                        category = currentState.category,
                        projects = currentState.projects.map { it.toUi() },
                        allCategories = currentState.categories,
                        onBackClick = {
                            currentState.onBack()
                            navController.popBackStack()
                        },
                        onAddProject = { name, description, category ->
                            currentState.onAddProject(
                                Project(
                                    name = name,
                                    description = description,
                                    category = category
                                )
                            )
                        },
                        onProjectClick = { projectId ->
                            currentState.onProjectSelected(projectId)
                        },
                        onDeleteProject = { projectId ->
                            currentState.onDeleteProject(projectId)
                        },
                    )
                }

                ProjectState.Loading -> LoadingView()

                is ProjectState.Error -> ErrorView(currentState.message)

                is ProjectState.CategoryView -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack(Screen.Categories.route, false)
                    }
                }

                is ProjectState.TasksView -> {
                    LaunchedEffect(Unit) {
                        if (navController.currentDestination?.route != Screen.ProjectDetails.route) {
                            navController.navigate(Screen.ProjectDetails.route)
                        }
                    }
                }

                is ProjectState.Init -> LaunchedEffect(Unit) {
                    navController.popBackStack(Screen.Categories.route, false)
                }
            }
        }

        composable(Screen.ProjectDetails.route) {
            val currentState = state

            // Sync system back button with UseCase state
            BackHandler {
                if (currentState is ProjectState.TasksView) {
                    currentState.onBack()
                }
                navController.popBackStack()
            }

            when (currentState) {
                is ProjectState.TasksView -> {
                    ProjectView(
                        project = currentState.project.toUi(),
                        onBackClick = {
                            currentState.onBack()
                            navController.popBackStack()
                        },
                        onTaskToggle = { taskId ->
                            val task = currentState.project.tasks[taskId]
                            if (task != null) {
                                currentState.onMarkTaskAsDone(taskId, !task.isDone)
                            }
                        },
                        onAddTask = { title, description ->
                            currentState.onAddTask(Task(title = title, description = description))
                        }
                    )
                }

                ProjectState.Loading -> LoadingView()

                is ProjectState.Error -> ErrorView(currentState.message)

                is ProjectState.ProjectsView -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack(Screen.Projects.route, false)
                    }
                }

                is ProjectState.CategoryView, is ProjectState.Init -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack(Screen.Categories.route, false)
                    }
                }
            }
        }
    }
}
