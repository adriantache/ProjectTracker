package com.adriantache.projecttracker.di

import com.adriantache.projecttracker.data.ProjectsRepository
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectsRepository(
        projectsRepository: ProjectsRepository,
    ): ProjectsRepositoryInterface

    companion object {
        @Provides
        @Singleton
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
