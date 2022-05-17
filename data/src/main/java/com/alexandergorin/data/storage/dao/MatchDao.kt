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
    fun insertMatch(match: MatchEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatches(matches: List<MatchEntity>): Single<List<Long>>

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
//
//    @Query("UPDATE MatchEntity SET systolicBpValue=:systolicValue, diastolicBpValue=:diastolicValue WHERE id = :userId")
//    suspend fun updateBloodPressure(userId: Long, systolicValue: Int, diastolicValue: Int)
//
//    @Query("UPDATE MatchEntity SET firstName=:firstName, middleName=:middleName, lastName=:lastName WHERE id = :userId")
//    suspend fun updateNames(userId: Long, firstName: String, middleName: String, lastName: String)
//
//    @Query("UPDATE MatchEntity SET firstName=:firstName, lastName=:lastName WHERE id = :userId")
//    suspend fun updateNames(userId: Long, firstName: String, lastName: String)
//
//    @Query("UPDATE MatchEntity SET ktpValue=:ktpValue WHERE id = :userId")
//    suspend fun updateKtp(userId: Long, ktpValue: String)
//
//    @Query("UPDATE MatchEntity SET height=:height WHERE id = :userId")
//    suspend fun updateHeight(userId: Long, height: Int)
//
//    @Query("DELETE FROM MatchEntity")
//    suspend fun deleteUserTable()
}