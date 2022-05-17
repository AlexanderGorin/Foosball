package com.alexandergorin.foosball.ui.matches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.ResourceProvider
import com.alexandergorin.foosball.core.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val appRepository: AppRepository,
    resourceProvider: ResourceProvider,
    @Named("UIScheduler") private val uiScheduler: Scheduler
) : BaseViewModel() {

    val appBarTitle: LiveData<String> = MutableLiveData(
        resourceProvider.getString(R.string.app_name)
    )

    private val mutableMatchesViewState = MutableLiveData<MatchesViewState>()
    val matchesViewState: LiveData<MatchesViewState> = mutableMatchesViewState

    fun loadMatches() {
        appRepository.getMatches()
            .toObservable()
            .map<MatchesViewState> { matches ->
                MatchesViewState.Loaded(
                    matches.map { match ->
                        MatchState(
                            match.id,
                            match.firstPersonName,
                            match.firstScore.toString(),
                            match.secondPersonName,
                            match.secondScore.toString(),
                        )
                    }
                )
            }
            .observeOn(uiScheduler)
            .onErrorReturn { MatchesViewState.Error }
            .startWith(Observable.just(MatchesViewState.Loading))
            .subscribeBy { viewState ->
                mutableMatchesViewState.value = viewState
            }
            .addTo(compositeDisposable)
    }
}