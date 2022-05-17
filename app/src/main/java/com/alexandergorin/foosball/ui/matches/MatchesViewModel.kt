package com.alexandergorin.foosball.ui.matches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexandergorin.domain.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val appRepository: AppRepository,
    @Named("UIScheduler") private val uiScheduler: Scheduler,
) : ViewModel() {

    private val bag = CompositeDisposable()

    private val mutableMatchesViewState = MutableLiveData<MatchesViewState>()
    val matchesViewState: LiveData<MatchesViewState> = mutableMatchesViewState

    fun loadMatches() {
        appRepository.getMatches()
            .toObservable()
            .observeOn(uiScheduler)
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
            .onErrorReturn { MatchesViewState.Error }
            .startWith(Observable.just(MatchesViewState.Loading))
            .subscribeBy { viewState ->
                mutableMatchesViewState.value = viewState
            }
            .addTo(bag)
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}