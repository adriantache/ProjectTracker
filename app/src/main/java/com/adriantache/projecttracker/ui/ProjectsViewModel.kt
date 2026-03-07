package com.adriantache.projecttracker.ui

import androidx.lifecycle.ViewModel
import com.adriantache.projecttracker.domain.CategoriesUseCase
import com.adriantache.projecttracker.domain.state.ProjectState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    useCase: CategoriesUseCase,
) : ViewModel() {
    val state: StateFlow<ProjectState> = useCase.state
}
