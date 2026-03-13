package com.adriantache.projecttracker.ui.view

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.adriantache.projecttracker.ui.model.CategoryUi
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesView(
    modifier: Modifier = Modifier,
    categories: List<CategoryUi>,
    completedProjects: List<ProjectUi> = emptyList(),
    allCategories: List<Category> = emptyList(),
    onAddProject: (String, String, Category) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProjectClick: (String) -> Unit = {},
    onEditCategory: (String, String, String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryUi?>(null) }
    var isCompletedExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundDark,
        topBar = {
            MainTopBar(
                title = "Categories",
                scrollBehavior = scrollBehavior,
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Fixed: LazyVerticalGrid inside LazyColumn needs to be handled carefully.
                // For now, let's use a simple Column with Rows if the number of categories is small,
                // or just keep it simple with a fixed height if we must use grid.
                // Better yet, let's use itemsIndexed to draw them directly in the LazyColumn as rows.

                // Grouping categories into pairs for a grid-like look in LazyColumn
                val chunkedCategories = categories.chunked(2)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    chunkedCategories.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            row.forEach { category ->
                                ItemCard(
                                    id = category.id,
                                    title = category.name,
                                    description = category.description,
                                    footerText = category.numProjects.toString(),
                                    index = categories.indexOf(category),
                                    onClick = onCategoryClick,
                                    onDelete = onDeleteCategory,
                                    onEdit = { editingCategory = category },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Add an empty spacer if the last row only has one item
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            if (completedProjects.isNotEmpty()) {
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 48.dp, vertical = 32.dp),
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
                            .padding(horizontal = 48.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "COMPLETED PROJECTS",
                                fontFamily = InterFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 2.sp
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
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }

                item {
                    AnimatedVisibility(visible = isCompletedExpanded) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 48.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            completedProjects.forEachIndexed { index, project ->
                                ItemCard(
                                    id = project.id,
                                    title = project.name,
                                    description = project.description,
                                    footerText = "Done",
                                    index = index,
                                    onClick = onProjectClick,
                                    onDelete = { /* Disable delete for now in this view */ },
                                    onEdit = { /* Disable edit for now in this view */ }
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddProjectDialog(
                categories = allCategories,
                onDismiss = { showAddDialog = false },
                onConfirm = { name, description, category ->
                    onAddProject(name, description, category)
                }
            )
        }

        editingCategory?.let { category ->
            AddCategoryDialog(
                initialName = category.name,
                initialDescription = category.description,
                isEdit = true,
                onDismiss = { editingCategory = null },
                onConfirm = { updatedCategory ->
                    onEditCategory(category.id, updatedCategory.name, updatedCategory.description)
                }
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun CategoriesViewPreview() {
    val sampleCategories = List(5) { i ->
        val index = i + 1

        CategoryUi(
            id = index.toString(),
            name = "Category $index",
            description = "Description for category $index",
            numProjects = index,
        )
    }

    ProjectTrackerTheme {
        CategoriesView(
            categories = sampleCategories,
            completedProjects = listOf(
                ProjectUi(
                    id = "1",
                    name = "Done Project",
                    description = "Desc",
                    categoryName = "Cat",
                    tasksText = "5/5",
                    tasks = emptyList(),
                    isCompleted = true,
                    canBeCompleted = false
                )
            ),
            onAddProject = { _, _, _ -> },
            onCategoryClick = {},
            onEditCategory = { _, _, _ -> },
            onDeleteCategory = {},
            onRefresh = {},
        )
    }
}
