package com.alexandergorin.data.di

import com.alexandergorin.data.repository.AppRepositoryImpl
import com.alexandergorin.domain.AppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {

    @Binds
    abstract fun bindAppRepository(
        appRepo: AppRepositoryImpl
    ): AppRepository
}