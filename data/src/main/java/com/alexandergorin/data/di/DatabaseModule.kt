package com.alexandergorin.data.di

import android.content.Context
import androidx.room.Room
import com.alexandergorin.data.storage.InitialDatabaseDataCallback
import com.alexandergorin.data.storage.dao.MatchDao
import com.alexandergorin.data.storage.db.AppDatabase
import com.alexandergorin.domain.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        appRepository: Provider<AppRepository>,
    ): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addCallback(InitialDatabaseDataCallback(appRepository))
            .build()
    }

    @Singleton
    @Provides
    fun provideMatchDao(appDataBase: AppDatabase): MatchDao {
        return appDataBase.matchDao()
    }
}