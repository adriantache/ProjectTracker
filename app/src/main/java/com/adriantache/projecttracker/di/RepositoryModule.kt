package com.adriantache.projecttracker.di

import com.adriantache.projecttracker.data.ProjectsRepository
import com.adriantache.projecttracker.domain.data.ProjectsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectsRepository(
        projectsRepository: ProjectsRepository,
    ): ProjectsRepositoryInterface
}
