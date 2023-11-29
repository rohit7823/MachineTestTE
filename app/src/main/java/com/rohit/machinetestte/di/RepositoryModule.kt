package com.rohit.machinetestte.di

import com.rohit.machinetestte.base.domain.repositories.AssignmentRepository
import com.rohit.machinetestte.data.impl.AssignmentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAssignmentRepository(impl: AssignmentRepositoryImpl): AssignmentRepository

}