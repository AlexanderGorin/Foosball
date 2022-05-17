package com.alexandergorin.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexandergorin.data.storage.entity.MatchEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatch(match: MatchEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatches(matches: List<MatchEntity>): Completable

    @Query("SELECT * FROM MatchEntity")
    fun getMatches(): Flowable<List<MatchEntity>>

    @Query("SELECT * FROM MatchEntity WHERE id=:id")
    fun getMatch(id: Int): Single<MatchEntity>

    @Query("UPDATE MatchEntity SET person1=:firstPersonName, score1=:firstScore, person2=:secondPersonName, score2=:secondScore WHERE id = :id")
    fun updateMatch(
        id: Int,
        firstPersonName: String,
        firstScore: Int,
        secondPersonName: String,
        secondScore: Int
    ): Completable

}