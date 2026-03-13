package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsView(
    modifier: Modifier = Modifier,
    category: Category,
    projects: List<ProjectUi>,
    allCategories: List<Category> = emptyList(),
    onBackClick: () -> Unit,
    onAddProject: (String, String, Category) -> Unit,
    onProjectClick: (String) -> Unit,
    onEditProject: (String, String, String, Category) -> Unit,
    onDeleteProject: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<ProjectUi?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundDark,
        topBar = {
            MainTopBar(
                title = "${category.name} Projects",
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
                onRefresh = onRefresh
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentTeal,
                contentColor = BackgroundDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Project",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(projects) { index, project ->
                    ItemCard(
                        id = project.id,
                        title = project.name,
                        description = project.description,
                        footerText = project.tasksText,
                        index = index,
                        onClick = onProjectClick,
                        onDelete = onDeleteProject,
                        onEdit = { editingProject = project }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddProjectDialog(
                categories = allCategories,
                initialCategory = category,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description, selectedCategory ->
                    onAddProject(name, description, selectedCategory)
                }
            )
        }

        editingProject?.let { project ->
            AddProjectDialog(
                categories = allCategories,
                initialCategory = allCategories.find { it.name == project.categoryName } ?: category,
                initialName = project.name,
                initialDescription = project.description,
                isEdit = true,
                onDismiss = { editingProject = null },
                onConfirm = { name, description, selectedCategory ->
                    onEditProject(project.id, name, description, selectedCategory)
                }
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun ProjectsViewPreview() {
    val sampleProjects = List(15) { i ->
        val index = i + 1

        ProjectUi(
            id = index.toString(),
            name = "Project $index",
            description = "Description for project $index",
            tasksText = "$index/20",
            tasks = emptyList(),
            categoryName = "Test",
        )
    }

    ProjectTrackerTheme {
        ProjectsView(
            category = Category(name = "Test", description = ""),
            projects = sampleProjects,
            onBackClick = {},
            onAddProject = { _, _, _ -> },
            onProjectClick = {},
            onEditProject = { _, _, _, _ -> },
            onDeleteProject = {},
            onRefresh = {},
        )
    }
}
