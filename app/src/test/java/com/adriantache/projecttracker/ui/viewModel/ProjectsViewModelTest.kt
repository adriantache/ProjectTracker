package com.adriantache.projecttracker.ui.viewModel

import com.adriantache.projecttracker.domain.CategoriesUseCase
import com.adriantache.projecttracker.domain.state.ProjectState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectsViewModelTest {

    @Test
    fun `viewModel exposes state from useCase`() {
        val mockState = mockk<ProjectState.DashboardView>()
        val stateFlow = MutableStateFlow<ProjectState>(mockState)
        val useCase = mockk<CategoriesUseCase> {
            every { state } returns stateFlow
        }

        val viewModel = ProjectsViewModel(useCase)

        assertEquals(mockState, viewModel.state.value)
    }
}
