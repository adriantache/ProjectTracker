package com.adriantache.projecttracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adriantache.projecttracker.ui.model.CategoryUi
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.model.TaskUi
import com.adriantache.projecttracker.ui.view.CategoriesView
import com.adriantache.projecttracker.ui.view.ProjectView
import com.adriantache.projecttracker.ui.view.ProjectsView

sealed class Screen(val route: String) {
    object Categories : Screen("categories")
    object Projects : Screen("projects/{categoryId}") {
        fun createRoute(categoryId: String) = "projects/$categoryId"
    }

    object ProjectDetails : Screen("project/{projectId}") {
        fun createRoute(projectId: String) = "project/$projectId"
    }
}

@Composable
fun ProjectNavigation() {
    val navController = rememberNavController()

    // Mock data for now, would typically come from a ViewModel
    val sampleCategories = List(5) { i ->
        val index = i + 1
        CategoryUi(
            id = index.toString(),
            name = "Category $index",
            description = "Description for category $index",
            numProjects = index * 3,
        )
    }

    val sampleProjects = List(15) { i ->
        val index = i + 1
        ProjectUi(
            id = index.toString(),
            name = "Project $index",
            description = "Description for project $index",
            tasksText = "$index/20",
            tasks = listOf(
                TaskUi("1", "Task 1", "Description for task 1", true),
                TaskUi("2", "Task 2", "Description for task 2", false),
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Categories.route
    ) {
        composable(Screen.Categories.route) {
            CategoriesView(
                categories = sampleCategories,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.Projects.createRoute(categoryId))
                }
            )
        }

        composable(
            route = Screen.Projects.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            ProjectsView(
                projects = sampleProjects,
                onBackClick = { navController.popBackStack() },
                onAddProject = { name, description ->
                    // Logic to add project
                },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetails.createRoute(projectId))
                }
            )
        }

        composable(
            route = Screen.ProjectDetails.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val project = sampleProjects.find { it.id == projectId }

            if (project != null) {
                ProjectView(
                    project = project,
                    onBackClick = { navController.popBackStack() },
                    onAddTask = { title, description ->
                        // Logic to add task
                    },
                    onTaskToggle = { taskId ->
                        // Logic to toggle task
                    }
                )
            }
        }
    }
}
