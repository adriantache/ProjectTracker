package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.SecondaryGray
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardView(
    pendingProjectsCount: Int,
    completedProjectsCount: Int,
    projectsCompletedThisWeek: Int,
    totalTasksCount: Int,
    completedTasksCount: Int,
    tasksCompletedThisWeek: Int,
    recentProjects: List<ProjectUi>,
    categories: List<Category>,
    onProjectClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllCategories: () -> Unit,
    onRefresh: () -> Unit,
    onToggleFavorite: (String) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val currentOnRefresh by rememberUpdatedState(onRefresh)
    LaunchedEffect(Unit) {
        while (true) {
            delay(30000) // Refresh every 30 seconds
            currentOnRefresh()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundDark,
        topBar = {
            MainTopBar(
                title = "Dashboard",
                scrollBehavior = scrollBehavior,
                onRefresh = onRefresh
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
        ) {
            // Stats Section
            item {
                SectionHeader(title = "Overview")
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Active Projects",
                        value = pendingProjectsCount.toString(),
                        icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                        color = AccentTeal
                    )

                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Completed Projects",
                        value = completedProjectsCount.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50)
                    )

                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Recent Projects",
                        value = projectsCompletedThisWeek.toString(),
                        subtitle = "Completed this week",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = Color(0xFFFF9800)
                    )

                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Total Tasks",
                        value = totalTasksCount.toString(),
                        icon = Icons.Default.AssignmentTurnedIn,
                        color = Color(0xFF2196F3)
                    )

                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Completed Tasks",
                        value = completedTasksCount.toString(),
                        icon = Icons.Default.History,
                        color = Color(0xFF9C27B0)
                    )

                    StatCard(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 160.dp),
                        title = "Task Pace",
                        value = tasksCompletedThisWeek.toString(),
                        subtitle = "Completed this week",
                        icon = Icons.Default.Speed,
                        color = Color(0xFFE91E63)
                    )
                }
            }

            // Recent Projects
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(
                    title = "Recent Projects",
                    onActionClick = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (recentProjects.isEmpty()) {
                    Text(
                        "No active projects yet. Add one to get started!",
                        color = SecondaryGray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            items(recentProjects) { project ->
                RecentProjectItem(
                    project = project,
                    onClick = { onProjectClick(project.id) },
                    onToggleFavorite = { onToggleFavorite(project.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Categories
            item {
                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(
                    title = "Categories",
                    actionText = "View All",
                    onActionClick = onViewAllCategories
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(categories.chunked(4)) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { category ->
                        CategoryCard(
                            modifier = Modifier.weight(1f),
                            category = category,
                            onClick = { onCategoryClick(category.id) }
                        )
                    }
                    repeat(4 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    color: Color,
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryGray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = SecondaryGray,
                        lineHeight = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText, color = AccentTeal)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = AccentTeal
                )
            }
        }
    }
}

@Composable
fun RecentProjectItem(
    project: ProjectUi,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryGray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = project.category.name,
                    color = AccentTeal,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (project.isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Toggle Favorite",
                    tint = if (project.isFavorite) Color.Yellow else SecondaryGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${(project.progress * 100).toInt()}%",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: Category,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryGray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, widthDp = 1280, heightDp = 800)
@Composable
fun DashboardPreview() {
    ProjectTrackerTheme {
        DashboardView(
            pendingProjectsCount = 4,
            completedProjectsCount = 8,
            projectsCompletedThisWeek = 2,
            totalTasksCount = 45,
            completedTasksCount = 38,
            tasksCompletedThisWeek = 12,
            recentProjects = listOf(
                ProjectUi(
                    id = "1",
                    name = "Project Tracker App",
                    description = "Mobile app development",
                    category = Category(name = "Mobile", description = ""),
                    tasksText = "3/5 tasks",
                    tasks = emptyList(),
                    progress = 0.6f,
                    isCompleted = false,
                    canBeCompleted = false,
                    isFavorite = true,
                )
            ),
            categories = listOf(
                Category(id = "1", name = "Mobile", description = ""),
                Category(id = "2", name = "Web", description = ""),
                Category(id = "3", name = "Desktop", description = ""),
                Category(id = "4", name = "Design", description = ""),
                Category(id = "5", name = "Marketing", description = ""),
                Category(id = "6", name = "Other", description = ""),
            ),
            onProjectClick = {},
            onCategoryClick = {},
            onViewAllCategories = {},
            onRefresh = {}
        )
    }
}
