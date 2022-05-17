package com.alexandergorin.foosball.di

import com.alexandergorin.foosball.core.AppResourceProvider
import com.alexandergorin.foosball.core.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds
    abstract fun bindResourceProvider(resourceProvider: AppResourceProvider): ResourceProvider

}