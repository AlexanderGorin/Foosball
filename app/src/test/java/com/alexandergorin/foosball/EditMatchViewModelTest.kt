package com.alexandergorin.foosball

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import com.alexandergorin.foosball.core.ResourceProvider
import com.alexandergorin.foosball.ui.edit.EditMatchType
import com.alexandergorin.foosball.ui.edit.EditMatchViewModel
import com.alexandergorin.foosball.ui.matches.MatchState
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val MATCH_ID = 111
private const val TITLE_EDIT = "TITlE_EDIT"
private const val TITLE_ADD = "TITlE_ADD"
private const val FIRST_NAME = "FIRST_NAME"
private const val SECOND_NAME = "SECOND_NAME"
private const val FIRST_SCORE = "1"
private const val SECOND_SCORE = "5"
private const val ERROR_MESSAGE = "ERROR_MESSAGE"

class EditMatchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val match =
        Match(FIRST_NAME, FIRST_SCORE.toInt(), SECOND_NAME, SECOND_SCORE.toInt(), MATCH_ID)
    private val addMatch = match.copy(id = 0)
    private val matchState =
        MatchState(MATCH_ID, FIRST_NAME, FIRST_SCORE, SECOND_NAME, SECOND_SCORE)
    private val throwable = Throwable()

    private val repository: AppRepository = mockk() {
        every { updateMatch(match) } returns Completable.complete()
        every { addMatch(addMatch) } returns Completable.complete()
    }
    private val resourceProvider: ResourceProvider = mockk() {
        every { getString(R.string.add_your_match) } returns TITLE_ADD
        every { getString(R.string.edit_your_match) } returns TITLE_EDIT
        every { getString(R.string.common_error) } returns ERROR_MESSAGE
    }

    private val observerMatchState: Observer<MatchState> = mockk(relaxed = true)
    private val observerAppBarTitle: Observer<String> = mockk(relaxed = true)
    private val observerNavigate: Observer<Unit> = mockk(relaxed = true)
    private val observerError: Observer<String> = mockk(relaxed = true)
    private val observerSaveButtonState: Observer<Boolean> = mockk(relaxed = true)
    private lateinit var viewModel: EditMatchViewModel

    @Before
    fun before() {
        viewModel = EditMatchViewModel(
            repository,
            resourceProvider,
            Schedulers.trampoline()
        )
        viewModel.matchViewState.observeForever(observerMatchState)
        viewModel.appBarTitle.observeForever(observerAppBarTitle)
        viewModel.navigateBackEvent.observeForever(observerNavigate)
        viewModel.errorEvent.observeForever(observerError)
        viewModel.saveButtonEnabledState.observeForever(observerSaveButtonState)
    }

    @Test
    fun `when loadAppBar called with edit type then emits app bar title`() {
        viewModel.loadAppBarTitle(EditMatchType.Edit(MATCH_ID))

        verify {
            observerAppBarTitle.onChanged(TITLE_EDIT)
        }
    }

    @Test
    fun `when loadAppBar called with add type then emits app bar title`() {
        viewModel.loadAppBarTitle(EditMatchType.Add)

        verify {
            observerAppBarTitle.onChanged(TITLE_ADD)
        }
    }

    @Test
    fun `given repository emits match when loadMatch called with edit type then emits match state`() {
        every { repository.getMatch(MATCH_ID) } returns Single.just(match)

        viewModel.loadMatch(EditMatchType.Edit(MATCH_ID))

        verify {
            observerMatchState.onChanged(matchState)
        }
    }

    @Test
    fun `given repository emits error when loadMatch called with edit type then emits error event`() {
        every { repository.getMatch(MATCH_ID) } returns Single.error(throwable)

        viewModel.loadMatch(EditMatchType.Edit(MATCH_ID))

        verify {
            observerError.onChanged(ERROR_MESSAGE)
        }
    }

    @Test
    fun `when loadMatch called with add type then repo is not called`() {
        viewModel.loadMatch(EditMatchType.Add)

        verify {
            repository wasNot Called
        }
    }


    @Test
    fun `given match is loaded and repository completes when saveMatch called with edit type then emits navigation event`() {
        every { repository.updateMatch(match) } returns Completable.complete()

        viewModel.onFirstPersonScoreChange(FIRST_SCORE)
        viewModel.onSecondPersonScoreChange(SECOND_SCORE)
        viewModel.onFirstPersonNameChange(FIRST_NAME)
        viewModel.onSecondPersonNameChange(SECOND_NAME)
        viewModel.saveMatch(EditMatchType.Edit(MATCH_ID))

        verify {
            observerNavigate.onChanged(null)
        }
    }

    @Test
    fun `given repository fails when saveMatch called with edit type then emits error event`() {
        every { repository.updateMatch(match) } returns Completable.error(throwable)

        viewModel.onFirstPersonScoreChange(FIRST_SCORE)
        viewModel.onSecondPersonScoreChange(SECOND_SCORE)
        viewModel.onFirstPersonNameChange(FIRST_NAME)
        viewModel.onSecondPersonNameChange(SECOND_NAME)
        viewModel.saveMatch(EditMatchType.Edit(MATCH_ID))

        verify {
            observerError.onChanged(ERROR_MESSAGE)
            observerNavigate wasNot called
        }
    }

    @Test
    fun `given repository completes when saveMatch called with add type then emits navigation event`() {
        every { repository.addMatch(addMatch) } returns Completable.complete()

        viewModel.onFirstPersonScoreChange(FIRST_SCORE)
        viewModel.onSecondPersonScoreChange(SECOND_SCORE)
        viewModel.onFirstPersonNameChange(FIRST_NAME)
        viewModel.onSecondPersonNameChange(SECOND_NAME)
        viewModel.saveMatch(EditMatchType.Add)

        verify {
            observerNavigate.onChanged(null)
        }
    }

    @Test
    fun `given repository emits error when saveMatch called with add type then emits error event`() {
        every { repository.addMatch(addMatch) } returns Completable.error(throwable)

        viewModel.onFirstPersonScoreChange(FIRST_SCORE)
        viewModel.onSecondPersonScoreChange(SECOND_SCORE)
        viewModel.onFirstPersonNameChange(FIRST_NAME)
        viewModel.onSecondPersonNameChange(SECOND_NAME)
        viewModel.saveMatch(EditMatchType.Add)

        verify {
            observerError.onChanged(ERROR_MESSAGE)
        }
    }

    @Test
    fun `when data is changed and is valid then emits enabled button state`() {
        viewModel.onFirstPersonScoreChange(FIRST_SCORE)
        viewModel.onSecondPersonScoreChange(SECOND_SCORE)
        viewModel.onFirstPersonNameChange(FIRST_NAME)
        viewModel.onSecondPersonNameChange(SECOND_NAME)

        verify {
            observerSaveButtonState.onChanged(true)
        }
    }

    @Test
    fun `when data is changed and not valid then emits disabled button state`() {
        viewModel.onFirstPersonScoreChange("")
        viewModel.onSecondPersonScoreChange("")
        viewModel.onFirstPersonNameChange("")
        viewModel.onSecondPersonNameChange("")

        verify {
            observerSaveButtonState.onChanged(false)
        }
    }

    @Test
    fun `when data is changed and valid and names are same then emits disabled button state`() {
        viewModel.onFirstPersonScoreChange("aaa")
        viewModel.onSecondPersonScoreChange("1")
        viewModel.onFirstPersonNameChange("2")
        viewModel.onSecondPersonNameChange("aaa")

        verify {
            observerSaveButtonState.onChanged(false)
        }
    }

}
