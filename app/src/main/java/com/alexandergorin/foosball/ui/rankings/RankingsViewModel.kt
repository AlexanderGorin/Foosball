package com.alexandergorin.foosball.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class RankingsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val bag = CompositeDisposable()

    val appBarTitle: LiveData<String> = MutableLiveData(
        resourceProvider.getString(R.string.rankings)
    )

    private val mutableRankingsViewState = MutableLiveData<RankingsViewState>()
    val rankingsViewState: LiveData<RankingsViewState> = mutableRankingsViewState

    fun loadRankings() {
        appRepository.getRankings()
            .toObservable()
            .map<RankingsViewState> { rankings ->
                RankingsViewState.Loaded(
                    rankings.map { ranking ->
                        Ranking(
                            ranking.id.toString(),
                            ranking.personName,
                            resourceProvider.getString(
                                R.string.games_played,
                                ranking.gamesPlayed
                            ),
                            resourceProvider.getString(R.string.games_won, ranking.gamesWon)
                        )
                    }
                )
            }
            .onErrorReturn { RankingsViewState.Error }
            .startWith(Observable.just(RankingsViewState.Loading))
            .subscribeBy { viewState ->
                mutableRankingsViewState.value = viewState
            }
            .addTo(bag)
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}