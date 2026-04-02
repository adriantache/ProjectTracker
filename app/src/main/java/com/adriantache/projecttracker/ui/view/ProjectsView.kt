package com.adriantache.projecttracker.ui.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsView(
    modifier: Modifier = Modifier,
    category: Category,
    projects: List<ProjectUi>,
    completedProjects: List<ProjectUi>,
    allCategories: List<Category> = emptyList(),
    onBackClick: () -> Unit,
    onAddProject: (String, String, Category) -> Unit,
    onProjectClick: (String) -> Unit,
    onEditProject: (String, String, String, Category) -> Unit,
    onDeleteProject: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<ProjectUi?>(null) }

    var isCompletedExpanded by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()

    LaunchedEffect(isCompletedExpanded) {
        if (isCompletedExpanded) {
            // Scroll to the "COMPLETED PROJECTS" header item, 
            // which is at index projects.size
            gridState.animateScrollToItem(projects.size)
        }
    }

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
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(200.dp),
                contentPadding = PaddingValues(
                    start = 48.dp,
                    top = 24.dp,
                    end = 48.dp,
                    bottom = 104.dp // Space for FAB
                ),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize(),
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
                        onEdit = { editingProject = project },
                        isFavorite = project.isFavorite,
                        onToggleFavorite = onToggleFavorite
                    )
                }

                if (completedProjects.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Spacer(Modifier.height(16.dp))

                            HorizontalDivider(
                                color = TextMuted.copy(alpha = 0.2f)
                            )

                            val rotation by animateFloatAsState(
                                targetValue = if (isCompletedExpanded) 180f else 0f,
                                label = "rotation"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isCompletedExpanded = !isCompletedExpanded }
                                    .padding(vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "COMPLETED PROJECTS",
                                    fontFamily = InterFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 2.sp,
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .background(TextMuted.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = completedProjects.size.toString(),
                                        fontFamily = InterFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.rotate(rotation)
                                )
                            }
                        }
                    }

                    if (isCompletedExpanded) {
                        itemsIndexed(completedProjects) { index, project ->
                            ItemCard(
                                id = project.id,
                                title = project.name,
                                description = project.description,
                                footerText = project.tasksText,
                                index = index,
                                onClick = onProjectClick,
                                onDelete = onDeleteProject,
                                onEdit = { editingProject = project },
                                isFavorite = project.isFavorite,
                                onToggleFavorite = onToggleFavorite
                            )
                        }
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
                    initialCategory = allCategories.find { it.id == project.category.id } ?: category,
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
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun ProjectsViewPreview() {
    val sampleProjects = List(15) { i ->
        val index = i + 1

        val isCompleted = i.mod(2) == 0
        ProjectUi(
            id = index.toString(),
            name = "Project $index",
            description = "Description for project $index",
            tasksText = "$index/20",
            tasks = emptyList(),
            category = Category(id = "Test", name = "Test", description = ""),
            progress = 0f,
            isCompleted = isCompleted,
            canBeCompleted = true,
            isFavorite = i % 3 == 0
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
            onToggleFavorite = {},
            onRefresh = {},
            completedProjects = sampleProjects.filter { it.isCompleted },
        )
    }
}
