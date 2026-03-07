package com.adriantache.projecttracker.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adriantache.projecttracker.domain.entity.Project
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RemoteDataSourceEmulatorTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var remoteDataSource: RemoteDataSource

    @Inject
    lateinit var auth: FirebaseAuth

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testSaveAndGetProjectWithEmulator() = runBlocking {
        // Step 1: Ensure user is logged in to the emulator
        // We use a "test" user. Emulator auth doesn't require real email verification.
        if (auth.currentUser == null) {
            auth.signInWithEmailAndPassword("test@example.com", "password123")
                .addOnFailureListener {
                    // If user doesn't exist in emulator, create it
                    runBlocking {
                        auth.createUserWithEmailAndPassword("test@example.com", "password123").await()
                    }
                }
                .await()
        }

        val testProject = Project(
            id = "test_project_id",
            name = "Emulator Test Project",
            description = "Tested against local Firebase"
        )

        // Step 2: Save project
        val saveResult = remoteDataSource.saveProject(testProject)
        assertTrue("Save should be successful", saveResult.isSuccess)

        // Step 3: Fetch projects
        val getResult = remoteDataSource.getProjects()
        assertTrue("Get should be successful", getResult.isSuccess)

        val projects = getResult.getOrThrow()
        val savedProject = projects.find { it.id == testProject.id }

        // Step 4: Verify data integrity
        assertEquals("Fetched project name should match", testProject.name, savedProject?.name)
        assertEquals("Fetched project description should match", testProject.description, savedProject?.description)

        // Step 5: Delete project
        val deleteResult = remoteDataSource.deleteProject(testProject.id)
        assertTrue("Delete should be successful", deleteResult.isSuccess)

        // Step 6: Verify deletion
        val finalGetResult = remoteDataSource.getProjects()
        val finalProjects = finalGetResult.getOrThrow()
        assertTrue("Project should no longer exist", finalProjects.none { it.id == testProject.id })
    }
}
