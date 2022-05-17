package com.alexandergorin.foosball.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import com.alexandergorin.foosball.core.SingleLiveEvent
import com.alexandergorin.foosball.ui.matches.MatchState
import com.jakewharton.rxrelay3.BehaviorRelay
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class EditMatchViewModel @Inject constructor(
    private val appRepository: AppRepository,
    @Named("UIScheduler") private val uiScheduler: Scheduler,
) : ViewModel() {

    private val bag = CompositeDisposable()

    private val mutableMatchViewState = MutableLiveData<MatchState>()
    val matchViewState: LiveData<MatchState> = mutableMatchViewState

    private val mutableSaveButtonEnabledState = MutableLiveData<Boolean>()
    val saveButtonEnabledState: LiveData<Boolean> = mutableSaveButtonEnabledState

    private val mutableNavigateBackEvent = SingleLiveEvent<Unit>()
    val navigateBackEvent: LiveData<Unit> = mutableNavigateBackEvent

    private val firstPersonNameRelay = BehaviorRelay.createDefault("")
    private val secondPersonNameRelay = BehaviorRelay.createDefault("")
    private val firstPersonScoreRelay = BehaviorRelay.createDefault("")
    private val secondPersonScoreRelay = BehaviorRelay.createDefault("")

    init {
        Observable.combineLatest(
            firstPersonNameRelay,
            secondPersonNameRelay,
            firstPersonScoreRelay,
            secondPersonScoreRelay
        ) { firstName, secondName, firstScore, secondScore ->
            firstName != secondName
                    && firstName.isNotBlank()
                    && firstScore.isNotEmpty()
                    && secondName.isNotBlank()
                    && secondScore.isNotBlank()
        }.subscribeBy { isEnabled ->
            mutableSaveButtonEnabledState.value = isEnabled
        }.addTo(bag)
    }

    fun loadMatch(id: Int) {
        appRepository.getMatch(id)
            .toObservable()
            .observeOn(uiScheduler)
            .map { match ->
                MatchState(
                    id = match.id,
                    firstPersonName = match.firstPersonName,
                    firstPersonScore = match.firstScore.toString(),
                    secondPersonName = match.secondPersonName,
                    secondPersonScore = match.secondScore.toString(),
                )

            }
            .subscribeBy { viewState ->
                mutableMatchViewState.value = viewState
            }
            .addTo(bag)
    }

    fun saveMatch(id: Int) {

        appRepository.updateMatch(
            Match(
                id = id,
                firstPersonName = firstPersonNameRelay.value.orEmpty(),
                secondPersonName = secondPersonNameRelay.value.orEmpty(),
                firstScore = firstPersonScoreRelay.value?.toInt() ?: 0,
                secondScore = secondPersonScoreRelay.value?.toInt() ?: 0
            )
        ).observeOn(uiScheduler)
            .subscribeBy {
                mutableNavigateBackEvent.call()
            }
    }

    fun addMatch() {
        appRepository.addMatch(
            Match(
                firstPersonName = firstPersonNameRelay.value.orEmpty(),
                secondPersonName = secondPersonNameRelay.value.orEmpty(),
                firstScore = firstPersonScoreRelay.value?.toInt() ?: 0,
                secondScore = secondPersonScoreRelay.value?.toInt() ?: 0
            )
        ).observeOn(uiScheduler)
            .subscribeBy {
                mutableNavigateBackEvent.call()
            }
    }

    fun onFirstPersonNameChange(value: String) {
        firstPersonNameRelay.accept(value)
    }

    fun onSecondPersonNameChange(value: String) {
        secondPersonNameRelay.accept(value)
    }

    fun onFirstPersonScoreChange(value: String) {
        firstPersonScoreRelay.accept(value)
    }

    fun onSecondPersonScoreChange(value: String) {
        secondPersonScoreRelay.accept(value)
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}