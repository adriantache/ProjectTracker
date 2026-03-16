package com.adriantache.projecttracker.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adriantache.projecttracker.R
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.ui.model.ProjectUi
import com.adriantache.projecttracker.ui.model.TaskUi
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.CardDarkGrey
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.adriantache.projecttracker.ui.theme.SurfaceDark
import com.adriantache.projecttracker.ui.theme.TextCream
import com.adriantache.projecttracker.ui.theme.TextMuted
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectView(
    modifier: Modifier = Modifier,
    project: ProjectUi,
    allCategories: List<Category> = emptyList(),
    onBackClick: () -> Unit,
    onEditProject: (String, String, Category) -> Unit,
    onAddTask: (String, String) -> Unit,
    onEditTask: (String, String, String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onTaskToggle: (String) -> Unit,
    onCompleteProject: () -> Unit,
    onRefresh: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isAddingTask by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskUi?>(null) }
    var showCompleteConfirmation by remember { mutableStateOf(false) }
    var isPlayingAnimation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundDark,
        topBar = {
            MainTopBar(
                title = project.name,
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
                onRefresh = onRefresh,
                onEdit = { showEditProjectDialog = true }
            )
        }
    ) { paddingValues ->
        val (doneTasks, notDoneTasks) = project.tasks.partition { it.isDone }

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = project.description,
                        fontFamily = InterFamily,
                        fontSize = 18.sp,
                        color = TextMuted,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
                    )
                }

                item {
                    Text(
                        text = "TASKS",
                        fontFamily = InterFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentTeal,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    AddTaskItem(
                        isExpanded = isAddingTask,
                        onExpandedChange = { isAddingTask = it },
                        title = newTaskTitle,
                        onTitleChange = {
                            newTaskTitle = it.replaceFirstChar { char ->
                                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
                            }
                        },
                        description = newTaskDescription,
                        onDescriptionChange = { newTaskDescription = it },
                        onAddClick = {
                            if (newTaskTitle.isNotBlank()) {
                                onAddTask(newTaskTitle, newTaskDescription)
                                newTaskTitle = ""
                                newTaskDescription = ""
                                isAddingTask = false
                            }
                        },
                        onCancelClick = {
                            newTaskTitle = ""
                            newTaskDescription = ""
                            isAddingTask = false
                        }
                    )
                }

                items(notDoneTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { onTaskToggle(task.id) },
                        onEdit = { editingTask = task },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }

                if (doneTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "COMPLETED",
                            fontFamily = InterFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(doneTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { onTaskToggle(task.id) },
                            onEdit = { editingTask = task },
                            onDelete = { onDeleteTask(task.id) }
                        )
                    }
                }

                if (project.canBeCompleted) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { showCompleteConfirmation = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentTeal,
                                contentColor = BackgroundDark
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MARK PROJECT AS COMPLETED",
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            if (isPlayingAnimation) {
                CompletionAnimation(
                    onAnimationFinished = {
                        isPlayingAnimation = false
                        onBackClick()
                    }
                )
            }
        }

        if (showCompleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showCompleteConfirmation = false },
                title = { Text("Complete Project?", color = TextCream, fontFamily = PlayfairFamily) },
                text = {
                    Text(
                        "Are you sure you want to mark this project as completed? It will be moved to the completed projects section.",
                        color = TextMuted,
                        fontFamily = InterFamily
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCompleteConfirmation = false
                            isPlayingAnimation = true
                            onCompleteProject()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                    ) {
                        Text("CONFIRM", color = BackgroundDark)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCompleteConfirmation = false }) {
                        Text("CANCEL", color = TextMuted)
                    }
                },
                containerColor = CardDarkGrey,
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (showEditProjectDialog) {
            AddProjectDialog(
                categories = allCategories,
                initialCategory = allCategories.find { it.name == project.categoryName },
                initialName = project.name,
                initialDescription = project.description,
                isEdit = true,
                onDismiss = { showEditProjectDialog = false },
                onConfirm = { name, description, category ->
                    onEditProject(name, description, category)
                }
            )
        }

        editingTask?.let { task ->
            EditTaskDialog(
                initialTitle = task.title,
                initialDescription = task.description,
                onDismiss = { editingTask = null },
                onConfirm = { title, description ->
                    onEditTask(task.id, title, description)
                }
            )
        }
    }
}

@Composable
fun CompletionAnimation(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CONGRATULATIONS!",
                fontFamily = PlayfairFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextCream,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Project marked as completed.",
                fontFamily = InterFamily,
                fontSize = 18.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EditTaskDialog(
    initialTitle: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
                .fillMaxWidth(0.9f)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
            ) {
                Text(
                    text = "EDIT TASK",
                    fontFamily = InterFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentTeal,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        fontFamily = PlayfairFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextCream
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "Task Title",
                                fontFamily = PlayfairFamily,
                                fontSize = 24.sp,
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
                        fontSize = 16.sp,
                        color = TextMuted
                    ),
                    cursorBrush = SolidColor(AccentTeal),
                    decorationBox = { innerTextField ->
                        if (description.isEmpty()) {
                            Text(
                                text = "Task Description",
                                fontFamily = InterFamily,
                                fontSize = 16.sp,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, description)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                    ) {
                        Text("SAVE CHANGES", color = BackgroundDark)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskItem(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardDarkGrey
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isExpanded) { onExpandedChange(true) }
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isExpanded) AccentTeal else TextMuted,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(20.dp))

                if (!isExpanded) {
                    Text(
                        text = "Add a new task...",
                        fontFamily = PlayfairFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                } else {
                    BasicTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontFamily = PlayfairFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextCream
                        ),
                        cursorBrush = SolidColor(AccentTeal),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = "Task title",
                                    fontFamily = PlayfairFamily,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMuted.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    )

                    IconButton(onClick = onCancelClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Spacer(modifier = Modifier.width(48.dp))
                        Column {
                            BasicTextField(
                                value = description,
                                onValueChange = onDescriptionChange,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = InterFamily,
                                    fontSize = 14.sp,
                                    color = TextMuted
                                ),
                                cursorBrush = SolidColor(AccentTeal),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                decorationBox = { innerTextField ->
                                    if (description.isEmpty()) {
                                        Text(
                                            text = "Add description (optional)",
                                            fontFamily = InterFamily,
                                            fontSize = 14.sp,
                                            color = TextMuted.copy(alpha = 0.5f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = onCancelClick,
                                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                                ) {
                                    Text(
                                        "CANCEL",
                                        fontFamily = InterFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = onAddClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AccentTeal,
                                        contentColor = BackgroundDark
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "ADD TASK",
                                        fontFamily = InterFamily,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: TaskUi,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            val value = dismissState.currentValue
            // Immediately snap back to settled state to avoid item being stuck in dismissed position
            // especially if the action doesn't remove it from the list immediately or shows a dialog.
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)

            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onEdit()
                SwipeToDismissBoxValue.EndToStart -> onDelete()
                else -> {}
            }
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> AccentTeal.copy(alpha = 0.2f)
                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> null
            }
            val tint = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> AccentTeal
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        content = {
            Card(
                onClick = onToggle,
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isDone) SurfaceDark else CardDarkGrey
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (task.isDone) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (task.isDone) "Done" else "Not Done",
                        tint = if (task.isDone) AccentTeal else TextMuted,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = task.title,
                            fontFamily = PlayfairFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isDone) TextMuted else TextCream,
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                        )
                        if (task.description.isNotEmpty()) {
                            Text(
                                text = task.description,
                                fontFamily = InterFamily,
                                fontSize = 14.sp,
                                color = TextMuted,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview(widthDp = 1280, heightDp = 800)
@Composable
fun ProjectViewPreview() {
    val sampleProject = ProjectUi(
        id = "1",
        name = "Deep Learning Research",
        description = "A comprehensive study on transformer architectures and their efficiency in edge computing environments. This includes testing various quantization methods and pruning strategies.",
        tasksText = "3/8",
        categoryName = "category",
        tasks = listOf(
            TaskUi("1", "Literature Review", "Read key papers on BERT and GPT-3", true),
            TaskUi("2", "Setup Environment", "Configure CUDA and PyTorch on server", true),
            TaskUi("3", "Data Preprocessing", "Clean and tokenize the dataset", true),
            TaskUi("4", "Model Implementation", "Build the baseline transformer model", false),
            TaskUi("5", "Training Loop", "Implement distributed training logic", false),
            TaskUi("6", "Evaluation Metrics", "Add BLEU and ROUGE score calculation", false),
            TaskUi("7", "Quantization Experiments", "Test 8-bit and 4-bit quantization", false),
            TaskUi("8", "Final Report", "Document all findings and results", false),
        ),
        isCompleted = false,
        canBeCompleted = false
    )

    ProjectTrackerTheme {
        ProjectView(
            project = sampleProject,
            onBackClick = {},
            onEditProject = { _, _, _ -> },
            onAddTask = { _, _ -> },
            onEditTask = { _, _, _ -> },
            onDeleteTask = {},
            onTaskToggle = {},
            onCompleteProject = {},
            onRefresh = {},
        )
    }
}
