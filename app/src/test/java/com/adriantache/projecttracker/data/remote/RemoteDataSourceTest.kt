package com.adriantache.projecttracker.data.remote

import com.adriantache.projecttracker.domain.entity.Project
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteDataSourceTest {

    private val database: FirebaseDatabase = mockk(relaxed = true)
    private val auth: FirebaseAuth = mockk()
    private val rootRef: DatabaseReference = mockk()
    private val userProjectsRef: DatabaseReference = mockk()
    private val user: FirebaseUser = mockk()

    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        // Mock the Firebase chains
        every { database.reference } returns rootRef
        every { auth.currentUser } returns user
        every { user.uid } returns "test_uid"

        // Mock child() chaining for: rootRef.child("users").child(uid).child("projects")
        every { rootRef.child("users") } returns rootRef
        every { rootRef.child("test_uid") } returns rootRef
        every { rootRef.child("projects") } returns userProjectsRef

        // Mock await() extension function
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")

        remoteDataSource = RemoteDataSource(database, auth)
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `getProjects returns success when user is logged in`() = runTest {
        // Arrange
        val snapshot: DataSnapshot = mockk()
        val task: Task<DataSnapshot> = mockk()
        val project = Project(id = "1", name = "Test Project")

        every { userProjectsRef.get() } returns task
        coEvery { task.await() } returns snapshot
        every { snapshot.children } returns listOf(snapshot)
        every { snapshot.getValue(Project::class.java) } returns project

        // Act
        val result = remoteDataSource.getProjects()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Test Project", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun `getProjects returns failure when user is not logged in`() = runTest {
        // Arrange
        every { auth.currentUser } returns null

        // Act
        val result = remoteDataSource.getProjects()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User not logged in", result.exceptionOrNull()?.message)
    }

    @Test
    fun `saveProject calls Firebase setValue`() = runTest {
        // Arrange
        val project = Project(id = "proj123", name = "New Project")
        val taskRef: DatabaseReference = mockk()
        val task: Task<Void> = mockk()

        every { userProjectsRef.child("proj123") } returns taskRef
        every { taskRef.setValue(project) } returns task
        coEvery { task.await() } returns mockk() // Void task

        // Act
        val result = remoteDataSource.saveProject(project)

        // Assert
        assertTrue(result.isSuccess)
    }
}
