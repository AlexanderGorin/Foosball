package com.alexandergorin.foosball.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import com.alexandergorin.foosball.R
import com.alexandergorin.foosball.core.ResourceProvider
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
    private val resourceProvider: ResourceProvider,
    @Named("UIScheduler") private val uiScheduler: Scheduler
) : ViewModel() {

    private val bag = CompositeDisposable()

    private val mutableAppBarTitle = MutableLiveData<String>()
    val appBarTitle: LiveData<String> = mutableAppBarTitle

    private val mutableMatchViewState = MutableLiveData<MatchState>()
    val matchViewState: LiveData<MatchState> = mutableMatchViewState

    private val mutableSaveButtonEnabledState = MutableLiveData<Boolean>()
    val saveButtonEnabledState: LiveData<Boolean> = mutableSaveButtonEnabledState

    private val mutableNavigateBackEvent = SingleLiveEvent<Unit>()
    val navigateBackEvent: LiveData<Unit> = mutableNavigateBackEvent

    private val mutableErrorEvent = SingleLiveEvent<String>()
    val errorEvent: LiveData<String> = mutableErrorEvent

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

    fun loadAppBarTitle(type: EditMatchType) {
        val title = when (type) {
            EditMatchType.Add -> resourceProvider.getString(R.string.add_your_match)
            is EditMatchType.Edit -> resourceProvider.getString(R.string.edit_your_match)
        }
        mutableAppBarTitle.value = title
    }

    fun loadMatch(type: EditMatchType) {
        if (type is EditMatchType.Edit) {
            appRepository.getMatch(type.matchId)
                .map { match ->
                    MatchState(
                        id = match.id,
                        firstPersonName = match.firstPersonName,
                        firstPersonScore = match.firstScore.toString(),
                        secondPersonName = match.secondPersonName,
                        secondPersonScore = match.secondScore.toString(),
                    )
                }
                .observeOn(uiScheduler)
                .subscribeBy(
                    onError = {
                        mutableErrorEvent.value = resourceProvider.getString(R.string.common_error)
                    },
                    onSuccess = { viewState ->
                        mutableMatchViewState.value = viewState
                    }
                )
                .addTo(bag)
        }
    }

    fun saveMatch(type: EditMatchType) {
        when (type) {
            EditMatchType.Add -> addMatch()
            is EditMatchType.Edit -> updateMatch(type)
        }
    }

    private fun updateMatch(type: EditMatchType.Edit) {
        appRepository.updateMatch(
            Match(
                id = type.matchId,
                firstPersonName = firstPersonNameRelay.value.orEmpty(),
                secondPersonName = secondPersonNameRelay.value.orEmpty(),
                firstScore = firstPersonScoreRelay.value?.toInt() ?: 0,
                secondScore = secondPersonScoreRelay.value?.toInt() ?: 0
            )
        ).observeOn(uiScheduler)
            .subscribeBy(
                onError = {
                    mutableErrorEvent.value = resourceProvider.getString(R.string.common_error)
                },
                onComplete = mutableNavigateBackEvent::call
            ).addTo(bag)
    }

    private fun addMatch() {
        appRepository.addMatch(
            Match(
                firstPersonName = firstPersonNameRelay.value.orEmpty(),
                secondPersonName = secondPersonNameRelay.value.orEmpty(),
                firstScore = firstPersonScoreRelay.value?.toInt() ?: 0,
                secondScore = secondPersonScoreRelay.value?.toInt() ?: 0
            )
        ).observeOn(uiScheduler)
            .subscribeBy(
                onError = {
                    mutableErrorEvent.value = resourceProvider.getString(R.string.common_error)
                },
                onComplete = mutableNavigateBackEvent::call
            ).addTo(bag)
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