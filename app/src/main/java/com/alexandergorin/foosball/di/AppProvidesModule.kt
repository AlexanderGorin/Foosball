package com.alexandergorin.foosball.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class AppProvidesModule {

    @Provides
    @Named("IOScheduler")
    fun providesIOScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Named("UIScheduler")
    fun providesUIScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
