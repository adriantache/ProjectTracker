package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = AccentTeal,
            strokeWidth = 3.dp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Loading",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = PlayfairFamily,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun LoadingViewPreview() {
    ProjectTrackerTheme {
        LoadingView()
    }
}
