package com.adriantache.projecttracker.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.CardDarkGrey
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.SurfaceDark
import com.adriantache.projecttracker.ui.theme.TextCream
import com.adriantache.projecttracker.ui.theme.TextMuted
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProjectDialog(
    categories: List<Category> = emptyList(),
    initialCategory: Category? = null,
    initialName: String = "",
    initialDescription: String = "",
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Category) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var selectedCategory by remember { mutableStateOf(initialCategory ?: categories.firstOrNull() ?: Category.All) }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var addedCategories by remember { mutableStateOf(listOf<Category>()) }
    val allCategories = remember(categories, addedCategories) { categories + addedCategories }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 600

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { newCategory ->
                addedCategories = addedCategories + newCategory
                selectedCategory = newCategory
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkGrey),
            modifier = Modifier
                .fillMaxWidth(if (isSmallScreen) 0.95f else 0.6f)
                .padding(if (isSmallScreen) 12.dp else 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isSmallScreen) 20.dp else 32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
            ) {
                Text(
                    text = if (isEdit) "EDIT PROJECT" else "NEW PROJECT",
                    fontFamily = InterFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                BasicTextField(
                    value = name,
                    onValueChange = {
                        name =
                            it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontFamily = PlayfairFamily,
                        fontSize = if (isSmallScreen) 28.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCream
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    decorationBox = { innerTextField ->
                        if (name.isEmpty()) {
                            Text(
                                text = "Project Name",
                                fontFamily = PlayfairFamily,
                                fontSize = if (isSmallScreen) 28.sp else 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = InterFamily,
                        fontSize = if (isSmallScreen) 16.sp else 18.sp,
                        color = TextMuted
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = "Add a description for your project...",
                                fontFamily = InterFamily,
                                fontSize = if (isSmallScreen) 16.sp else 18.sp,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "CATEGORY",
                    fontFamily = InterFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allCategories.forEach { category ->
                        CategoryChip(
                            name = category.name,
                            isSelected = selectedCategory.id == category.id,
                            onClick = {
                                selectedCategory = category
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .clickable {
                                showAddCategoryDialog = true
                            }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = AccentTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NEW",
                                fontFamily = InterFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentTeal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isSmallScreen) 32.dp else 48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) {
                        Text(
                            "CANCEL",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(if (isSmallScreen) 12.dp else 24.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, description, selectedCategory)
                                onDismiss()
                            }
                        },
                        modifier = if (isSmallScreen) Modifier.weight(1f) else Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentTeal,
                            contentColor = BackgroundDark
                        ),
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = if (isSmallScreen) 16.dp else 32.dp,
                            vertical = if (isSmallScreen) 10.dp else 16.dp
                        )
                    ) {
                        Text(
                            if (isEdit) "SAVE CHANGES" else "CREATE PROJECT",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    initialName: String = "",
    initialDescription: String = "",
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp < 600

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkGrey),
            modifier = Modifier
                .fillMaxWidth(if (isSmallScreen) 0.95f else 0.5f)
                .padding(if (isSmallScreen) 12.dp else 24.dp)
        ) {
            @Suppress("DEPRECATION")
            Column(
                modifier = Modifier
                    .padding(if (isSmallScreen) 20.dp else 32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
            ) {
                Text(
                    text = if (isEdit) "EDIT CATEGORY" else "NEW CATEGORY",
                    fontFamily = InterFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                BasicTextField(
                    value = name,
                    onValueChange = {
                        name =
                            it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontFamily = PlayfairFamily,
                        fontSize = if (isSmallScreen) 28.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCream
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    decorationBox = { innerTextField ->
                        if (name.isEmpty()) {
                            Text(
                                text = "Category Name",
                                fontFamily = PlayfairFamily,
                                fontSize = if (isSmallScreen) 28.sp else 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = InterFamily,
                        fontSize = if (isSmallScreen) 16.sp else 18.sp,
                        color = TextMuted
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = "Add a description for your category...",
                                fontFamily = InterFamily,
                                fontSize = if (isSmallScreen) 16.sp else 18.sp,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(if (isSmallScreen) 32.dp else 48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) {
                        Text(
                            "CANCEL",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(if (isSmallScreen) 12.dp else 24.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(Category(name = name, description = description))
                                onDismiss()
                            }
                        },
                        modifier = if (isSmallScreen) Modifier.weight(1f) else Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentTeal,
                            contentColor = BackgroundDark
                        ),
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = if (isSmallScreen) 16.dp else 32.dp,
                            vertical = if (isSmallScreen) 10.dp else 16.dp
                        )
                    ) {
                        Text(
                            if (isEdit) "SAVE CHANGES" else "ADD CATEGORY",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) AccentTeal else SurfaceDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = BackgroundDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = name,
                fontFamily = InterFamily,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) BackgroundDark else TextCream
            )
        }
    }
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun AddProjectDialogPreview() {
    ProjectTrackerTheme {
        AddProjectDialog(
            categories = listOf(
                Category(name = "Work", description = ""),
                Category(name = "Personal", description = ""),
                Category(name = "Study", description = "")
            ),
            onDismiss = {},
            onConfirm = { _, _, _ -> }
        )
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun AddProjectDialogMobilePreview() {
    ProjectTrackerTheme {
        AddProjectDialog(
            categories = listOf(
                Category(name = "Work", description = ""),
                Category(name = "Personal", description = ""),
                Category(name = "Study", description = "")
            ),
            onDismiss = {},
            onConfirm = { _, _, _ -> }
        )
    }
}
