package com.alexandergorin.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface AppRepository {

    fun getMatches(): Flowable<List<Match>>

    fun getMatch(id: Int): Single<Match>

    fun addMatch(match: Match): Completable

    fun addMatches(matches: List<Match>): Completable

    fun updateMatch(match: Match): Completable

    fun getRankings(): Flowable<List<Ranking>>
}