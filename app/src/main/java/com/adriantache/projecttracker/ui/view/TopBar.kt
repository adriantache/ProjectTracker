package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.TextCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackClick: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
) {
    val topBarInsets = WindowInsets(top = 32.dp)

    if (scrollBehavior != null) {
        LargeTopAppBar(
            title = {
                val fontSize = lerp(22.sp, 57.sp, 1f - scrollBehavior.state.collapsedFraction)
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = fontSize)
                )
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextCream
                        )
                    }
                }
            },
            actions = {
                if (onRefresh != null) {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextCream
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark,
                scrolledContainerColor = BackgroundDark,
                navigationIconContentColor = TextCream,
                titleContentColor = Color.White,
                actionIconContentColor = TextCream
            ),
            scrollBehavior = scrollBehavior,
            windowInsets = topBarInsets,
            modifier = Modifier.padding(start = 32.dp),
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
            actions = {
                if (onRefresh != null) {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextCream
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark,
                scrolledContainerColor = BackgroundDark,
                titleContentColor = Color.White,
                navigationIconContentColor = TextCream,
                actionIconContentColor = TextCream
            ),
            windowInsets = topBarInsets,
            modifier = Modifier.padding(start = 32.dp),
        )
    }
}
