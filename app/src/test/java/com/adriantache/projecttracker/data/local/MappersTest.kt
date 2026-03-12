package com.adriantache.projecttracker.data.local

import com.adriantache.projecttracker.data.local.entity.CategoryEntity
import com.adriantache.projecttracker.data.local.entity.ProjectEntity
import com.adriantache.projecttracker.data.local.entity.ProjectWithTasks
import com.adriantache.projecttracker.data.local.entity.TaskEntity
import com.adriantache.projecttracker.domain.entity.Category
import com.adriantache.projecttracker.domain.entity.Project
import com.adriantache.projecttracker.domain.entity.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MappersTest {

    @Test
    fun `Project toEntity maps correctly`() {
        val category = Category("cat1", "Cat", "Desc")
        val project = Project("p1", "Project", "Desc", category)

        val entity = project.toEntity(12345L)

        assertEquals("p1", entity.id)
        assertEquals("Project", entity.name)
        assertEquals("cat1", entity.categoryId)
        assertEquals(12345L, entity.lastUpdated)
    }

    @Test
    fun `Category toEntity and toCategory map correctly`() {
        val domain = Category("cat1", "Cat", "Desc")
        val entity = domain.toEntity(12345L)

        assertEquals("cat1", entity.id)
        assertEquals("Cat", entity.name)
        assertEquals(12345L, entity.lastUpdated)

        val backToDomain = entity.toCategory()
        assertEquals(domain, backToDomain)
    }

    @Test
    fun `Task toEntity and toTask map correctly`() {
        val timestamp = ZonedDateTime.now()
        val domain = Task("t1", "Task", isDone = true, timestamp = timestamp)
        val entity = domain.toEntity("p1", 12345L)

        assertEquals("t1", entity.id)
        assertEquals("p1", entity.projectId)
        assertEquals("Task", entity.name)
        assertEquals(true, entity.isDone)
        assertEquals(timestamp.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), entity.timestamp)
        assertEquals(12345L, entity.lastUpdated)

        val backToDomain = entity.toTask()
        assertEquals(domain.id, backToDomain.id)
        assertEquals(domain.title, backToDomain.title)
        assertEquals(domain.isDone, backToDomain.isDone)
        assertEquals(domain.timestamp, backToDomain.timestamp)
    }

    @Test
    fun `ProjectWithTasks toProject maps correctly`() {
        val projectEntity = ProjectEntity("p1", "Project", "Desc", "cat1")
        val categoryEntity = CategoryEntity("cat1", "Cat", "Desc")
        val isoTimestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        val taskEntities = listOf(
            TaskEntity("t1", "Task 1", "Desc 1", false, "p1", isoTimestamp),
            TaskEntity("t2", "Task 2", "Desc 2", true, "p1", isoTimestamp)
        )
        val projectWithTasks = ProjectWithTasks(projectEntity, categoryEntity, taskEntities)

        val project = projectWithTasks.toProject()

        assertEquals("p1", project.id)
        assertEquals("Cat", project.category.name)
        assertEquals(2, project.tasks.size)
        assertEquals(true, project.tasks["t2"]?.isDone)
    }
}
