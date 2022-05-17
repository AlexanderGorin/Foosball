package com.alexandergorin.data.repository

import com.alexandergorin.data.storage.db.AppDatabase
import com.alexandergorin.data.storage.entity.MatchEntity
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import com.alexandergorin.domain.Ranking
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Named

class AppRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    @Named("IOScheduler") private val ioScheduler: Scheduler,
    @Named("UIScheduler") private val uiScheduler: Scheduler
) : AppRepository {

    override fun getMatches(): Flowable<List<Match>> {
        return database.matchDao().getMatches().map { listOfMatches ->
            listOfMatches.map { match ->
                Match(
                    firstPersonName = match.person1,
                    firstScore = match.score1,
                    secondPersonName = match.person2,
                    secondScore = match.score2,
                    id = match.id
                )
            }
        }.map { it.reversed() }.subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

    override fun getMatch(id: Int): Single<Match> {
        return database.matchDao().getMatch(id).map { match ->
            Match(
                firstPersonName = match.person1,
                firstScore = match.score1,
                secondPersonName = match.person2,
                secondScore = match.score2,
                id = match.id
            )
        }.subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

    override fun addMatch(match: Match): Completable {
        return database.matchDao().insertMatch(
            MatchEntity(
                person1 = match.firstPersonName,
                score1 = match.firstScore,
                person2 = match.secondPersonName,
                score2 = match.secondScore
            )
        ).subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

    override fun addMatches(matches: List<Match>): Completable {
        return database.matchDao().insertMatches(
            matches.map { match ->
                MatchEntity(
                    person1 = match.firstPersonName,
                    score1 = match.firstScore,
                    person2 = match.secondPersonName,
                    score2 = match.secondScore
                )
            }
        ).subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

    override fun updateMatch(match: Match): Completable {
        return database.matchDao().updateMatch(
            id = match.id,
            firstPersonName = match.firstPersonName,
            firstScore = match.firstScore,
            secondPersonName = match.secondPersonName,
            secondScore = match.secondScore
        ).subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

    override fun getRankings(): Flowable<List<Ranking>> {
        return database.matchDao().getMatches().map { matches ->
            val allNames = (matches.map { it.person1 } + matches.map { it.person2 }).toSet()
            val rankings = allNames.map { name ->
                var gamesPlayed = 0
                var gamesWon = 0
                matches.forEach { match ->
                    when (name) {
                        match.person1 -> {
                            gamesPlayed++
                            if (match.score1 > match.score2) {
                                gamesWon++
                            }
                        }
                        match.person2 -> {
                            gamesPlayed++
                            if (match.score2 > match.score1) {
                                gamesWon++
                            }
                        }
                    }
                }

                Ranking(name, gamesPlayed, gamesWon)
            }
            rankings.sortedWith(compareByDescending<Ranking> { it.gamesPlayed }
                .thenByDescending { it.gamesWon })
                .mapIndexed { index, ranking ->
                    Ranking(
                        id = index + 1,
                        personName = ranking.personName,
                        gamesPlayed = ranking.gamesPlayed,
                        gamesWon = ranking.gamesWon
                    )
                }

        }.subscribeOn(ioScheduler).observeOn(uiScheduler)
    }

}