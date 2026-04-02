package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.SurfaceDark
import com.adriantache.projecttracker.ui.theme.TextCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackClick: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
) {
    val topBarInsets = WindowInsets(top = 32.dp)
    var showMenu by remember { mutableStateOf(false) }

    val navigationIcon: @Composable () -> Unit = {
        if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextCream
                )
            }
        }
    }

    val actions: @Composable RowScope.() -> Unit = {
        if (onToggleFavorite != null) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) Color.Yellow else TextCream
                )
            }
        }
        if (onRefresh != null) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = TextCream
                )
            }
        }
        if (onEdit != null) {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextCream
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    containerColor = SurfaceDark
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = TextCream) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                }
            }
        }
    }

    if (scrollBehavior != null) {
        LargeTopAppBar(
            title = {
                val fontSize = lerp(22.sp, 57.sp, 1f - scrollBehavior.state.collapsedFraction)
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = fontSize)
                )
            },
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark,
                scrolledContainerColor = BackgroundDark,
                navigationIconContentColor = TextCream,
                titleContentColor = Color.White,
                actionIconContentColor = TextCream
            ),
            scrollBehavior = scrollBehavior,
            windowInsets = topBarInsets,
            modifier = Modifier.padding(start = if (onBackClick == null) 32.dp else 0.dp),
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = PlayfairFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark,
                scrolledContainerColor = BackgroundDark,
                titleContentColor = Color.White,
                navigationIconContentColor = TextCream,
                actionIconContentColor = TextCream
            ),
            windowInsets = topBarInsets,
            modifier = Modifier.padding(start = if (onBackClick == null) 32.dp else 0.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun MainTopBarPreview() {
    ProjectTrackerTheme {
        MainTopBar(
            title = "Dashboard",
            onRefresh = {},
            onEdit = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun MainTopBarBackPreview() {
    ProjectTrackerTheme {
        MainTopBar(
            title = "Project Details",
            onBackClick = {},
            onRefresh = {},
            onEdit = {}
        )
    }
}
