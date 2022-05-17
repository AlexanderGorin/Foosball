package com.alexandergorin.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexandergorin.data.storage.dao.MatchDao
import com.alexandergorin.data.storage.entity.MatchEntity

@Database(version = AppDatabase.VERSION, entities = [MatchEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDao

    companion object {
        const val DATABASE_NAME = "database.db"
        const val VERSION = 1
    }
}