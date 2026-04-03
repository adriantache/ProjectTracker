package com.adriantache.projecttracker.ui.viewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun `viewModel initializes with current user`() {
        val mockUser = mockk<FirebaseUser>()
        val mockAuth = mockk<FirebaseAuth> {
            every { currentUser } returns mockUser
            every { addAuthStateListener(any()) } returns Unit
        }

        val viewModel = AuthViewModel(mockAuth)

        assertEquals(mockUser, viewModel.currentUser.value)
        verify { mockAuth.addAuthStateListener(any()) }
    }

    @Test
    fun `authStateListener updates currentUser`() {
        val mockUser = mockk<FirebaseUser>()
        val listenerSlot = slot<FirebaseAuth.AuthStateListener>()
        val mockAuth = mockk<FirebaseAuth> {
            every { currentUser } returns null
            every { addAuthStateListener(capture(listenerSlot)) } returns Unit
        }

        val viewModel = AuthViewModel(mockAuth)

        // Mock currentUser changing
        every { mockAuth.currentUser } returns mockUser
        listenerSlot.captured.onAuthStateChanged(mockAuth)

        assertEquals(mockUser, viewModel.currentUser.value)
    }

    @Test
    fun `viewModel removes listener on cleared`() {
        val mockAuth = mockk<FirebaseAuth> {
            every { currentUser } returns null
            every { addAuthStateListener(any()) } returns Unit
            every { removeAuthStateListener(any()) } returns Unit
        }

        val viewModel = AuthViewModel(mockAuth)

        // Use reflection to call protected onCleared
        val onClearedMethod = viewModel.javaClass.declaredMethods.find { it.name == "onCleared" }
        onClearedMethod?.let {
            it.isAccessible = true
            it.invoke(viewModel)
        }

        verify { mockAuth.removeAuthStateListener(any()) }
    }
}
