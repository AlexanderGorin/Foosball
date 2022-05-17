package com.alexandergorin.foosball

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.alexandergorin.domain.AppRepository
import com.alexandergorin.domain.Ranking
import com.alexandergorin.foosball.core.ResourceProvider
import com.alexandergorin.foosball.ui.rankings.RankingState
import com.alexandergorin.foosball.ui.rankings.RankingsViewModel
import com.alexandergorin.foosball.ui.rankings.RankingsViewState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TITLE = "TITlE"
private const val GAMES_PLAYED = "GAMES_PLAYED"
private const val GAMES_WON = "GAMES_WON"

class RankingsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val listOfRankings = emptyList<Ranking>()
    private val rankingState: List<RankingState>
        get() = listOfRankings.map {
            RankingState(
                it.id.toString(),
                it.personName,
                GAMES_PLAYED,
                GAMES_WON
            )
        }

    private val throwable = Throwable()

    private val repository: AppRepository = mockk() {
        every { getRankings() } returns Flowable.just(listOfRankings)
    }
    private val resourceProvider: ResourceProvider = mockk() {
        every { getString(R.string.rankings) } returns TITLE
        every { getString(R.string.games_played) } returns GAMES_PLAYED
        every { getString(R.string.games_won) } returns GAMES_WON
    }

    private val observerState: Observer<RankingsViewState> = mockk(relaxed = true)
    private val observerAppBarTitle: Observer<String> = mockk(relaxed = true)
    private lateinit var viewModel: RankingsViewModel

    @Before
    fun before() {
        viewModel = RankingsViewModel(
            repository,
            resourceProvider,
            Schedulers.trampoline()
        )
        viewModel.rankingsViewState.observeForever(observerState)
        viewModel.appBarTitle.observeForever(observerAppBarTitle)
    }

    @Test
    fun `when created then emits app bar title`() {
        verify {
            observerAppBarTitle.onChanged(TITLE)
        }
    }

    @Test
    fun `given repository emits list of rankings when loadRankings called then emits loading and loaded states`() {
        every { repository.getRankings() } returns Flowable.just(listOfRankings)

        viewModel.loadRankings()

        verifyOrder {
            observerState.onChanged(RankingsViewState.Loading)
            observerState.onChanged(RankingsViewState.Loaded(rankingState))
        }
    }

    @Test
    fun `given repository emits error when loadRankings called then emits loading and error states`() {
        every { repository.getRankings() } returns Flowable.error(throwable)

        viewModel.loadRankings()

        verifyOrder {
            observerState.onChanged(RankingsViewState.Loading)
            observerState.onChanged(RankingsViewState.Error)
        }
    }

}
