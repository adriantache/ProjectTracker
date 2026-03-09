package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.TextCream
import com.adriantache.projecttracker.ui.theme.TextMuted
import com.adriantache.projecttracker.ui.theme.myCardColors

@Composable
fun ItemCard(
    id: String,
    title: String,
    description: String,
    footerText: String,
    index: Int,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = { onClick(id) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = myCardColors(index)),
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .padding(start = 24.dp)
                .padding(end = 16.dp)
        ) {
            val endPadding = 8.dp

            Row(modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontFamily = PlayfairFamily,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextCream,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = TextCream
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                onDelete(id)
                                showMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                fontFamily = InterFamily,
                fontSize = 14.sp,
                color = TextMuted,
                lineHeight = 20.sp,
                modifier = Modifier.padding(end = endPadding + 32.dp),
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .requiredHeightIn(8.dp)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = endPadding),
                text = footerText,
                fontFamily = InterFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = AccentTeal,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardPreview() {
    ProjectTrackerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ItemCard(
                id = "1",
                title = "Sample Project",
                description = "This is a sample project description to test the ItemCard layout and see how it looks.",
                footerText = "5",
                index = 0,
                onClick = {},
                onDelete = {}
            )
        }
    }
}
