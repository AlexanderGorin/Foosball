package com.alexandergorin.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface AppRepository {

    fun getMatches(): Flowable<List<Match>>
    fun getMatch(id: Int): Single<Match>
    fun addMatch(match: Match): Single<Long>
    fun addMatches(matches: List<Match>): Single<List<Long>>
    fun updateMatch(match: Match): Completable
    fun getRankings(): Flowable<List<Ranking>>

//    suspend fun insertUser(user: MatchEntity): Long
//    suspend fun updateBloodPressureForUser(userId: Long, systolicValue: Int, diastolicValue: Int)
//    suspend fun deleteUser()
//    suspend fun updateNamesForUser(
//        userId: Long,
//        firstName: String,
//        middleName: String,
//        lastName: String
//    )
//
//    suspend fun updateKtpForUser(userId: Long, ktpValue: String)
//    suspend fun updateHeightForUser(userId: Long, height: Int)
}