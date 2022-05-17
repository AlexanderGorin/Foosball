package com.alexandergorin.data.storage

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber
import javax.inject.Provider

class InitialDatabaseDataCallback(
    private val appRepository: Provider<AppRepository>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        val matches = listOf(
            Match("Amos", 4, "Diego", 5),
            Match("Amos", 1, "Diego", 5),
            Match("Amos", 2, "Diego", 5),
            Match("Amos", 0, "Diego", 5),
            Match("Amos", 6, "Diego", 5),
            Match("Amos", 5, "Diego", 2),
            Match("Amos", 4, "Diego", 0),
            Match("Joel", 4, "Diego", 5),
            Match("Tim", 4, "Amos", 5),
            Match("Tim", 5, "Amos", 2),
            Match("Amos", 3, "Tim", 5),
            Match("Amos", 5, "Tim", 3),
            Match("Amos", 5, "Joel", 4),
            Match("Joel", 5, "Tim", 2)
        )

        appRepository.get().addMatches(matches)
            .subscribeBy(
                onError = {
                    Timber.d(it.message.orEmpty())
                }
            )
    }
}