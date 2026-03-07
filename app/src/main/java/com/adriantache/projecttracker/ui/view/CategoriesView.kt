package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.model.CategoryUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesView(
    modifier: Modifier = Modifier,
    categories: List<CategoryUi>,
    allCategories: List<Category> = emptyList(),
    onAddProject: (String, String, Category) -> Unit,
    onCategoryClick: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            MainTopBar(title = "Categories")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AccentTeal,
                contentColor = BackgroundDark,
                shape = RoundedCornerShape(16.dp),
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
                .padding(horizontal = 48.dp, vertical = 8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(categories) { index, category ->
                    ItemCard(
                        id = category.id,
                        title = category.name,
                        description = category.description,
                        footerText = category.numProjects.toString(),
                        index = index,
                        onClick = onCategoryClick,
                        onDelete = onDeleteCategory
                    )
                }
            }
        }

        if (showAddDialog) {
            AddProjectDialog(
                categories = allCategories,
                onDismiss = { showAddDialog = false },
                onAddProject = onAddProject
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun CategoriesViewPreview() {
    val sampleCategories = List(15) { i ->
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
            onAddProject = { _, _, _ -> },
            onCategoryClick = {},
            onDeleteCategory = {},
        )
    }
}
