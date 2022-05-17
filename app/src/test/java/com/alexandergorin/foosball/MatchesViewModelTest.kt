package com.alexandergorin.foosball

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Match
import com.alexandergorin.foosball.core.ResourceProvider
import com.alexandergorin.foosball.ui.matches.MatchState
import com.alexandergorin.foosball.ui.matches.MatchesViewModel
import com.alexandergorin.foosball.ui.matches.MatchesViewState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TITLE = "TITlE"

class MatchesViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val listOfMatches = emptyList<Match>()
    private val matchesState: List<MatchState>
        get() = listOfMatches.map {
            MatchState(
                it.id,
                it.firstPersonName,
                it.firstScore.toString(),
                it.secondPersonName,
                it.secondScore.toString()
            )
        }
    private val throwable = Throwable()

    private val repository: AppRepository = mockk()
    private val resourceProvider: ResourceProvider = mockk() {
        every { getString(R.string.app_name) } returns TITLE
    }

    private val observerState: Observer<MatchesViewState> = mockk(relaxed = true)
    private val observerAppBarTitle: Observer<String> = mockk(relaxed = true)
    private lateinit var viewModel: MatchesViewModel

    @Before
    fun before() {
        viewModel = MatchesViewModel(
            repository,
            resourceProvider,
            Schedulers.trampoline()
        )
        viewModel.matchesViewState.observeForever(observerState)
        viewModel.appBarTitle.observeForever(observerAppBarTitle)
    }

    @Test
    fun `when created then emits app bar title`() {
        verify {
            observerAppBarTitle.onChanged(TITLE)
        }
    }

    @Test
    fun `given repository emits list of matches when loadMatches called then emits loading and loaded states`() {
        every { repository.getMatches() } returns Flowable.just(listOfMatches)

        viewModel.loadMatches()

        verifyOrder {
            observerState.onChanged(MatchesViewState.Loading)
            observerState.onChanged(MatchesViewState.Loaded(matchesState))
        }
    }

    @Test
    fun `given repository emits error when loadMatches called then emits loading and error states`() {
        every { repository.getMatches() } returns Flowable.error(throwable)

        viewModel.loadMatches()

        verifyOrder {
            observerState.onChanged(MatchesViewState.Loading)
            observerState.onChanged(MatchesViewState.Error)
        }
    }

}
